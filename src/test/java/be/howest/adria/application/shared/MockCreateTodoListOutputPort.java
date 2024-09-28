package be.howest.adria.application.shared;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.CreateTodoListOutput;

public class MockCreateTodoListOutputPort implements OutputPort<CreateTodoListOutput> {
    private CreateTodoListOutput output;

    @Override
    public void present(CreateTodoListOutput data) {
        output = data;
    }

    public CreateTodoListOutput getOutput() {
        return output;
    }

    public void assertPresented() {
        if (output == null)
            throw new AssertionError("No output was presented");
    }
}
