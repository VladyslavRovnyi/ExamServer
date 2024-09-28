package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.TodoList;
import java.util.NoSuchElementException;

public class TodoListByIdAndUserId implements UseCase<TodoListByIdAndUserIdInput> {
    private final OutputPort<TodoListByIdAndUserIdOutput> outputPort;
    private final TodoListRepository todoListRepository;

    public TodoListByIdAndUserId(
            TodoListRepository todoListRepository,
            OutputPort<TodoListByIdAndUserIdOutput> outputPort) {
        this.outputPort = outputPort;
        this.todoListRepository = todoListRepository;
    }

    @Override
    public void execute(TodoListByIdAndUserIdInput input) {
        TodoList todoList = todoListRepository.byIdAndUserId(input.todoListId, input.userId)
                .orElseThrow(() -> new NoSuchElementException("TodoList not found"));
        outputPort.present(new TodoListByIdAndUserIdOutput(todoList));
    }
}
