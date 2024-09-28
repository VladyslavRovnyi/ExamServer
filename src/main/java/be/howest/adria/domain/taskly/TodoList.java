package be.howest.adria.domain.taskly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import be.howest.adria.domain.exceptions.TasklyException;

public class TodoList {

  private final UUID id;
  private final String title;
  private final User user;
  private final List<TodoItem> items = new ArrayList<>();
  private boolean isInTrash;

  public static TodoList create(
      UUID id,
      User user,
      String title,
      boolean isInTrash,
      List<TodoItem> items) {
    return new TodoList(id, user, title, isInTrash, items);
  }

  public static TodoList create(
      User user,
      String title) {
    UUID id = UUID.randomUUID();
    boolean isInTrash = false;
    List<TodoItem> items = new ArrayList<>();
    return create(id, user, title, isInTrash, items);
  }

  private TodoList(
      UUID id,
      User user,
      String title,
      boolean isInTrash,
      List<TodoItem> items) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.isInTrash = isInTrash;
    this.items.addAll(items);
  }

  public void addTodoItem(TodoItem item) {
    items.add(item);
  }

  public List<TodoItem> items() {
    return Collections.unmodifiableList(items);
  }

  public UUID id() {
    return id;
  }

  public String title() {
    return title;
  }

  public User user() {
    return user;
  }

  public void markTodoItemAsDone(UUID itemId) {
    TodoItem item = items
        .stream()
        .filter(todoItem -> todoItem.id().equals(itemId))
        .findFirst()
        .orElseThrow();

    if (item.isDone())
      throw new TasklyException("The todo item is already done.");

    item.markAsDone();
  }

  public boolean hasUnfinishedItems() {
    return items
        .stream()
        .anyMatch(todoItem -> !todoItem.isDone());
  }

  public void moveToTrash() {
    isInTrash = true;
  }

  public void delayDeadline(UUID todoItemId, Deadline newDeadline) {
    TodoItem todoItem = findTodoItem(todoItemId).orElseGet(() -> {
      throw new NoSuchElementException("The todo item with id " + todoItemId + " does not exist.");
    });

    if (newDeadline.isBefore(todoItem.deadline()))
      throw new IllegalArgumentException(
          "The new deadline should be after the current deadline."
              + " Current deadline: " + todoItem.deadline()
              + " New deadline: " + newDeadline);

    todoItem.delayDeadline(newDeadline);
  }

  private Optional<TodoItem> findTodoItem(UUID todoItemId) {
    return items
        .stream()
        .filter(todoItem -> todoItem.id().equals(todoItemId))
        .findFirst();
  }

  public boolean isInTrash() {
    return isInTrash;
  }
}
