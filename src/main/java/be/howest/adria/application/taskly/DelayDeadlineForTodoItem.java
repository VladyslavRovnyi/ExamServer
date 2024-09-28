package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoList;
import java.util.NoSuchElementException;

public class DelayDeadlineForTodoItem implements UseCase<DelayDeadlineForTodoItemInput> {
  private final TodoListRepository todoListRepository;

  public DelayDeadlineForTodoItem(TodoListRepository todoListRepository) {
    this.todoListRepository = todoListRepository;
  }

  public void execute(DelayDeadlineForTodoItemInput input) {
    TodoList todoList = todoListRepository.byId(input.todoListId)
        .orElseThrow(() -> new NoSuchElementException("TodoList not found"));

    Deadline deadline = Deadline.create(input.deadlineDate);
    todoList.delayDeadline(input.todoItemId, deadline);

    todoListRepository.save(todoList);
  }
}
