package be.howest.adria.application.shared;

import org.junit.jupiter.api.Assertions;

import be.howest.adria.application.contracts.EventNotifier;

public class MockEventNotifier implements EventNotifier {
    private String lastMessage;

    @Override
    public void publish(String message) {
        lastMessage = message;
    }

    public void assertLastMessage(String expectedMessage) {
        if (!lastMessage.equals(expectedMessage))
            Assertions.fail(String.format("Expected message: %s, but got: %s", expectedMessage, lastMessage));
    }
    
}
