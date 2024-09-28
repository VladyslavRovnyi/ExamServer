package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.exceptions.TasklyException;
import be.howest.adria.domain.taskly.TodoList;
import java.util.NoSuchElementException;

public class MoveTodoListToTrash implements UseCase<MoveTodoListToTrashInput> {

  private final TodoListRepository todoListRepository;

  public MoveTodoListToTrash(
      TodoListRepository todoListRepository) {
    this.todoListRepository = todoListRepository;
  }

  public void execute(MoveTodoListToTrashInput input) {
    TodoList todoList = todoListRepository.byId(input.todoListId)
        .orElseThrow(() -> new NoSuchElementException("TodoList not found"));

    if (!isSafeToRemove(todoList, input.forceDelete))
      throw new TasklyException("Cannot trash a todo list with unfinished items");

    todoList.moveToTrash();

    todoListRepository.save(todoList);
  }

  private boolean isSafeToRemove(TodoList todoList, boolean forceDelete) {
    return forceDelete || !todoList.hasUnfinishedItems();
  }
}
