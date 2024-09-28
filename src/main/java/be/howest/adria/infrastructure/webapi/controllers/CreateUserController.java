package be.howest.adria.infrastructure.webapi.controllers;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.taskly.CreateUserInput;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.webapi.shared.Request;

public class CreateUserController implements Controller<Request> {
    private final UseCase<CreateUserInput> createUserUseCase;

    public CreateUserController(UseCase<CreateUserInput> createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @Override
    public void handle(Request request) {
        String username = request.body().getString("userName");
        createUserUseCase.execute(new CreateUserInput(username));
    }
}
