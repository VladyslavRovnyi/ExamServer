package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.domain.taskly.TodoList;

import java.util.NoSuchElementException;

public class MarkTodoItemAsFavorite {

    private final TodoListRepository todoListRepository;

    public MarkTodoItemAsFavorite(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    public void execute(MarkTodoItemAsFavoriteInput input) {
        TodoList todoList = todoListRepository.byId(input.getListId())
                .orElseThrow(() -> new NoSuchElementException("Todo list not found"));

        todoList.markTodoItemAsFavorite(input.getItemId());
        todoListRepository.save(todoList);
    }
}
