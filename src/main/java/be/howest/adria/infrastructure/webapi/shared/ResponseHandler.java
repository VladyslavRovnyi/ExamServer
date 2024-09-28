package be.howest.adria.infrastructure.webapi.shared;

public interface ResponseHandler<R> {
    void handle(R response);
}
