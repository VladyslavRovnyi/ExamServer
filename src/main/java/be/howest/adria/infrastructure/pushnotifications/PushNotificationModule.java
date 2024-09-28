package be.howest.adria.infrastructure.pushnotifications;

import java.security.KeyPair;

import be.howest.adria.infrastructure.pushnotifications.database.SubscriptionDb;
import be.howest.adria.infrastructure.pushnotifications.database.SubscriptionDbImpl;
import be.howest.adria.infrastructure.pushnotifications.server.PushServer;
import be.howest.adria.infrastructure.pushnotifications.server.VapidKeys;
import be.howest.adria.infrastructure.shared.utils.Config;
import nl.martijndwars.webpush.PushService;

public class PushNotificationModule {

    public static void init(Config config) {
        String vapidKeysFile = config.readSetting("pushnotifications.vapidkeys.path");
        KeyPair vapidKeys = VapidKeys.generate(vapidKeysFile);
        PushService pushService = new PushService()
                .setSubject("mailto:your-email@example.com") // Challenge: this should be a configuration value
                .setPublicKey(vapidKeys.getPublic())
                .setPrivateKey(vapidKeys.getPrivate());

        SubscriptionDb subscriptionDb = 
            new SubscriptionDbImpl(config.readSetting("pushnotifications.subscriptions.db.path"));

        PushServer.initialize(pushService, subscriptionDb);
    }

    private PushNotificationModule() {}
}
