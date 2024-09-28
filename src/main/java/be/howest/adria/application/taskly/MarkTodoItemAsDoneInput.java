package be.howest.adria.application.taskly;

import java.util.UUID;

public class MarkTodoItemAsDoneInput {
  public final UUID todoListId;
  public final UUID todoItemId;

  public MarkTodoItemAsDoneInput(
    UUID todoListId,
    UUID todoItemId
  ) {
    this.todoListId = todoListId;
    this.todoItemId = todoItemId;
  }
}
