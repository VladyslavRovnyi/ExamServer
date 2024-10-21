package be.howest.adria.infrastructure.pushnotifications.server;

import java.util.Base64;

public class Subscription implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public final String endpoint;
    public final String p256dh;
    public final String auth;

    private Subscription(String endpoint, String p256dh, String auth) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    public static Subscription create(String endpoint, String p256dh, String auth) {
        Subscription s = new Subscription(endpoint, p256dh, auth);
        s.validate();

        return s;
    }

    @Override
    public int hashCode() {
        return endpoint.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Subscription other = (Subscription) obj;
        return this.endpoint.equals(other.endpoint);

    }

    public void validate() {
        if (endpoint == null || endpoint.isEmpty())
            throw new IllegalStateException("Endpoint is required");

        if (p256dh == null || p256dh.isEmpty())
            throw new IllegalStateException("P256dh is required");

        if (auth == null || auth.isEmpty())
            throw new IllegalStateException("Auth is required");

        try {
            new java.net.URL(endpoint);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalStateException("Endpoint is not a valid URL", e);
        }

        byte[] p256dhBytes;
        try {
            p256dhBytes = base64UrlDecode(p256dh);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("p256dh is not a valid Base64URL-encoded string", e);
        }

        byte[] authBytes;
        try {
            authBytes = base64UrlDecode(auth);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Auth is not a valid Base64URL-encoded string", e);
        }

        if (authBytes.length != 16)
            throw new IllegalStateException("Auth should be 16 bytes after decoding");

        if (p256dhBytes.length != 65 || p256dhBytes[0] != 0x04)
            throw new IllegalStateException("p256dh should be 65 bytes and start with 0x04");
    }

    private byte[] base64UrlDecode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }
}
