package be.howest.adria.infrastructure.persistence.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.infrastructure.persistence.repositories.mappers.ResultSetMapper;
import be.howest.adria.infrastructure.persistence.shared.utils.JdbcConnection;

public class SqliteTodoListRepository implements TodoListRepository {
    private static final Logger LOGGER = Logger.getLogger(SqliteTodoListRepository.class.getName());
    private static TodoListRepository instance;
    private final JdbcConnection jdbcConnection;
    private final ResultSetMapper<TodoList> todoListMapper;

    private static final String QRY_DELETE_TODOLIST_BY_ID = """
            DELETE FROM todo_lists WHERE id = ?;
            """;

    private static final String QRY_DELETE_TODOLIST_ITEMS_BY_TODOLIST_ID = """
            DELETE FROM todo_items WHERE todo_list_id = ?;
            """;

    private static final String QRY_INSERT_TODOLIST = """
            INSERT INTO todo_lists (id, user_id, title, is_in_trash)
            VALUES (?, ?, ?, ?);
            """;

    private static final String QRY_INSERT_TODOLIST_ITEM = """
            INSERT INTO todo_items (id, todo_list_id, description, deadline_date, is_done, is_favorite, status)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String QRY_BY_ID = """
            SELECT u.id AS user_id, u.username, tl.id AS todo_list_id, tl.title AS todo_list_title,
                   tl.is_in_trash AS todo_list_is_in_trash, ti.id AS todo_item_id,
                   ti.description AS todo_item_description, ti.deadline_date AS todo_item_deadline_date,
                   ti.is_done AS todo_item_is_done, ti.is_favorite AS todo_item_is_done
            FROM users u
            LEFT JOIN todo_lists tl ON u.id = tl.user_id
            LEFT JOIN todo_items ti ON tl.id = ti.todo_list_id
            WHERE tl.id = ?
            ORDER BY u.id, tl.id;
            """;

    private static final String QRY_TRASHED_TODOLISTS_BY_USER = """
            SELECT u.id AS user_id, u.username, tl.id AS todo_list_id, tl.title AS todo_list_title,
                   tl.is_in_trash AS todo_list_is_in_trash, ti.id AS todo_item_id,
                   ti.description AS todo_item_description, ti.deadline_date AS todo_item_deadline_date,
                   ti.is_done AS todo_item_is_done
            FROM users u
            LEFT JOIN todo_lists tl ON u.id = tl.user_id
            LEFT JOIN todo_items ti ON tl.id = ti.todo_list_id
            WHERE tl.is_in_trash = 1 AND u.id = ?
            ORDER BY u.id, tl.id;
            """;

    private static final String QRY_BY_ID_AND_USER_ID = """
            SELECT u.id AS user_id, u.username, tl.id AS todo_list_id, tl.title AS todo_list_title,
                   tl.is_in_trash AS todo_list_is_in_trash, ti.id AS todo_item_id,
                   ti.description AS todo_item_description, ti.deadline_date AS todo_item_deadline_date,
                   ti.is_done AS todo_item_is_done
            FROM users u
            LEFT JOIN todo_lists tl ON u.id = tl.user_id
            LEFT JOIN todo_items ti ON tl.id = ti.todo_list_id
            WHERE tl.id = ? AND u.id = ?;
            """;

    public static TodoListRepository initialize(
            JdbcConnection jdbcConnection,
            ResultSetMapper<TodoList> todoListByIdAndUserIdMapper) {
        if (instance != null)
            return instance;

        instance = new SqliteTodoListRepository(jdbcConnection, todoListByIdAndUserIdMapper);
        return instance();
    }

    public static TodoListRepository instance() {
        if (instance == null)
            throw new IllegalStateException("Repository is not initialized");

        return instance;
    }

    private SqliteTodoListRepository(
            JdbcConnection jdbcConnection,
            ResultSetMapper<TodoList> todoListByIdAndUserIdMapper) {
        this.jdbcConnection = jdbcConnection;
        this.todoListMapper = todoListByIdAndUserIdMapper;
    }

    @Override
    public Optional<TodoList> byId(UUID id) {
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(QRY_BY_ID)) {
            statement.setString(1, id.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return Optional.ofNullable(todoListMapper.map(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.severe("Failed to retrieve todo list by id {0}" + id);
            throw new IllegalStateException("Failed to retrieve todo list by id", e);
        }
    }

    @Override
    public List<TodoList> allTrashedTodoLists(UUID userId) {
        List<TodoList> todoLists = new ArrayList<>();
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(QRY_TRASHED_TODOLISTS_BY_USER)) {
            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TodoList todoList = todoListMapper.map(resultSet);
                todoLists.add(todoList);
            }

            return todoLists;
        } catch (SQLException e) {
            LOGGER.severe("Failed to retrieve trashed todo lists by user id {0}" + userId);
            throw new IllegalStateException("Failed to retrieve trashed todo lists by user id", e);
        }
    }

    @Override
    public void save(TodoList todoList) {
        Connection connection = null;
        try {
            connection = jdbcConnection.getConnection();

            // Begin transaction
            connection.setAutoCommit(false);
            removeTodoListCascade(connection, todoList);

            // Insert the todolist
            try (PreparedStatement insertTodoListStatement = connection.prepareStatement(QRY_INSERT_TODOLIST)) {
                insertTodoListStatement.setString(1, todoList.id().toString());
                insertTodoListStatement.setString(2, todoList.user().id().toString());
                insertTodoListStatement.setString(3, todoList.title());
                insertTodoListStatement.setBoolean(4, todoList.isInTrash());
                insertTodoListStatement.executeUpdate();
            }
            // Then the todolist items
            try (PreparedStatement insertTodoItemStatement = connection.prepareStatement(QRY_INSERT_TODOLIST_ITEM)) {
                for (var item : todoList.items()) {
                    insertTodoItemStatement.setString(1, item.id().toString());
                    insertTodoItemStatement.setString(2, todoList.id().toString());
                    insertTodoItemStatement.setString(3, item.description());
                    insertTodoItemStatement.setDate(4, java.sql.Date.valueOf(item.deadline().date()));
                    insertTodoItemStatement.setBoolean(5, item.isDone());
                    insertTodoItemStatement.setBoolean(6, item.isFavorite());
                    insertTodoItemStatement.setString(7, item.deadline().status().toString());
                    insertTodoItemStatement.addBatch();
                }
                insertTodoItemStatement.executeBatch();
            }

            // End transaction
            connection.commit();
        } catch (SQLException e) {
            LOGGER.severe("Failed to save todo list {0}" + todoList);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.severe("Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            throw new IllegalStateException("Failed to save todo list", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    LOGGER.severe("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public void remove(TodoList todoList) {
        try (Connection connection = jdbcConnection.getConnection()) {
            connection.setAutoCommit(false);

            removeTodoListCascade(connection, todoList);

            connection.commit();
        } catch (SQLException e) {
            LOGGER.severe("Failed to remove todo list {0}" + todoList);
            throw new IllegalStateException("Failed to remove todo list", e);
        }
    }

    private void removeTodoListCascade(Connection connection, TodoList todoList) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(QRY_DELETE_TODOLIST_ITEMS_BY_TODOLIST_ID)) {
            statement.setString(1, todoList.id().toString());
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(QRY_DELETE_TODOLIST_BY_ID)) {
            statement.setString(1, todoList.id().toString());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<TodoList> byIdAndUserId(UUID id, UUID userId) {
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(QRY_BY_ID_AND_USER_ID)) {

            statement.setString(1, id.toString());
            statement.setString(2, userId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return Optional.ofNullable(todoListMapper.map(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve todo list by id and user id", e);
        }
    }
}