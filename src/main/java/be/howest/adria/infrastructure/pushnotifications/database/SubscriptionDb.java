package be.howest.adria.infrastructure.pushnotifications.database;

import java.util.List;

import be.howest.adria.infrastructure.pushnotifications.server.Subscription;

public interface SubscriptionDb {
    void saveSubscriptions(List<Subscription> subscriptions);
    List<Subscription> getSubscriptions();
}
