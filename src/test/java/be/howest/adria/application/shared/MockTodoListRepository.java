package be.howest.adria.application.shared;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.domain.taskly.TodoList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockTodoListRepository implements TodoListRepository {
  private final List<TodoList> todoLists = new ArrayList<>();
  private final List<UUID> savedTodoLists = new ArrayList<>();
  private final List<UUID> hardRemovedTodoLists = new ArrayList<>();

  public boolean assertTodoListSaved(UUID userId, String title) {
    return todoLists
      .stream()
      .anyMatch(todoList -> todoList.title().equals(title) && todoList.user().id().equals(userId));
  }

  @Override
  public Optional<TodoList> byId(UUID id) {
    return todoLists
      .stream()
      .filter(todoList -> todoList.id().equals(id))
      .findFirst();
  }

  @Override
  public void save(TodoList todoList) {
    todoLists.add(todoList);
    savedTodoLists.add(todoList.id());
  }

  @Override
  public void remove(TodoList todoList) {
    todoLists.remove(todoList);
    hardRemovedTodoLists.add(todoList.id());
  }

  public boolean saveWasCalled(TodoList list, int times) {
    return savedTodoLists
      .stream()
      .filter(id -> id.equals(list.id()))
      .count() == times;
  }

  public boolean removeWasCalled(TodoList list, int times) {
    return hardRemovedTodoLists
      .stream()
      .filter(id -> id.equals(list.id()))
      .count() == times;
  }

  public void startTracking() {
    savedTodoLists.clear();
    hardRemovedTodoLists.clear();
  }

  @Override
  public List<TodoList> allTrashedTodoLists(UUID userId) {
    return todoLists.stream()
      .filter(todoList -> todoList.user().id().equals(userId) && todoList.isInTrash())
      .toList();
  }

  @Override
  public Optional<TodoList> byIdAndUserId(UUID id, UUID userId) {
    return todoLists
      .stream()
      .filter(todoList -> todoList.id().equals(id) && todoList.user().id().equals(userId))
      .findFirst();
  }
}
