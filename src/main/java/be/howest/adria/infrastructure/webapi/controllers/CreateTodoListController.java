package be.howest.adria.infrastructure.webapi.controllers;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.taskly.CreateTodoListInput;
import be.howest.adria.infrastructure.webapi.shared.Request;
import be.howest.adria.infrastructure.shared.contracts.Controller;

import java.util.UUID;

public class CreateTodoListController implements Controller<Request> {
    private final UseCase<CreateTodoListInput> createTodoListUseCase;

    public CreateTodoListController(UseCase<CreateTodoListInput> createTodoListUsecase) {
        this.createTodoListUseCase = createTodoListUsecase;
    }

    public void handle(Request request) {
        String title = request.body().getString("title");
        UUID userId = UUID.fromString(request.body().getString("userId"));
        createTodoListUseCase.execute(new CreateTodoListInput(userId, title));
    }
}
