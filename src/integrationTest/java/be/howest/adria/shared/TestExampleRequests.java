package be.howest.adria.shared;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

import static be.howest.adria.shared.TestConstants.*;

public class TestExampleRequests {
    private static final Logger LOGGER = Logger.getLogger(TestExampleRequests.class.getName());
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> createUser(String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_USER))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return sendRequest(request);
    }

    public static HttpResponse<String> createTodoList(String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_TODOLIST))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return sendRequest(request);
    }

    public static HttpResponse<String> subscribe(String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_SUBSCRIBE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return sendRequest(request);
    }

    public static HttpResponse<String> todoListByIdAndUserId(String todoListId, String userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_TODOLIST + "/" + userId + "/" + todoListId))
                .GET()
                .build();

        return sendRequest(request);
    }

    public static HttpResponse<String> addTodoItem(String todoListId, String todoItem1Body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL_TODOLIST_ITEMS, todoListId)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(todoItem1Body))
                .build();
        
        return sendRequest(request);
    }

    public static HttpResponse<Void> markTodoItemAsDone(String todoListId, String todoItemId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL_TODOLIST_ITEM, todoListId, todoItemId)))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            LOGGER.info("Sending request to mark todo item as done" + request.toString());
            return client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to send request", e);
        }
    }

    public static HttpResponse<String> moveTodoItemToOtherList(String sourceListId, String targetListId,
            String itemId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL_MOVE_TODOITEM_TO_OTHER_LIST, sourceListId, itemId)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(targetListId))
                .build();
        
        try {
            LOGGER.info("Sending request to move todo item to other list" + request.toString());
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to send request", e);
        }
    }

    public static HttpResponse<String> allTrashedTodoListsByUser(String userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL_ALL_TRASHED_TODOLISTS, userId)))
                .GET()
                .build();

        return sendRequest(request);
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            LOGGER.info("Sending request " + request.toString());
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to send request", e);
        }
    }
}
