package be.howest.adria.application.contracts.usecases;

public interface UseCase<I> {
    void execute(I input);
}
