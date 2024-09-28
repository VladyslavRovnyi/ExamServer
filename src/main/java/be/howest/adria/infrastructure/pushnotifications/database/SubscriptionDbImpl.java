package be.howest.adria.infrastructure.pushnotifications.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import be.howest.adria.infrastructure.pushnotifications.server.Subscription;

public class SubscriptionDbImpl implements SubscriptionDb {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionDbImpl.class.getName());
    private final String dbPath;

    public SubscriptionDbImpl(String dbPath) {
        this.dbPath = dbPath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Subscription> getSubscriptions() {
        try (FileInputStream fileIn = new FileInputStream(dbPath);
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (List<Subscription>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Error loading subscriptions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void saveSubscriptions(List<Subscription> subscriptions) {
        try (FileOutputStream fileOut = new FileOutputStream(dbPath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(subscriptions);
        } catch (IOException e) {
            LOGGER.severe("Error saving subscriptions: " + e.getMessage());
        }
    }

}
