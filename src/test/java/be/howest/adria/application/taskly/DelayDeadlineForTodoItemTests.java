package be.howest.adria.application.taskly;

import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DelayDeadlineForTodoItemTests {
  @Test
  void delayDeadlineForTodoItem_WithValidInput_DelaysDeadlineForTodoItem() {
    // Arrange
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate currentDate = LocalDate.now();
    LocalDate futureDate = currentDate.plusDays(1);
    String currentDateString = currentDate.format(formatter);
    String futureDateString = futureDate.format(formatter);
    Deadline currentDeadline = Deadline.create(currentDateString);

    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    DelayDeadlineForTodoItem delayDeadlineForTodoItem = new DelayDeadlineForTodoItem(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", currentDeadline);
    DelayDeadlineForTodoItemInput input = new DelayDeadlineForTodoItemInput(todoList.id(), todoItem.id(), futureDateString);
    todoList.addTodoItem(todoItem);
    mockTodoListRepository.save(todoList);

    mockTodoListRepository.startTracking();

    // Act
    delayDeadlineForTodoItem.execute(input);

    // Assert
    assertEquals(todoItem.deadline().toString(), futureDate.toString());
    assertTrue(mockTodoListRepository.saveWasCalled(todoList, 1));
  }

  @Test
  void delayDeadlineForTodoItem_WithUnknownTodoList_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    DelayDeadlineForTodoItem delayDeadlineForTodoItem = new DelayDeadlineForTodoItem(mockTodoListRepository);
    DelayDeadlineForTodoItemInput input = new DelayDeadlineForTodoItemInput(UUID.randomUUID(), UUID.randomUUID(), "01-01-2022");

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> delayDeadlineForTodoItem.execute(input));
  }

  @Test
  void delayDeadlineForTodoItem_WithUnknownTodoItem_ThrowsException() {
    // Arrange
    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    DelayDeadlineForTodoItem delayDeadlineForTodoItem = new DelayDeadlineForTodoItem(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockTodoListRepository.save(todoList);
    DelayDeadlineForTodoItemInput input = new DelayDeadlineForTodoItemInput(todoList.id(), UUID.randomUUID(), "01-01-2022");

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> delayDeadlineForTodoItem.execute(input));
  }

  @Test
  void delayDeadlineForTodoItem_WithNewDateBeforeCurrentDeadline_ThrowsException() {
    // Arrange
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = currentDate.minusDays(1);
    String currentDateString = currentDate.format(formatter);
    String futureDateString = pastDate.format(formatter);
    Deadline currentDeadline = Deadline.create(currentDateString);

    MockTodoListRepository mockTodoListRepository = new MockTodoListRepository();
    DelayDeadlineForTodoItem delayDeadlineForTodoItem = new DelayDeadlineForTodoItem(mockTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", currentDeadline);
    DelayDeadlineForTodoItemInput input = new DelayDeadlineForTodoItemInput(todoList.id(), todoItem.id(), futureDateString);
    todoList.addTodoItem(todoItem);
    mockTodoListRepository.save(todoList);

    mockTodoListRepository.startTracking();

    // Act + Assert
    assertThrows(IllegalArgumentException.class, () -> delayDeadlineForTodoItem.execute(input));
  }
}