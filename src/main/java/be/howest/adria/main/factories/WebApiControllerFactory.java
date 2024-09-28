package be.howest.adria.main.factories;

import be.howest.adria.application.contracts.EventNotifier;
import be.howest.adria.application.contracts.repositories.TodoListRepository;
import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.application.taskly.AddTodoItem;
import be.howest.adria.application.taskly.CreateTodoList;
import be.howest.adria.application.taskly.CreateUser;
import be.howest.adria.application.taskly.MarkTodoItemAsDone;
import be.howest.adria.application.taskly.TodoListByIdAndUserId;
import be.howest.adria.infrastructure.persistence.repositories.SqliteTodoListRepository;
import be.howest.adria.infrastructure.persistence.repositories.SqliteUserRepository;
import be.howest.adria.infrastructure.pushnotifications.server.PushServer;
import be.howest.adria.infrastructure.shared.contracts.Controller;
import be.howest.adria.infrastructure.webapi.controllers.AddTodoItemController;
import be.howest.adria.infrastructure.webapi.controllers.CreateTodoListController;
import be.howest.adria.infrastructure.webapi.controllers.CreateUserController;
import be.howest.adria.infrastructure.webapi.controllers.MarkTodoItemAsDoneController;
import be.howest.adria.infrastructure.webapi.controllers.TodoListByIdAndUserIdController;
import be.howest.adria.infrastructure.webapi.presenters.AddTodoItemPresenter;
import be.howest.adria.infrastructure.webapi.presenters.CreateTodoListPresenter;
import be.howest.adria.infrastructure.webapi.presenters.CreateUserPresenter;
import be.howest.adria.infrastructure.webapi.presenters.TodoListByIdAndUserPresenter;
import be.howest.adria.infrastructure.webapi.responsehandlers.AddTodoItemResponseHandler;
import be.howest.adria.infrastructure.webapi.responsehandlers.CreateTodoListResponseHandler;
import be.howest.adria.infrastructure.webapi.responsehandlers.CreateUserResponseHandler;
import be.howest.adria.infrastructure.webapi.responsehandlers.TodoListByIdAndUserIdResponseHandler;
import be.howest.adria.infrastructure.webapi.shared.Request;
import io.vertx.ext.web.RoutingContext;

public class WebApiControllerFactory {
    private static final WebApiControllerFactory instance = new WebApiControllerFactory();
    private final TodoListRepository todoListRepository = SqliteTodoListRepository.instance();
    private final UserRepository userRepository = SqliteUserRepository.instance();
    private final EventNotifier eventNotifier = PushServer.instance();

    public static WebApiControllerFactory instance() {
        return instance;
    }

    private WebApiControllerFactory() {}

    public Controller<Request> createController(String operationId, RoutingContext ctx) {
        switch (operationId) {
            case "createTodoList":
                return createCreateTodoListController(ctx);
            case "addTodoItem":
                return createAddTodoItemController(ctx);
            case "createUser":
                return createCreateUserController(ctx);
            case "todoListByIdAndUserId":
                return createTodoListByIdAndUserIdController(ctx);
            case "markTodoItemAsDone":
                return createMarkTodoItemAsDoneController();
            default:
                throw new IllegalArgumentException("Unknown operationId: " + operationId);
        }
    }

    private CreateTodoListController createCreateTodoListController(RoutingContext ctx) {
        CreateTodoListResponseHandler responseHandler = new CreateTodoListResponseHandler(ctx);
        CreateTodoListPresenter outputPort = new CreateTodoListPresenter(responseHandler);
        CreateTodoList useCase = new CreateTodoList(userRepository, todoListRepository, eventNotifier, outputPort);

        return new CreateTodoListController(useCase);
    }

    private AddTodoItemController createAddTodoItemController(RoutingContext ctx) {
        AddTodoItemResponseHandler responseHandler = new AddTodoItemResponseHandler(ctx);
        AddTodoItemPresenter outputPort = new AddTodoItemPresenter(responseHandler);
        AddTodoItem useCase = new AddTodoItem(todoListRepository, outputPort);

        return new AddTodoItemController(useCase);
    }

    private CreateUserController createCreateUserController(RoutingContext ctx) {
        CreateUserResponseHandler responseHandler = new CreateUserResponseHandler(ctx);
        CreateUserPresenter outputPort = new CreateUserPresenter(responseHandler);
        CreateUser useCase = new CreateUser(userRepository, outputPort);

        return new CreateUserController(useCase);
    }

    private TodoListByIdAndUserIdController createTodoListByIdAndUserIdController(RoutingContext ctx) {
        TodoListByIdAndUserIdResponseHandler responseHandler = new TodoListByIdAndUserIdResponseHandler(ctx);
        TodoListByIdAndUserPresenter outputPort = new TodoListByIdAndUserPresenter(responseHandler);
        TodoListByIdAndUserId useCase = new TodoListByIdAndUserId(todoListRepository, outputPort);

        return new TodoListByIdAndUserIdController(useCase);
    }

    private MarkTodoItemAsDoneController createMarkTodoItemAsDoneController() {
        MarkTodoItemAsDone useCase = new MarkTodoItemAsDone(todoListRepository);
        return new MarkTodoItemAsDoneController(useCase);
    }
}
