package be.howest.adria.application.shared;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.AddTodoItemOutput;

public class MockAddTodoItemOutputPort implements OutputPort<AddTodoItemOutput> {
    private AddTodoItemOutput output;

    @Override
    public void present(AddTodoItemOutput data) {
        output = data;
    }

    public AddTodoItemOutput getOutput() {
        return output;
    }
}
