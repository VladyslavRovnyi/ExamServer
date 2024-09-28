package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.shared.MockCreateTodoListOutputPort;
import be.howest.adria.application.shared.MockEventNotifier;
import be.howest.adria.application.shared.MockTodoListRepository;
import be.howest.adria.application.shared.MockUserRepository;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTodoListTests {
  @Test
  void createTodoList_WithValidInput_CreatesTodoList() {
    // Arrange
    MockUserRepository userRepository = new MockUserRepository();
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    MockCreateTodoListOutputPort outputPort = new MockCreateTodoListOutputPort();
    MockEventNotifier eventNotifier = new MockEventNotifier();
    UseCase<CreateTodoListInput> createTodoList = new CreateTodoList(
      userRepository,
      todoListRepository,
      eventNotifier,
      outputPort);
    CreateTodoListInput input = new CreateTodoListInput(
      UUID.randomUUID(),
      "My Todo List"
    );

    // Act
    createTodoList.execute(input);

    // Assert
    assertTrue(todoListRepository.assertTodoListSaved(input.userId, input.title));
    outputPort.assertPresented();
  }

  @Test
  void createTodoList_WithUnknownUser_ThrowsException() {
    // Arrange
    boolean byIdIsFailing = true;
    MockCreateTodoListOutputPort outputPort = new MockCreateTodoListOutputPort();
    MockUserRepository userRepository = new MockUserRepository(byIdIsFailing);
    MockTodoListRepository todoListRepository = new MockTodoListRepository();
    MockEventNotifier eventNotifier = new MockEventNotifier();
    UseCase<CreateTodoListInput> createTodoList = new CreateTodoList(
      userRepository,
      todoListRepository,
      eventNotifier,
      outputPort
    );
    CreateTodoListInput input = new CreateTodoListInput(
      UUID.randomUUID(),
      "My Todo List"
    );

    // Act + Assert
    assertThrows(NoSuchElementException.class, () -> createTodoList.execute(input));
  }
}
