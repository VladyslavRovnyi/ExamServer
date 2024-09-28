package be.howest.adria.infrastructure.shared.contracts;

public interface Controller<R> {
    public void handle(R request);
}
