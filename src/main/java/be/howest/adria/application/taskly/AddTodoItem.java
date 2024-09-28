package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import java.util.NoSuchElementException;

public class AddTodoItem implements UseCase<AddTodoItemInput> {

  private final TodoListRepository todoListRepository;
  private final OutputPort<AddTodoItemOutput> outputPort;

  public AddTodoItem(
      TodoListRepository todoListRepository,
      OutputPort<AddTodoItemOutput> outputPort) {
    this.todoListRepository = todoListRepository;
    this.outputPort = outputPort;
  }

  public void execute(AddTodoItemInput input) {
    TodoList todoList = todoListRepository.byId(input.todoListId)
        .orElseThrow(() -> new NoSuchElementException("TodoList not found"));

    Deadline deadline = Deadline.create(input.deadlineDate);
    TodoItem item = TodoItem.create(input.description, deadline);
    todoList.addTodoItem(item);

    todoListRepository.save(todoList);

    outputPort.present(new AddTodoItemOutput(todoList.id(), item.id()));
  }
}
