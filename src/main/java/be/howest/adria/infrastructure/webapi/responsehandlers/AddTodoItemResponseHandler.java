package be.howest.adria.infrastructure.webapi.responsehandlers;

import be.howest.adria.infrastructure.webapi.presenters.AddTodoItemResponse;
import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;
import io.vertx.ext.web.RoutingContext;

public class AddTodoItemResponseHandler implements ResponseHandler<AddTodoItemResponse> {
    private final RoutingContext ctx;

    public AddTodoItemResponseHandler(RoutingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(AddTodoItemResponse response) {
        String uri = "/todolists/" + response.todoListId + "/items/" + response.todoItemId;
        ApiResult.created(ctx, uri);
    }
}
