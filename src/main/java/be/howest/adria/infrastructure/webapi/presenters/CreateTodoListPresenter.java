package be.howest.adria.infrastructure.webapi.presenters;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.CreateTodoListOutput;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;


public class CreateTodoListPresenter implements OutputPort<CreateTodoListOutput> {
    private final ResponseHandler<String> responseHandler;

    public CreateTodoListPresenter(ResponseHandler<String> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void present(CreateTodoListOutput data) {
        responseHandler.handle(data.todoListId.toString());
    }
}
