package be.howest.adria.application.taskly;

import java.util.UUID;

public class CreateTodoListInput {
    public final UUID userId;
    public final String title;

    public CreateTodoListInput(UUID userId, String title) {
        this.userId = userId;
        this.title = title;
    }
}
