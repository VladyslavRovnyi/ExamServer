package be.howest.adria.application.taskly;

import java.util.UUID;

public class AddTodoItemInput {
  public final UUID todoListId;
  public final String description;
  public final String deadlineDate;

  public AddTodoItemInput(UUID todoListId, String description) {
    this(todoListId, description, "");
  }

  public AddTodoItemInput(UUID todoListId, String description, String deadlineDate) {
    this.todoListId = todoListId;
    this.description = description;
    this.deadlineDate = deadlineDate;
  }
}
