package be.howest.adria.application.taskly;

import java.util.UUID;

public class DelayDeadlineForTodoItemInput {
    public final UUID todoListId;
    public final String deadlineDate;
    public final UUID todoItemId;

    public DelayDeadlineForTodoItemInput(UUID todoListId, UUID todoItemId, String deadlineDate) {
        this.todoListId = todoListId;
        this.deadlineDate = deadlineDate;
        this.todoItemId = todoItemId;
    }
}
