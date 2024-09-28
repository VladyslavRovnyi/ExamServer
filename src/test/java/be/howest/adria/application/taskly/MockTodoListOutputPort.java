package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.domain.taskly.TodoList;

public class MockTodoListOutputPort implements OutputPort<TodoList> {
  private TodoList todoList;

  public TodoList todoList() {
    return todoList;
  }

  @Override
  public void present(TodoList data) {
    this.todoList = data;
  }
}
