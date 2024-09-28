package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.TodoList;
import java.util.NoSuchElementException;

public class MarkTodoItemAsDone implements UseCase<MarkTodoItemAsDoneInput> {
  private final TodoListRepository todoListRepository;

  public MarkTodoItemAsDone(
      TodoListRepository todoListRepository) {
    this.todoListRepository = todoListRepository;
  }

  public void execute(MarkTodoItemAsDoneInput input) {
    TodoList todoList = todoListRepository.byId(input.todoListId)
        .orElseThrow(() -> new NoSuchElementException("TodoList not found"));

    todoList.markTodoItemAsDone(input.todoItemId);

    todoListRepository.save(todoList);
  }
}
