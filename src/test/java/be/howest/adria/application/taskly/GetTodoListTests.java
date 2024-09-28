package be.howest.adria.application.taskly;

import be.howest.adria.application.shared.MockTodoListByIdAndUserIdOutputPort;
import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetTodoListTests {
  @Test
  void GetTodoList_WithValidInput_ShouldReturnTodoList() {
    // Arrange
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    MockTodoListByIdAndUserIdOutputPort outputPort = new MockTodoListByIdAndUserIdOutputPort();
    TodoList todoList = buildValidTodoList();
    todoListRepository.save(todoList);

    TodoListByIdAndUserIdInput getTodoListInput = new TodoListByIdAndUserIdInput(todoList.id(), todoList.user().id());
    TodoListByIdAndUserId todoListByIdAndUserId = new TodoListByIdAndUserId(todoListRepository, outputPort);

    // Act
    todoListByIdAndUserId.execute(getTodoListInput);

    // Assert
    assertExpectedTodoListData(todoList, outputPort.output.todoList);
  }

  @Test
  void GetTodoList_WithInvalidInput_ThrowsNoSuchElementException() {
    // Arrange
    MockTodoListByIdAndUserIdOutputPort outputPort = new MockTodoListByIdAndUserIdOutputPort();
    TodoList todoList = buildValidTodoList();
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    TodoListByIdAndUserIdInput getTodoListInput = new TodoListByIdAndUserIdInput(todoList.id(), todoList.user().id());
    TodoListByIdAndUserId todoListByIdAndUserId = new TodoListByIdAndUserId(todoListRepository, outputPort);

    // Act & Assert
    assertThrows(NoSuchElementException.class, () -> todoListByIdAndUserId.execute(getTodoListInput));
  }

  private void assertExpectedTodoListData(TodoList expected,
      TodoList actual) {
    assertEquals(expected.id(), actual.id());
    assertEquals(expected.title(), actual.title());
    assertEquals(expected.isInTrash(), actual.isInTrash());
    assertEquals(expected.items().size(), actual.items().size());
    for (int i = 0; i < expected.items().size(); i++) {
      TodoItem expectedTodoItem = expected.items().get(i);
      TodoItem actualTodoItem = actual.items().get(i);
      assertEquals(expectedTodoItem.id(), actualTodoItem.id());
      assertEquals(expectedTodoItem.description(), actualTodoItem.description());
      assertEquals(expectedTodoItem.deadline().toString(), actualTodoItem.deadline().toString());
      assertEquals(expectedTodoItem.isDone(), actualTodoItem.isDone());
    }
  }

  private TodoList buildValidTodoList() {
    User user = User.create("john");
    List<TodoItem> todoItems = Arrays.asList(
        TodoItem.create("Buy milk", Deadline.create("10-10-2050")),
        TodoItem.create("Buy bread", Deadline.create("31-12-2021")));

    TodoList todoList = TodoList.create(user, "Groceries");
    todoItems.forEach(todoList::addTodoItem);
    return todoList;
  }
}
