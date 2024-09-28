package be.howest.adria.infrastructure.webapi.responsehandlers;

import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import be.howest.adria.infrastructure.webapi.shared.ResponseHandler;
import io.vertx.ext.web.RoutingContext;

public class CreateUserResponseHandler implements ResponseHandler<String> {
    private final RoutingContext ctx;

    public CreateUserResponseHandler(RoutingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(String todoListId) {
        ApiResult.created(ctx, "/users/" + todoListId);
    }
}
