package be.howest.adria.infrastructure.persistence.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.domain.taskly.User;
import be.howest.adria.infrastructure.persistence.shared.utils.JdbcConnection;

public class SqliteUserRepository implements UserRepository {
    private static final Logger LOGGER = Logger.getLogger(SqliteUserRepository.class.getName());
    private static SqliteUserRepository instance;
    private static final String QRY_BY_ID = """
            SELECT id, username
            FROM users
            WHERE id = ?;
            """;

    private static final String QRY_DELETE_BY_ID = """
            DELETE FROM users
            WHERE id = ?;
            """;

    private static final String QRY_INSERT_USER = """
            INSERT INTO users (id, username)
            VALUES (?, ?);
            """;

    private final JdbcConnection jdbcConnection;

    public static SqliteUserRepository initialize(JdbcConnection jdbcConnection) {
        if (instance != null)
            return instance;

        instance = new SqliteUserRepository(jdbcConnection);
        return instance();
    }

    public static SqliteUserRepository instance() {
        if (instance == null)
            throw new IllegalStateException("SqliteUserRepository has not been initialized");

        return instance;
    }

    private SqliteUserRepository(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    @Override
    public Optional<User> byId(UUID userId) {
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(QRY_BY_ID)) {
            statement.setString(1, userId.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return Optional.of(
                        User.create(UUID.fromString(resultSet.getString(1)), resultSet.getString(2)));
            }

            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve user by id", e);
            throw new IllegalStateException("Failed to retrieve user by id", e);
        }
    }

    @Override
    public void save(User user) {
        try (Connection connection = jdbcConnection.getConnection()) {
            // Begin transaction
            connection.setAutoCommit(false);

            // First delete the user
            try (PreparedStatement statement = connection.prepareStatement(QRY_DELETE_BY_ID)) {
                statement.setString(1, user.id().toString());
                statement.executeUpdate();
            }

            // Then insert the user
            try (PreparedStatement statement = connection.prepareStatement(QRY_INSERT_USER)) {
                statement.setString(1, user.id().toString());
                statement.setString(2, user.username());
                statement.executeUpdate();
            }

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save user", e);
            throw new IllegalStateException("Failed to save user", e);
        }
    }
}
