package be.howest.adria.application.taskly;

import be.howest.adria.application.contracts.repositories.UserRepository;
import be.howest.adria.application.contracts.usecases.OutputPort;
import be.howest.adria.application.contracts.usecases.UseCase;
import be.howest.adria.domain.taskly.User;

public class CreateUser implements UseCase<CreateUserInput> {
    private final UserRepository userRepository;
    private final OutputPort<CreateUserOutput> outputPort;

    public CreateUser(UserRepository userRepository,
            OutputPort<CreateUserOutput> outputPort) {
        this.userRepository = userRepository;
        this.outputPort = outputPort;
    }

    @Override
    public void execute(CreateUserInput input) {
        User user = User.create(input.userName);
        userRepository.save(user);
        outputPort.present(new CreateUserOutput(user.id()));
    }
}
