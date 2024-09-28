package be.howest.adria.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestModels {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String createUserRequestBodyDefault() {
        return createUserRequestBody("John Doe");
    }

    public static String createUserRequestBody(String userName) {
        ObjectNode customJson = objectMapper.createObjectNode();
        customJson.put("userName", userName);
        return customJson.toString();
    }

    public static String createTodoListBodyRequest(String userId, String description) {
        ObjectNode customJson = objectMapper.createObjectNode();
        customJson.put("userId", userId);
        customJson.put("title", description);
        return customJson.toString();
    }

    public static String createTodoItemBodyRequest(String todoListId, String description, String deadlineDate) {
        ObjectNode customJson = objectMapper.createObjectNode();
        customJson.put("todoListId", todoListId);
        customJson.put("description", description);
        customJson.put("deadlineDate", deadlineDate);   
        return customJson.toString();
    }

    public static String createMoveTodoItemToOtherListRequest(String targetListId) {
        ObjectNode customJson = objectMapper.createObjectNode();
        customJson.put("targetListId", targetListId);
        return customJson.toString();
    }
}
