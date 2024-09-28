package be.howest.adria.application.taskly;

import java.util.UUID;

public class CreateUserOutput {
    public final UUID userId;

    public CreateUserOutput(UUID userId) {
        this.userId = userId;
    }
}
