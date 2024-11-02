package be.howest.adria.domain.taskly;

import java.util.UUID;

public class TodoItem {
  private final UUID id;
  private final String description;
  private final Deadline deadline;
  private boolean isDone = false;
  private boolean isFavorite = false; // New favorite field

  public static TodoItem create(UUID id, String description, Deadline deadline, boolean isFavorite) {
    validateDescription(description);
    return new TodoItem(id, description, deadline, isFavorite);
  }

  public static TodoItem create(UUID id, String description, Deadline deadline) {
    validateDescription(description);
    return new TodoItem(id, description, deadline, false);
  }

  public static TodoItem create(String description, Deadline deadline) {
    validateDescription(description);
    return create(UUID.randomUUID(), description, deadline);
  }

  private TodoItem(UUID id, String description, Deadline deadline, boolean isFavorite) {
    this.id = id;
    this.description = description;
    this.deadline = deadline;
    this.isFavorite = isFavorite;
  }

  public String description() {
    return description;
  }

  public UUID id() {
    return id;
  }

  public boolean isDone() {
    return isDone;
  }

  public void markAsDone() {
    isDone = true;
  }

  public Deadline deadline() {
    return deadline;
  }

  public void delayDeadline(Deadline newDeadline) {
    deadline.update(newDeadline);
  }

  // Method to toggle favorite status
  public void toggleFavorite() {
    isFavorite = !isFavorite;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  private static void validateDescription(String description) {
    if (!isValidDescription(description))
      throw new IllegalArgumentException("The description of a todo item cannot be empty.");
  }

  private static boolean isValidDescription(String description) {
    return description != null && !description.trim().isEmpty();
  }
}
