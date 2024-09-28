package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.UseCase;

public class EmptyTrash implements UseCase<EmptyTrashInput> {
  private final TodoListRepository todoListRepository;

  public EmptyTrash(
      TodoListRepository todoListRepository) {
    this.todoListRepository = todoListRepository;
  }

  public void execute(EmptyTrashInput input) {
    todoListRepository
        .allTrashedTodoLists(input.userId)
        .forEach(todoListRepository::remove);
  }
}
