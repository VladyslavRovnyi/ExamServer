package be.howest.adria.application.taskly;

import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmptyTrashTests {
  @Test
  void emptyTrash_WithValidInput_EmptiesTrash() {
    // Arrange
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    EmptyTrash emptyTrash = new EmptyTrash(todoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList = TodoList.create(user, "My Todo List");
    todoList.moveToTrash();
    UUID todoListId = todoList.id();
    todoListRepository.save(todoList);
    todoListRepository.startTracking();

    // Act
    emptyTrash.execute(new EmptyTrashInput(user.id()));

    // Assert
    assertTrue(todoListRepository.byId(todoListId).isEmpty());
  }

  @Test
  void emptyTrash_WithUnknownUser_DoesNothing() {
    // Arrange
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    EmptyTrash emptyTrash = new EmptyTrash(todoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    EmptyTrashInput input = new EmptyTrashInput(user.id());

    // Act
    emptyTrash.execute(input);

    // Assert
    assertTrue(todoListRepository.removeWasCalled(null, 0));
  }

  @Test
  void emptyTrash_WithNoTrashedTodoLists_DoesNothing() {
    // Arrange
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    EmptyTrash emptyTrash = new EmptyTrash(todoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    EmptyTrashInput input = new EmptyTrashInput(user.id());

    todoListRepository.startTracking();

    // Act
    emptyTrash.execute(input);

    // Assert
    assertTrue(todoListRepository.removeWasCalled(null, 0));
  }

  @Test
  void emptyTrash_WithMultipleTrashedTodoLists_EmptiesTrashForAllTodoLists() {
    // Arrange
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    EmptyTrash emptyTrash = new EmptyTrash(todoListRepository);
    User user = User.create(UUID.randomUUID(), "john");
    TodoList todoList1 = TodoList.create(user, "My Todo List 1");
    TodoList todoList2 = TodoList.create(user, "My Todo List 2");
    todoList1.moveToTrash();
    todoList2.moveToTrash();
    todoListRepository.save(todoList1);
    todoListRepository.save(todoList2);
    UUID todoList1Id = todoList1.id();
    UUID todoList2Id = todoList2.id();

    todoListRepository.startTracking();

    // Act
    emptyTrash.execute(new EmptyTrashInput(user.id()));

    // Assert
    assertTrue(todoListRepository.byId(todoList1Id).isEmpty());
    assertTrue(todoListRepository.byId(todoList2Id).isEmpty());
  }
}
