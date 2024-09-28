package be.howest.adria.infrastructure.webapi.controllers;

import java.util.UUID;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.taskly.MarkTodoItemAsDoneInput;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.webapi.shared.Request;

public class MarkTodoItemAsDoneController implements Controller<Request> {
    private final UseCase<MarkTodoItemAsDoneInput> markTodoItemAsDoneUseCase;

    public MarkTodoItemAsDoneController(UseCase<MarkTodoItemAsDoneInput> markTodoItemAsDoneUseCase) {
        this.markTodoItemAsDoneUseCase = markTodoItemAsDoneUseCase;
    }

    @Override
    public void handle(Request request) {
        markTodoItemAsDoneUseCase.execute(convertToUseCaseInput(request));
    }

    // It's fine to have a private conversion method in a controller
    private MarkTodoItemAsDoneInput convertToUseCaseInput(Request request) {
        return new MarkTodoItemAsDoneInput(
                UUID.fromString(request.pathParam("todoListId")),
                UUID.fromString(request.pathParam("todoItemId")));
    }
}
