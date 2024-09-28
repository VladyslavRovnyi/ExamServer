package be.howest.adria.infrastructure.webapi.controllers;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.taskly.AddTodoItemInput;
import be.howest.adria.infrastructure.webapi.shared.Request;
import be.howest.adria.infrastructure.shared.contracts.Controller;

import java.util.UUID;


public class AddTodoItemController implements Controller<Request> {
    private final UseCase<AddTodoItemInput> addTodoItemUseCase;

    public AddTodoItemController(UseCase<AddTodoItemInput> addTodoItemUseCase) {
        this.addTodoItemUseCase = addTodoItemUseCase;
    }

    public void handle(Request request) {
        UUID todoListId = UUID.fromString(request.pathParam("todoListId"));
        String description = request.body().getString("description");
        String deadLineDate = request.body().getString("deadlineDate");

        addTodoItemUseCase.execute(new AddTodoItemInput(todoListId, description, deadLineDate));
    }
}
