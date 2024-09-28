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

class MoveTodoListToTrashTests {
  @Test
  void removeTodoList_WithAllItemsDone_RemovesTodoList() {
    // Arrange
    MockTodoListRepository mockedTodoListRepository = new MockTodoListRepository();
    MoveTodoListToTrash moveTodoListToTrash = new MoveTodoListToTrash(mockedTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoItem.markAsDone();
    todoList.addTodoItem(todoItem);
    mockedTodoListRepository.save(todoList);
    MoveTodoListToTrashInput input = new MoveTodoListToTrashInput(todoList.id());
    mockedTodoListRepository.startTracking();

    // Act
    moveTodoListToTrash.execute(input);

    // Assert
    assertTrue(todoList.isInTrash());
  }

  @Test
  void removeTodoList_WithNotAllItemsDone_ThrowsException() {
    // Arrange
    MockTodoListRepository mockedTodoListRepository = new MockTodoListRepository();
    MoveTodoListToTrash moveTodoListToTrash = new MoveTodoListToTrash(mockedTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoList.addTodoItem(todoItem);
    mockedTodoListRepository.save(todoList);
    MoveTodoListToTrashInput input = new MoveTodoListToTrashInput(todoList.id());

    // Act + Assert
    assertThrows(TasklyException.class, () -> moveTodoListToTrash.execute(input));
  }

  @Test
  void removeTodoList_WithUnknownTodoList_ThrowsException() {
    // Arrange
    MockTodoListRepository mockedTodoListRepository = new MockTodoListRepository();
    MoveTodoListToTrash moveTodoListToTrash = new MoveTodoListToTrash(mockedTodoListRepository);
    MoveTodoListToTrashInput input = new MoveTodoListToTrashInput(UUID.randomUUID());

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> moveTodoListToTrash.execute(input));
  }

  @Test
  void removeTodoList_WithEmptyTodoList_RemovesTodoList() {
    // Arrange
    MockTodoListRepository mockedTodoListRepository = new MockTodoListRepository();
    MoveTodoListToTrash moveTodoListToTrash = new MoveTodoListToTrash(mockedTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    mockedTodoListRepository.save(todoList);
    MoveTodoListToTrashInput input = new MoveTodoListToTrashInput(todoList.id());
    mockedTodoListRepository.startTracking();

    // Act
    moveTodoListToTrash.execute(input);

    // Assert
    assertTrue(todoList.isInTrash());
  }

  @Test
  void removeTodoList_ForceRemoveTodoList_RemovesTodoList() {
    // Arrange
    MockTodoListRepository mockedTodoListRepository = new MockTodoListRepository();
    MoveTodoListToTrash moveTodoListToTrash = new MoveTodoListToTrash(mockedTodoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    TodoItem todoItem = TodoItem.create("My Todo Item", Deadline.UNLIMITED);
    todoItem.markAsDone();
    todoList.addTodoItem(todoItem);
    mockedTodoListRepository.save(todoList);
    MoveTodoListToTrashInput input = new MoveTodoListToTrashInput(todoList.id(), true);
    mockedTodoListRepository.startTracking();

    // Act
    moveTodoListToTrash.execute(input);

    // Assert
    assertTrue(todoList.isInTrash());
  }
}
