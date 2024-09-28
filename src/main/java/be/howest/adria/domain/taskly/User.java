package be.howest.adria.domain.taskly;

import java.util.UUID;

public class User {
  private final UUID id;
  private String username;
  
  public static User create(String username) {
    return new User(UUID.randomUUID(), username);
  }

  public static User create(UUID id, String username) {
    return new User(id, username);
  }

  private User(UUID id, String username) {
    this.id = id;
    this.username = username;
  }

  public UUID id() {
    return id;
  }

  public String username() {
    return username;
  }
}
