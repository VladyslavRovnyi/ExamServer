package be.howest.adria.application.taskly;

import java.util.UUID;

public class MarkTodoItemAsFavoriteInput {
    private final UUID listId;
    private final UUID itemId;

    public MarkTodoItemAsFavoriteInput(UUID listId, UUID itemId) {
        this.listId = listId;
        this.itemId = itemId;
    }

    public UUID getListId() {
        return listId;
    }

    public UUID getItemId() {
        return itemId;
    }
}
