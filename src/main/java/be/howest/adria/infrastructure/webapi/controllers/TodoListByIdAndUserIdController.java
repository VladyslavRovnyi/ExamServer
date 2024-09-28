package be.howest.adria.infrastructure.webapi.controllers;

import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.application.taskly.TodoListByIdAndUserIdInput;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.webapi.shared.Request;

import java.util.UUID;

public class TodoListByIdAndUserIdController implements Controller<Request> {
    private UseCase<TodoListByIdAndUserIdInput> todoListByIdAndUserIdUseCase;

    public TodoListByIdAndUserIdController(UseCase<TodoListByIdAndUserIdInput> todoListByIdAndUserIdUseCase) {
        this.todoListByIdAndUserIdUseCase = todoListByIdAndUserIdUseCase;
    }

    @Override
    public void handle(Request request) {
        todoListByIdAndUserIdUseCase.execute(convertToUseCaseInput(request));
    }

    private TodoListByIdAndUserIdInput convertToUseCaseInput(Request request) {
        return new TodoListByIdAndUserIdInput(
                UUID.fromString(request.pathParam("todoListId")),
                UUID.fromString(request.pathParam("userId")));
    }
}
