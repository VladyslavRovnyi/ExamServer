package be.howest.adria.infrastructure.webapi.responsehandlers;

import be.howest.adria.domain.taskly.TodoList;
import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;
import io.vertx.ext.web.RoutingContext;

public class TodoListByIdAndUserIdResponseHandler implements ResponseHandler<TodoList> {
    private final RoutingContext ctx;

    public TodoListByIdAndUserIdResponseHandler(RoutingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(TodoList response) {
        ApiResult.ok(ctx, response);
    }
}
