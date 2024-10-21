package be.howest.adria.infrastructure.pushnotifications;

import static be.howest.adria.shared.TestConstants.API_URL_HEALTH_CHECK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import be.howest.adria.infrastructure.pushnotifications.server.PushServer;
import be.howest.adria.infrastructure.pushnotifications.server.Subscription;
import be.howest.adria.infrastructure.shared.utils.Config;
import be.howest.adria.shared.TestExampleRequests;
import be.howest.adria.shared.TestModels;
import be.howest.adria.shared.WebApiTestUtils;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import be.howest.adria.infrastructure.pushnotifications.database.SubscriptionDb;
import be.howest.adria.infrastructure.pushnotifications.database.SubscriptionDbImpl;

import java.nio.file.Files;
import java.nio.file.Paths;

class PushNotificationTests {

    private static MockPushService mockPushService;
    private String validAuth;
    private String validP256dh;

    @BeforeEach
    void setUp() throws IOException {
        Config config = new Config("/config/config.properties");
        SubscriptionDb subscriptionDb = new SubscriptionDbImpl(
                config.readSetting("pushnotifications.subscriptions.db.path"));
        mockPushService = new MockPushService();
        Files.deleteIfExists(Paths.get(config.readSetting("pushnotifications.vapidkeys.path")));
        PushServer.initialize(mockPushService, subscriptionDb);
        WebApiTestUtils.waitForServer(API_URL_HEALTH_CHECK);
    }

    @AfterEach
    void tearDown() {
        WebApiTestUtils.tearDownWebApi();
    }

    @Test
    void whenCreatingTodoList_shouldSendPushNotification() {
        // Arrange
        Gson gson = new Gson();
        generateClientKeys();

        // Subscribe to notifications endpoint
        Subscription subscription = Subscription.create(API_URL_HEALTH_CHECK, validP256dh, validAuth);
        String subscriptionJson = gson.toJson(subscription);
        HttpResponse<String> subscribeResponse = TestExampleRequests.subscribe(subscriptionJson);
        WebApiTestUtils.assertCreated(subscribeResponse);

        // Create valid user and todolist
        String createUserBody = TestModels.createUserRequestBodyDefault();
        HttpResponse<String> createUserResponse = TestExampleRequests.createUser(createUserBody);
        String userId = WebApiTestUtils.getResourceId(createUserResponse);
        String createTodoListBody = TestModels.createTodoListBodyRequest(userId, "My Push Notification Test List");

        // Act
        HttpResponse<String> response = TestExampleRequests.createTodoList(createTodoListBody);

        // Assert
        WebApiTestUtils.assertCreated(response);

        // Verify notification details
        assertTrue(mockPushService.hasSentNotification());
        Notification sentNotification = mockPushService.getLastNotification();
        assertEquals(subscription.endpoint, sentNotification.getEndpoint());
        String message = new String(sentNotification.getPayload());
        assertTrue(message.contains("TodoListCreated"));
        assertTrue(message.contains("My Push Notification Test List"));
    }

    private void generateClientKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
            keyPairGenerator.initialize(256, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
            byte[] uncompressedPoint = ecPublicKey.getQ().getEncoded(false); // 'false' for uncompressed
            String p256dh = Base64.getUrlEncoder().withoutPadding().encodeToString(uncompressedPoint);

            byte[] authSecret = new byte[16]; // 16 bytes = 128 bits
            new SecureRandom().nextBytes(authSecret);
            String auth = Base64.getUrlEncoder().withoutPadding().encodeToString(authSecret);

            validP256dh = p256dh;
            validAuth = auth;

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    static class MockPushService extends PushService {
        private final List<Notification> sentNotifications = new CopyOnWriteArrayList<>();

        @Override
        public org.apache.http.HttpResponse send(Notification notification)
                throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
            sentNotifications.add(notification);
            return null;
        }

        public boolean hasSentNotification() {
            return !sentNotifications.isEmpty();
        }

        public Notification getLastNotification() {
            return sentNotifications.get(sentNotifications.size() - 1);
        }
    }
}
