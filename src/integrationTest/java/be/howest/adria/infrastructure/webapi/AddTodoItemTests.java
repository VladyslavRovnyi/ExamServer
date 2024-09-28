package be.howest.adria.infrastructure.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import be.howest.adria.shared.TestExampleRequests;
import be.howest.adria.shared.TestModels;
import be.howest.adria.shared.WebApiTestUtils;
import static be.howest.adria.shared.TestConstants.*;

class AddTodoItemTests {
    @BeforeEach
    void setUp() {
        WebApiTestUtils.waitForServer(API_URL_HEALTH_CHECK);    
    }
    
    @AfterEach
    void tearDown() {
        WebApiTestUtils.tearDownWebApi();
    }

    @Test 
    void whenAddingTodoItem_withValidInput_shouldUpdateTodoList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String tomorrow = formatter.format(java.time.LocalDate.now().plusDays(1));
        String createUserBody = TestModels.createUserRequestBodyDefault();
        HttpResponse<String> createUserResponse = TestExampleRequests.createUser(createUserBody);
        String userId = WebApiTestUtils.getResourceId(createUserResponse);
        String createTodoListBody = TestModels.createTodoListBodyRequest(userId, "My todo list");
        HttpResponse<String> response = TestExampleRequests.createTodoList(createTodoListBody);
        String todoListId = WebApiTestUtils.getResourceId(response);
        String todoItem1Body = TestModels.createTodoItemBodyRequest(todoListId, "My first todo item", tomorrow);

        // Act
        HttpResponse<String> todoItem1Response = TestExampleRequests.addTodoItem(todoListId, todoItem1Body);

        // Assert
        WebApiTestUtils.assertCreated(todoItem1Response);
        HttpResponse<String> todoListReponse = TestExampleRequests.todoListByIdAndUserId(todoListId, userId);
        WebApiTestUtils.assertOk(todoListReponse);
        JsonObject todoList = new Gson().fromJson(todoListReponse.body(), JsonObject.class);
        System.out.println(todoList);
        assertEquals(todoListId, todoList.get("id").getAsString());
        assertEquals(userId, todoList.getAsJsonObject("user").get("id").getAsString());
        assertEquals("My todo list", todoList.get("title").getAsString());
        assertEquals("John Doe", todoList.getAsJsonObject("user").get("username").getAsString());
        assertFalse(todoList.get("isInTrash").getAsBoolean());
        assertEquals(1, todoList.getAsJsonArray("items").size());
        assertEquals("My first todo item", todoList.getAsJsonArray("items").get(0).getAsJsonObject().get("description").getAsString());
    }
}
