package be.howest.adria.application.taskly;

import be.howest.adria.domain.taskly.TodoList;

public class TodoListByIdAndUserIdOutput {
    public final TodoList todoList;

    public TodoListByIdAndUserIdOutput(TodoList todoList) {
        this.todoList = todoList;
    }
}
