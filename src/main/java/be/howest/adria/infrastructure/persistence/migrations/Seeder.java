package be.howest.adria.infrastructure.persistence.migrations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.domain.taskly.Deadline;
import be.howest.adria.domain.taskly.TodoItem;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;

public class Seeder {
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;

    public Seeder(
        TodoListRepository todoListRepository,
        UserRepository userRepository
    ) {
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
    }

    public void seed() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String tomorrowDeadlineDate = now.plusDays(1).format(formatter);
        String yesterdayDeadlineDate = now.minusDays(1).format(formatter);
        String nextWeekDeadlineDate = now.plusWeeks(1).format(formatter);
        String nextMonthDeadlineDate = now.plusMonths(1).format(formatter);

        String todoList1Name = "Todo List 1";
        String todoList2Name = "Todo List 2";

        // Create 3 users
        User user1 = User.create(UUID.fromString("e52e14b4-3f80-4fa8-b77c-0c1ac85ba697"), "John Doe");
        User user2 = User.create(UUID.randomUUID(), "Jane Woke");
        User user3 = User.create(UUID.randomUUID(), "Jack Black");
        
        // Save users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Create 2 todolists for each user
        TodoList todoList1User1 = TodoList.create(user1, todoList1Name);
        TodoList todoList2User1 = TodoList.create(user1, todoList2Name);
        TodoList todoList1User2 = TodoList.create(user2, todoList1Name);
        TodoList todoList2User2 = TodoList.create(user2, todoList2Name);
        TodoList todoList1User3 = TodoList.create(user3, todoList1Name);
        TodoList todoList2User3 = TodoList.create(user3, todoList2Name);

        // Add 2 todolist items to each todo list
        todoList1User1.addTodoItem(TodoItem.create("Buy banana", Deadline.create(tomorrowDeadlineDate)));
        todoList1User1.addTodoItem(TodoItem.create("Buy apple", Deadline.create(yesterdayDeadlineDate)));
        todoList2User1.addTodoItem(TodoItem.create("Buy milk", Deadline.create(nextWeekDeadlineDate)));
        todoList2User1.addTodoItem(TodoItem.create("Buy bread", Deadline.create(nextMonthDeadlineDate)));
        todoList1User2.addTodoItem(TodoItem.create("Clean kitchen", Deadline.create(tomorrowDeadlineDate)));
        todoList1User2.addTodoItem(TodoItem.create("Clean bathroom", Deadline.create(yesterdayDeadlineDate)));
        todoList2User2.addTodoItem(TodoItem.create("Clean living room", Deadline.create(nextWeekDeadlineDate)));
        todoList2User2.addTodoItem(TodoItem.create("Clean bedroom", Deadline.create(nextMonthDeadlineDate)));
        todoList1User3.addTodoItem(TodoItem.create("Study Java", Deadline.create(tomorrowDeadlineDate)));
        todoList1User3.addTodoItem(TodoItem.create("Study SQL", Deadline.create(yesterdayDeadlineDate)));
        todoList2User3.addTodoItem(TodoItem.create("Study Python", Deadline.create(nextWeekDeadlineDate)));
        todoList2User3.addTodoItem(TodoItem.create("Study JavaScript", Deadline.create(nextMonthDeadlineDate)));

        // Save todolists
        todoListRepository.save(todoList1User1);
        todoListRepository.save(todoList1User1);
        todoListRepository.save(todoList2User1);
        todoListRepository.save(todoList1User2);
        todoListRepository.save(todoList2User2);
        todoListRepository.save(todoList1User3);
        todoListRepository.save(todoList2User3);
    }
}
