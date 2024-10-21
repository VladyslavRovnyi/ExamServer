package be.howest.adria.infrastructure.pushnotifications.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class VapidKeys {
    private static final Logger LOGGER = Logger.getLogger(VapidKeys.class.getName());

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair load(String vapidKeysPath) {
        KeyPair keyPair = readKeyPair(vapidKeysPath);
        if (keyPair == null) {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
                keyPairGenerator.initialize(256);
                keyPair = keyPairGenerator.generateKeyPair();
                saveKeyPair(vapidKeysPath, keyPair);
            } catch (NoSuchAlgorithmException | IllegalStateException e) {
                LOGGER.severe("Failed to generate VAPID keys: " + e.getMessage());
                throw new IllegalArgumentException("Failed to generate VAPID keys", e);
            } catch (NoSuchProviderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return keyPair;

    }

    private static void saveKeyPair(String keyPairPath, KeyPair keyPair) {
        try (FileOutputStream fileOut = new FileOutputStream(keyPairPath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(keyPair);
        } catch (IOException e) {
            LOGGER.severe("Error saving VAPID keys: " + e.getMessage());
            throw new IllegalStateException("Error saving VAPID keys", e);
        }
    }

    private static KeyPair readKeyPair(String keyPairPath) {
        try (FileInputStream fileIn = new FileInputStream(keyPairPath);
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (KeyPair) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    private VapidKeys() {
    }
}
