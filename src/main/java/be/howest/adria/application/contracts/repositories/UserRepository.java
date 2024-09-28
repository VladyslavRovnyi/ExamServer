package be.howest.adria.application.contracts.repositories;

import java.util.Optional;
import java.util.UUID;

import be.howest.adria.domain.taskly.User;

public interface UserRepository {

    Optional<User> byId(UUID userId);

    void save(User user);

}
