package be.howest.adria.application.contracts.usecases;

public interface OutputPort<D> {
    void present(D data);
}
