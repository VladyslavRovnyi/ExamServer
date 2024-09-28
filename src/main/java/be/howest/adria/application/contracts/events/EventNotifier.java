package be.howest.adria.application.contracts.events;

public interface EventNotifier {
    // We will limit the type of messages to String for simplicity
    public void publish(String message);
}
