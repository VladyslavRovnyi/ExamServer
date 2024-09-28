package be.howest.adria.infrastructure.webapi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import be.howest.adria.shared.TestExampleRequests;
import be.howest.adria.shared.TestModels;
import be.howest.adria.shared.WebApiTestUtils;
import static be.howest.adria.shared.TestConstants.*;

class CreateTodoListTests {
    @BeforeEach
    void setUp() {
        WebApiTestUtils.waitForServer(API_URL_HEALTH_CHECK);    
    }
    
    @AfterEach
    void tearDown() {
        WebApiTestUtils.tearDownWebApi();
    }

    @Test
    void whenTodoList_withValidInput_shouldReturnCreated() {
        // Arrange
        String createUserBody = TestModels.createUserRequestBodyDefault();
        HttpResponse<String> createUserResponse = TestExampleRequests.createUser(createUserBody);
        String userId = WebApiTestUtils.getResourceId(createUserResponse);
        
        String createTodoListBody = TestModels.createTodoListBodyRequest(userId, "My todo list");

        // Act
        HttpResponse<String> response = TestExampleRequests.createTodoList(createTodoListBody);

        // Assert
        String todoListId = WebApiTestUtils.getResourceId(response);
        WebApiTestUtils.assertCreated(response);
        assertDoesNotThrow(() -> {
            UUID.fromString(todoListId);
        });
        
        HttpResponse<String> todoListReponse = TestExampleRequests.todoListByIdAndUserId(todoListId, userId);
        JsonObject todoList = new Gson().fromJson(todoListReponse.body(), JsonObject.class);
        WebApiTestUtils.assertOk(todoListReponse);

        assertEquals(todoListId, todoList.get("id").getAsString());
        assertEquals(userId, todoList.getAsJsonObject("user").get("id").getAsString());
        assertEquals("My todo list", todoList.get("title").getAsString());
        assertEquals("John Doe", todoList.getAsJsonObject("user").get("username").getAsString());
        assertFalse(todoList.get("isInTrash").getAsBoolean());
        assertEquals(0, todoList.getAsJsonArray("items").size());
    }
}
