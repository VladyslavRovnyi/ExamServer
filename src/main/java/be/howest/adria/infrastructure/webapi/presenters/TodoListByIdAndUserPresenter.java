package be.howest.adria.infrastructure.webapi.presenters;

import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.taskly.TodoListByIdAndUserIdOutput;
import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;

public class TodoListByIdAndUserPresenter implements OutputPort<TodoListByIdAndUserIdOutput> {
    private final ResponseHandler<TodoList> responseHandler;

    public TodoListByIdAndUserPresenter(ResponseHandler<TodoList> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void present(TodoListByIdAndUserIdOutput data) {
        responseHandler.handle(data.todoList);
    }
}
