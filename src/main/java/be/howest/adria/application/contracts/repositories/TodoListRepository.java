package be.howest.adria.application.contracts.repositories;

import be.howest.adria.domain.taskly.TodoList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoListRepository {
  List<TodoList> allTrashedTodoLists(UUID userId);

  Optional<TodoList> byId(UUID id);

  Optional<TodoList> byIdAndUserId(UUID id, UUID userId);

  void save(TodoList todoList);

  void remove(TodoList todoList);
}
