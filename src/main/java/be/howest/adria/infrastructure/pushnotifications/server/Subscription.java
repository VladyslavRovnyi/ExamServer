package be.howest.adria.infrastructure.pushnotifications.server;

public class Subscription implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public final String endpoint;
    public final String p256dh;
    public final String auth;

    public Subscription(String endpoint, String p256dh, String auth) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
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
}
