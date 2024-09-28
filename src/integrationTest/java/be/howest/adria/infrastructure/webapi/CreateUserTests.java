package be.howest.adria.infrastructure.webapi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.howest.adria.shared.TestExampleRequests;
import be.howest.adria.shared.TestModels;
import be.howest.adria.shared.WebApiTestUtils;
import static be.howest.adria.shared.TestConstants.*;

class CreateUserTests {
    @BeforeEach
    void setUp() {
        WebApiTestUtils.waitForServer(API_URL_HEALTH_CHECK);
    }

    @AfterEach
    void tearDown() {
        WebApiTestUtils.tearDownWebApi();
    }

    @Test
    void whenCreatingUser_withValidInput_shouldReturnCreated() {
        // Arrange
        String body = TestModels.createUserRequestBodyDefault();

        // Act
        HttpResponse<String> response = TestExampleRequests.createUser(body);

        // Assert
        String uuid = WebApiTestUtils.getResourceId(response);
        WebApiTestUtils.assertCreated(response);
        assertDoesNotThrow(() -> {
            UUID.fromString(uuid);
        });
    }
}
