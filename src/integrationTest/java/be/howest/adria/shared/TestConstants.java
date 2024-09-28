package be.howest.adria.shared;

public class TestConstants {
    public static final String API_URL = "http://localhost:8080/api/";
    public static final String API_URL_USER = API_URL + "users";
    public static final String API_URL_HEALTH_CHECK = API_URL + "health";
    public static final String API_URL_SUBSCRIBE = API_URL + "subscribe";
    public static final String API_URL_TODOLIST = API_URL + "todolists";
    public static final String API_URL_TODOLIST_ITEMS = API_URL + "todolists/%s/items";
    public static final String API_URL_TODOLIST_ITEM = API_URL + "todolists/%s/items/%s";
    public static final String API_URL_MOVE_TODOITEM_TO_OTHER_LIST = API_URL + "todolists/%s/items/%s/move";
    public static final String API_URL_ALL_TRASHED_TODOLISTS = API_URL + "todolists/%s/trash";
}
