package be.howest.adria.infrastructure.webapi;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.howest.adria.infrastructure.pushnotifications.server.PushServer;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.shared.utils.Config;
import be.howest.adria.infrastructure.webapi.shared.ApiResult;
import be.howest.adria.infrastructure.webapi.shared.ProblemDetails;
import be.howest.adria.infrastructure.webapi.shared.Request;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.router.OpenAPIRoute;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.SchemaValidationException;

public class WebApiModule extends AbstractVerticle {
    private static final Logger LOGGER = Logger.getLogger(WebApiModule.class.getName());

    private Promise<Void> startPromise;
    private final Config config;
    private final BiFunction<String, RoutingContext, Controller<Request>> controllerFactoryMethod;
    private final PushServer pushServer;
    private static Vertx vertxInstance;

    public static void init(Config config,
            BiFunction<String, RoutingContext, Controller<Request>> controllerFactoryMethod,
            PushServer pushServer) {
        vertxInstance = Vertx.vertx();
        vertxInstance.deployVerticle(new WebApiModule(config, controllerFactoryMethod, pushServer))
                .onSuccess(id -> LOGGER.info("WebApi verticle deployed"))
                .onFailure(cause -> {
                    LOGGER.log(Level.SEVERE, "Failed to deploy WebApi verticle", cause);
                    vertxInstance.close();
                });
    }

    public static void tearDown() {
        LOGGER.info("Shutting down");
        vertxInstance.close();
    }

    private WebApiModule(Config config,
            BiFunction<String, RoutingContext, Controller<Request>> controllerFactoryMethod,
            PushServer pushServer) {
        this.config = config;
        this.controllerFactoryMethod = controllerFactoryMethod;
        this.pushServer = pushServer;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        this.startPromise = startPromise;
        int port = Integer.parseInt(config.readSetting("webapi.port"));
        String apiUrl = config.readSetting("webapi.path");

        LOGGER.log(Level.INFO, "Server will be listening at port {0}", port);

        OpenAPIContract.from(vertx, apiUrl)
                .onFailure(cause -> shutDown("Failed to load API specification", cause))
                .onSuccess(openAPIContract -> {
                    LOGGER.log(Level.INFO, "API specification loaded");

                    // MBL: this is new in vertx 4.5.x
                    RouterBuilder routerBuilder = RouterBuilder.create(vertx, openAPIContract,
                            RequestExtractor.withBodyHandler());
                    routerBuilder.rootHandler(createCorsHandler());
                    routerBuilder.rootHandler(BodyHandler.create());
                    routerBuilder.getRoute("subscribe").addHandler(pushServer::handleSubscription);
                    routerBuilder.getRoute("healthCheck").addHandler(ApiResult::ok);
                    routerBuilder.getRoutes().forEach(this::installEndpoint);
                    Router mainRouter = routerBuilder.createRouter();

                    vertx.createHttpServer()
                            .requestHandler(mainRouter)
                            .listen(port)
                            .onFailure(cause -> shutDown("Failed to start server", cause))
                            .onSuccess(server -> {
                                LOGGER.log(Level.INFO, "Server is listening on port: {0}", server.actualPort());
                                startPromise.complete();
                            });
                });
    }

    private void installEndpoint(OpenAPIRoute route) {
        LOGGER.log(Level.INFO, "Installing route: {0}", route.getOperation().getOperationId());
        route.addHandler(ctx -> {
            LOGGER.log(Level.INFO, "Handling request for route: {0}", route.getOperation().getOperationId());
            String operationId = route.getOperation().getOperationId();
            Controller<Request> controller = controllerFactoryMethod.apply(operationId, ctx);
            try {
                controller.getClass()
                        .getMethod("handle", Object.class)
                        .invoke(controller, createRequest(ctx));

                if (!ctx.response().ended())
                    ApiResult.noContent(ctx);
                    
            } catch (SecurityException | ReflectiveOperationException e) {
                handleError(ctx, e.getCause());
            }
        }).addFailureHandler(this::handleEndpointError);
    }

    private void handleEndpointError(RoutingContext ctx) {
        handleError(ctx, ctx.failure());
    }

    private ProblemDetails createProblemDetails(RoutingContext ctx, Throwable cause) {
        ProblemDetails.Builder problemDetails = new ProblemDetails.Builder();
        problemDetails.setInstance(ctx.request().uri());
        problemDetails.setDetail(cause.getMessage());

        if (cause instanceof BadRequestException
                || cause instanceof IllegalArgumentException
                || cause instanceof SchemaValidationException) {
            problemDetails.setTitle("Bad Request");
            problemDetails.setStatus(400);
            problemDetails.setType("https://tools.ietf.org/html/rfc7231#section-6.5.1");
        } else if (cause instanceof NoSuchElementException) {
            problemDetails.setTitle("Not Found");
            problemDetails.setStatus(404);
            problemDetails.setType("https://tools.ietf.org/html/rfc7231#section-6.5.4");
        } else {
            problemDetails.setTitle("Internal Server Error");
            problemDetails.setStatus(500);
            problemDetails.setType("https://tools.ietf.org/html/rfc7231#section-6.6.1");
        }

        return problemDetails.build();
    }

    private void handleError(RoutingContext ctx, Throwable cause) {
        ProblemDetails problemDetails = createProblemDetails(ctx, cause);
        ApiResult.problemDetails(ctx, problemDetails);
        String logMessage = String.format("Failed to handle request: %s", problemDetails.toString());
        LOGGER.log(Level.SEVERE, logMessage, ctx.failure());
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create()
                .addRelativeOrigin(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .exposedHeader("*")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }

    private void shutDown(String message, Throwable cause) {
        LOGGER.log(Level.SEVERE, message, cause);
        LOGGER.info("Shutting down");
        vertx.close();
        startPromise.fail(cause);
    }

    private Object createRequest(RoutingContext ctx) {
        try {
            Class<Request> requestType = Request.class;
            return requestType.getConstructor(RoutingContext.class).newInstance(ctx);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to create request object", e.getCause());
        }
    }
}