package be.howest.adria.infrastructure.persistence.repositories.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;

public class TodoListMapper implements ResultSetMapper<TodoList> {
    private static final String TODO_LIST_ID = "todo_list_id";
    private static final String TODO_LIST_TITLE = "todo_list_title";
    private static final String TODO_LIST_IS_IN_TRASH = "todo_list_is_in_trash";
    private static final String USER_ID = "user_id";
    private static final String USER_USERNAME = "username";
    private static final String TODO_ITEM_ID = "todo_item_id";
    private static final String TODO_ITEM_DESCRIPTION = "todo_item_description";
    private static final String TODO_ITEM_DEADLINE_DATE = "todo_item_deadline_date";

    @Override
    public TodoList map(ResultSet resultSet) throws SQLException {
        if (resultSet.getString(TODO_LIST_ID) == null)
            return null;

        String todoListId = resultSet.getString(TODO_LIST_ID);
        String title = resultSet.getString(TODO_LIST_TITLE);
        boolean isInTrash = resultSet.getBoolean(TODO_LIST_IS_IN_TRASH);

        User user = mapUser(resultSet);

        List<TodoItem> items = new ArrayList<>();
        do {
            TodoItem item = mapTodoItem(resultSet);
            if (item != null)
                items.add(item);
        } while (resultSet.next() && resultSet.getString(TODO_LIST_ID).equals(todoListId));

        return TodoList.create(UUID.fromString(todoListId), user, title, isInTrash, items);
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return User.create(
                UUID.fromString(resultSet.getString(USER_ID)),
                resultSet.getString(USER_USERNAME));
    }

    private TodoItem mapTodoItem(ResultSet resultSet) throws SQLException {
        String todoItemId = resultSet.getString(TODO_ITEM_ID);
        if (todoItemId == null)
            return null;

        LocalDate deadline = resultSet.getDate(TODO_ITEM_DEADLINE_DATE).toLocalDate();
        int day = deadline.getDayOfMonth();
        int month = deadline.getMonthValue();
        int year = deadline.getYear();

        return TodoItem.create(
                UUID.fromString(todoItemId),
                resultSet.getString(TODO_ITEM_DESCRIPTION),
                Deadline.create(day, month, year));
    }
}
