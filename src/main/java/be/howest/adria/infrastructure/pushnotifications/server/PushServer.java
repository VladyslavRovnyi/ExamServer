package be.howest.adria.infrastructure.pushnotifications.server;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import be.howest.adria.application.contracts.EventNotifier;
import be.howest.adria.infrastructure.pushnotifications.database.SubscriptionDb;
import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.security.GeneralSecurityException;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

import org.jose4j.lang.JoseException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

import java.util.logging.*;

public class PushServer implements EventNotifier {
    private static final Logger LOGGER = Logger.getLogger(PushServer.class.getName());
    private static PushServer instance;
    private SubscriptionDb subscriptionDb;
    private final PushService pushService;
    private Gson gson = new GsonBuilder().create();
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PushServer initialize(PushService pushService, SubscriptionDb subscriptionDb) {
        if (instance != null)
            return instance;

        instance = new PushServer(pushService, subscriptionDb);
        return instance;
    }

    public static PushServer instance() {
        if (instance == null)
            throw new IllegalStateException("PushServer is not initialized");
        return instance;
    }

    public void handleSubscription(RoutingContext ctx) {
        List<Subscription> subscriptions = subscriptionDb.getSubscriptions();
        Subscription subscription = gson.fromJson(ctx.body().asString(), Subscription.class);
        subscriptions.add(subscription);
        subscriptionDb.saveSubscriptions(subscriptions);
        ApiResult.created(ctx, subscription.endpoint);
    }

    @Override
    public void publish(String message) {
        subscriptionDb.getSubscriptions().forEach(subscription -> sendPushMessage(subscription, message));
    }

    public void sendPushMessage(Subscription subscription, String message) {
        try {
            Notification notification = new Notification(
                    subscription.endpoint,
                    subscription.p256dh,
                    subscription.auth,
                    message.getBytes());

            // Challenge: the message could be not delivered because the client unsubscribed.
            // Implement a mechanism to remove the subscription from the database if the message is not delivered.
            pushService.send(notification);
        } catch (IOException | JoseException | ExecutionException | InterruptedException | GeneralSecurityException e) {
            LOGGER.info("Failed to send push message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private PushServer(PushService pushService, SubscriptionDb subscriptionDb) {
        this.pushService = pushService;
        this.subscriptionDb = subscriptionDb;
    }

}
