package be.howest.adria.application.shared;

import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.domain.taskly.User;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class MockUserRepository implements UserRepository {

  private final boolean byIdIsFailing;

  public MockUserRepository(boolean byIdIsFailing) {
    this.byIdIsFailing = byIdIsFailing;
  }

  public MockUserRepository() {
    this(false);
  }

  @Override
  public Optional<User> byId(UUID userId) {
    if (byIdIsFailing)
      throw new NoSuchElementException(String.format("User with id %s not found", userId));

    return Optional.of(User.create(userId, "john"));
  }

  @Override
  public void save(User user) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
