package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.shared.MockAddTodoItemOutputPort;
import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;
import be.howest.adria.domain.taskly.Deadline.Status;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddTodoItemTests {

  @Test
  void addTodoItem_WithValidInput_CreatesTodoItem() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MockAddTodoItemOutputPort mockAddTodoItemOutputPort = new MockAddTodoItemOutputPort();
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockTodoListRepository.save(todoList);
    UseCase<AddTodoItemInput> addTodoItem = new AddTodoItem(mockTodoListRepository, mockAddTodoItemOutputPort);
    AddTodoItemInput input = new AddTodoItemInput(
        todoList.id(),
        "My Todo Item");
    mockTodoListRepository.startTracking();

    // Act
    addTodoItem.execute(input);

    // Assert
    assertTodoItemAdded(todoList, input.description);
    assertTrue(mockTodoListRepository.saveWasCalled(todoList, 1));
  }

  @Test
  void addTodoItem_WithUnknownTodoList_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MockAddTodoItemOutputPort mockAddTodoItemOutputPort = new MockAddTodoItemOutputPort();
    UseCase<AddTodoItemInput> addTodoItem = new AddTodoItem(mockTodoListRepository, mockAddTodoItemOutputPort);
    AddTodoItemInput input = new AddTodoItemInput(
        UUID.randomUUID(),
        "My Todo Item");

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> addTodoItem.execute(input));
  }

  @Test
  void addTodoItem_WithEmptyDescription_ThrowsException() {
    // Arrange
    MockAddTodoItemOutputPort mockAddTodoItemOutputPort = new MockAddTodoItemOutputPort();
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockTodoListRepository.save(todoList);
    UseCase<AddTodoItemInput> addTodoItem = new AddTodoItem(mockTodoListRepository, mockAddTodoItemOutputPort);
    AddTodoItemInput input = new AddTodoItemInput(
        todoList.id(),
        "");

    // Act + Assert
    assertThrows(IllegalArgumentException.class, () -> addTodoItem.execute(input));
  }

  @Test
  void addTodoItem_WithNullDescription_ThrowsException() {
    // Arrange
    MockAddTodoItemOutputPort mockAddTodoItemOutputPort = new MockAddTodoItemOutputPort();
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockTodoListRepository.save(todoList);
    UseCase<AddTodoItemInput> addTodoItem = new AddTodoItem(mockTodoListRepository, mockAddTodoItemOutputPort);
    AddTodoItemInput input = new AddTodoItemInput(
        todoList.id(),
        null);

    // Act + Assert
    assertThrows(IllegalArgumentException.class, () -> addTodoItem.execute(input));
  }

  @Test
  void addTodoItem_WithoutDeadline_CreatesTodoItemWithUnlimitedDeadline() {
    // Arrange
    MockAddTodoItemOutputPort mockAddTodoItemOutputPort = new MockAddTodoItemOutputPort();
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockTodoListRepository.save(todoList);
    UseCase<AddTodoItemInput> addTodoItem = new AddTodoItem(mockTodoListRepository, mockAddTodoItemOutputPort);
    AddTodoItemInput input = new AddTodoItemInput(
        todoList.id(),
        "My Todo Item");
    mockTodoListRepository.startTracking();

    // Act
    addTodoItem.execute(input);

    // Assert
    assertEquals(Status.UNLIMITED, todoList.items().get(0).deadline().status());
    assertTodoItemAdded(todoList, input.description);
    assertTrue(mockTodoListRepository.saveWasCalled(todoList, 1));
  }

  private void assertTodoItemAdded(TodoList todoList, String description) {
    assertTrue(todoList.items().stream().anyMatch(item -> item.description().equals(description)));
  }
}
