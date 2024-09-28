package be.howest.adria.infrastructure.webapi.presenters;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.AddTodoItemOutput;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;

public class AddTodoItemPresenter implements OutputPort<AddTodoItemOutput> {
    private final ResponseHandler<AddTodoItemResponse> responseHandler;

    public AddTodoItemPresenter(ResponseHandler<AddTodoItemResponse> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void present(AddTodoItemOutput data) {
        AddTodoItemResponse response = new AddTodoItemResponse(
                data.todoListId.toString(),
                data.todoItemId.toString());

        responseHandler.handle(response);
    }
}
