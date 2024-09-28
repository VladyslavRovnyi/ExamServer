package be.howest.adria.application.taskly;

import java.util.UUID;

public class CreateTodoListOutput {
    public final UUID todoListId;

    public CreateTodoListOutput(UUID todoListId) {
        this.todoListId = todoListId;
    }
}
