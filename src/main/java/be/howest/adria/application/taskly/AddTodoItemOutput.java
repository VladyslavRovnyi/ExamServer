package be.howest.adria.application.taskly;

import java.util.UUID;

public class AddTodoItemOutput {
    public final UUID todoItemId;
    public final UUID todoListId;

    public AddTodoItemOutput(UUID todoListId, UUID todoItemId) {
        this.todoItemId = todoItemId;
        this.todoListId = todoListId;
    }
}
