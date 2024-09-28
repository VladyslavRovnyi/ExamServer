package be.howest.adria.application.shared;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.TodoListByIdAndUserIdOutput;

public class MockTodoListByIdAndUserIdOutputPort implements OutputPort<TodoListByIdAndUserIdOutput> {
    public TodoListByIdAndUserIdOutput output;

    @Override
    public void present(TodoListByIdAndUserIdOutput data) {
        output = data;
    }
}
