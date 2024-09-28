package be.howest.adria.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.logging.Logger;
import java.time.Instant;

import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;

import be.howest.adria.main.Main;
import java.time.Duration;

public class WebApiTestUtils {
    private static Gson gson = new Gson();

    public static void assertOk(HttpResponse<String> todoListReponse) {
        assertOnWrongStatusCode(200, todoListReponse);
    }

    public static void assertCreated(HttpResponse<String> response) {
        assertOnWrongStatusCode(201, response);
    }

    public static void assertNoContent(HttpResponse<?> response) {
        assertOnWrongStatusCode(204, response);
    }

    public static String createRequestBody(Object object) {
        return gson.toJson(object);
    }

    public static void assertResponseHeader(HttpResponse<String> response, String header, String expectedValue) {
        String actualValue = response.headers().firstValue(header).orElse("");
        assertEquals(expectedValue, actualValue);
    }

    public static String getResourceId(HttpResponse<String> response) {
        String location = response.headers().firstValue("Location").orElse("");
        return location.substring(location.lastIndexOf("/") + 1);
    }

    public static void assertValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            Assertions.fail("Invalid UUID");
        }
    }

    public static void waitForServer(String apiUrl) {
        Main.main(new String[] { "" });
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Instant end = Instant.now().plus(Duration.ofSeconds(6));

        while (Instant.now().isBefore(end)) {
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200)
                    return;
            } catch (Exception e) {
                Logger.getGlobal().info("Server check attempt failed");
            }
        }

        throw new IllegalStateException("Server did not start");
    }

    public static <T> T parseResponseBody(HttpResponse<String> response, Class<T> clazz) {
        try {
            return gson.fromJson(response.body(), clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse response body", e);
        }
    }

    private static void assertOnWrongStatusCode(int expected, HttpResponse<?> response) {
        if (expected == response.statusCode())
            return;

        int actualResponseCode = response.statusCode();
        Object body = response.body();
        String message = String.format("Expected status code %d, but got %d",
                expected, actualResponseCode);

        if (body != null)
            message += ", body: " + body;

        Assertions.fail(message);
    }

    public static void tearDownWebApi() {
        Main.tearDown();
    }
}
