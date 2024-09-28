package be.howest.adria.application.taskly;

import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.domain.exceptions.TasklyException;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkTodoItemAsDoneTests {

  @Test
  void markTodoItemAsDone_WithValidInput_MarksTodoItemAsDone() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MarkTodoItemAsDone markTodoItemAsDone = new MarkTodoItemAsDone(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoList.addTodoItem(todoItem);
    mockTodoListRepository.save(todoList);
    MarkTodoItemAsDoneInput input = new MarkTodoItemAsDoneInput(todoList.id(), todoItem.id());
    mockTodoListRepository.startTracking();

    // Act
    markTodoItemAsDone.execute(input);

    // Assert
    assertTrue(todoItem.isDone());
    assertTrue(mockTodoListRepository.saveWasCalled(todoList, 1));
  }

  @Test
  void markTodoItemAsDone_WithAlreadyDoneTodoItem_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MarkTodoItemAsDone markTodoItemAsDone = new MarkTodoItemAsDone(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoList.addTodoItem(todoItem);
    todoList.markTodoItemAsDone(todoItem.id());
    mockTodoListRepository.save(todoList);
    MarkTodoItemAsDoneInput input = new MarkTodoItemAsDoneInput(todoList.id(), todoItem.id());
    mockTodoListRepository.startTracking();

    // Assert
    assertThrows(TasklyException.class, () -> markTodoItemAsDone.execute(input));
  }

  @Test
  void markTodoItemAsDone_WithUnknownTodoList_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MarkTodoItemAsDoneInput input = new MarkTodoItemAsDoneInput(UUID.randomUUID(), UUID.randomUUID());
    MarkTodoItemAsDone markTodoItemAsDone = new MarkTodoItemAsDone(mockTodoListRepository);

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> markTodoItemAsDone.execute(input));
  }

  @Test
  void markTodoItemAsDone_WithUnknownTodoItem_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    MarkTodoItemAsDone markTodoItemAsDone = new MarkTodoItemAsDone(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoList.addTodoItem(todoItem);
    mockTodoListRepository.save(todoList);
    MarkTodoItemAsDoneInput input = new MarkTodoItemAsDoneInput(todoList.id(), UUID.randomUUID());
    mockTodoListRepository.startTracking();

    // Assert + Act
    assertThrows(NoSuchElementException.class, () -> markTodoItemAsDone.execute(input));
  }

}
