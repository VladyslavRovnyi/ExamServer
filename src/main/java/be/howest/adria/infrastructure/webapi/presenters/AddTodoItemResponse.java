package be.howest.adria.infrastructure.webapi.presenters;

public class AddTodoItemResponse {

    public final String todoItemId;
    public final String todoListId;

    public AddTodoItemResponse(String todoListId, String todoItemId) {
        this.todoItemId = todoItemId;
        this.todoListId = todoListId;
    }
}
