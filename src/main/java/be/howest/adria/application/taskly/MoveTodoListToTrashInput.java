package be.howest.adria.application.taskly;

import java.util.UUID;

public class MoveTodoListToTrashInput {
  public final UUID todoListId;
  public final boolean forceDelete;

  public MoveTodoListToTrashInput(UUID id) {
    this(id, false);
  }

  public MoveTodoListToTrashInput(UUID id, boolean forceDelete) {
    this.todoListId = id;
    this.forceDelete = forceDelete;
  }
}
