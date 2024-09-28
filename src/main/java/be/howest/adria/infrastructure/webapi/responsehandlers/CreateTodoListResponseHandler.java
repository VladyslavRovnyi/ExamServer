package be.howest.adria.infrastructure.webapi.responsehandlers;

import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;
import io.vertx.ext.web.RoutingContext;

public class CreateTodoListResponseHandler implements ResponseHandler<String> {
    private final RoutingContext ctx;

    public CreateTodoListResponseHandler(RoutingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(String todoListId) {
        ApiResult.created(ctx, "/todolists/" + todoListId);
    }
}
