package be.howest.adria.infrastructure.webapi.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApiResult {
    private static final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    public static void created(RoutingContext ctx, String location) {
        ctx.response()
                .putHeader("Location", location)
                .setStatusCode(201)
                .end();
    }
    
    public static void problemDetails(RoutingContext ctx, ProblemDetails problemDetails) {
        ctx.response()
                .setStatusCode(problemDetails.getStatus())
                .putHeader("Content-Type", "application/problem+json")
                .end(gson.toJson(problemDetails));
    }

    public static void ok(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(200)
                .end();
    }

    public static void ok(RoutingContext ctx, Object body) {
        ctx.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(HttpResponseStatus.OK.code())
                .end(gson.toJson(body));
    }

    public static void noContent(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(204)
                .end();
    }

    private ApiResult() {
    }

}
