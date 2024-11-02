package be.howest.adria.infrastructure.webapi.controllers;

import be.howest.adria.application.taskly.MarkTodoItemAsFavorite;
import be.howest.adria.application.taskly.MarkTodoItemAsFavoriteInput;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.webapi.shared.Request;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.NoSuchElementException;
import java.util.UUID;

public class MarkTodoItemAsFavoriteController implements Controller<Request> {

    private final MarkTodoItemAsFavorite markTodoItemAsFavorite;

    public MarkTodoItemAsFavoriteController(MarkTodoItemAsFavorite markTodoItemAsFavorite) {
        this.markTodoItemAsFavorite = markTodoItemAsFavorite;
    }

    public void registerRoutes(Router router) {
        router.post("/todo/markFavorite")
                .handler(BodyHandler.create()) // To handle JSON body
                .handler(this::handleRoutingContext);
    }

    @Override
    public void handle(Request request) {
        // Method required by Controller<Request> but not used in this context.
    }

    private void handleRoutingContext(RoutingContext context) {
        JsonObject requestBody = context.getBodyAsJson();
        if (requestBody == null) {
            context.response()
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Invalid JSON").encode());
            return;
        }

        try {
            UUID listId = UUID.fromString(requestBody.getString("listId"));
            UUID itemId = UUID.fromString(requestBody.getString("itemId"));

            MarkTodoItemAsFavoriteInput input = new MarkTodoItemAsFavoriteInput(listId, itemId);
            markTodoItemAsFavorite.execute(input);

            context.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("message", "Todo item marked as favorite").encode());
        } catch (IllegalArgumentException e) {
            context.response()
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Invalid UUID format").encode());
        } catch (NoSuchElementException e) {
            context.response()
                    .setStatusCode(404)
                    .end(new JsonObject().put("error", "Todo list or item not found").encode());
        } catch (Exception e) {
            context.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Internal server error").encode());
        }
    }
}