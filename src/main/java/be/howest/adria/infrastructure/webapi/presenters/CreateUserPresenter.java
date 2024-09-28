package be.howest.adria.infrastructure.webapi.presenters;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.CreateUserOutput;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;


public class CreateUserPresenter implements OutputPort<CreateUserOutput> {
    private final ResponseHandler<String> responseHandler;

    public CreateUserPresenter(ResponseHandler<String> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void present(CreateUserOutput data) {
        responseHandler.handle(data.userId.toString());
    }
}
