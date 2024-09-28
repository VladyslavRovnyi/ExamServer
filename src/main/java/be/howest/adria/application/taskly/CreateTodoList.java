package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.EventNotifier;
import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.domain.taskly.User;
import java.util.NoSuchElementException;


public class CreateTodoList implements UseCase<CreateTodoListInput> {

    private final UserRepository userRepository;
    private final TodoListRepository todoListRepository;
    private final OutputPort<CreateTodoListOutput> outputPort;
    private final EventNotifier eventNotifier;

    public CreateTodoList(
            UserRepository userRepository,
            TodoListRepository todoListRepository,
            EventNotifier eventNotifier,
            OutputPort<CreateTodoListOutput> outputPort) {
        this.userRepository = userRepository;
        this.todoListRepository = todoListRepository;
        this.outputPort = outputPort;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void execute(
            CreateTodoListInput input) {
        User user = userRepository.byId(input.userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        TodoList todoList = TodoList.create(user, input.title);

        todoListRepository.save(todoList);

        eventNotifier.publish(String.format("TodoListCreated,title:{%s},id:{%s}", todoList.title(), todoList.id()));

        outputPort.present(new CreateTodoListOutput(todoList.id()));
    }
}
