package api.commands;

import api.commands.type.CommandType;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;

public final class RegisterCommand extends Command {

    private static final String REGISTER_COMMAND_INVALID_COMMAND_USAGE =
        "Usage: register <email> <password>";
    private static final String REGISTER_COMMAND_USER_LOGGED_IN = "User with email %s is already logged in!";
    private static final String REGISTER_COMMAND_USER_ALREADY_REGISTERED = "User with email %s already exists in DB!";
    private static final String REGISTER_COMMAND_INVALID_EMAIL = "Invalid email address provided!";
    private static final String REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB =
        "User with email %s has been successfully registered!";

    private final Users users;

    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    public RegisterCommand(Users users) {
        this.users = users;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.REGISTER.getArgs()) {
            return REGISTER_COMMAND_INVALID_COMMAND_USAGE;
        }

        return register(
            input[EMAIL_INDEX],
            input[PASSWORD_INDEX],
            key
        );
    }

    private String register(String email,
                            String password,
                            SelectionKey key) {
        if (key.attachment() != null) {
            return REGISTER_COMMAND_USER_LOGGED_IN.formatted(email);
        }

        User foundUser = this.users.findUser(email);

        if (foundUser != null) {
            return REGISTER_COMMAND_USER_ALREADY_REGISTERED.formatted(email);
        }

        if (!email.matches(EMAIL_REGEX)) {
            return REGISTER_COMMAND_INVALID_EMAIL;
        }

        User toAdd = new User(email, password);
        this.users.addUser(toAdd);

        return REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB.formatted(email);
    }
}
