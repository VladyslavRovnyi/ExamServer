package be.howest.adria.application.taskly;

import java.util.UUID;

public class TodoListByIdAndUserIdInput {
  public final UUID todoListId;
  public final UUID userId;

  public TodoListByIdAndUserIdInput(UUID todoListId, UUID userId) {
    this.todoListId = todoListId;
    this.userId = userId;
  }
}
