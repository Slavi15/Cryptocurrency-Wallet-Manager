package api.commands;

import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;

public final class RegisterCommand extends Command {

    private static final String REGISTER_COMMAND_INVALID_COMMAND_USAGE =
        "Usage: register <username> <email> <password>";
    private static final String REGISTER_COMMAND_USER_LOGGED_IN = "User %s is already logged in!";
    private static final String REGISTER_COMMAND_USER_ALREADY_REGISTERED = "User %s already exists in DB!";
    private static final String REGISTER_COMMAND_INVALID_EMAIL = "Invalid email address provided!";
    private static final String REGISTER_COMMAND_INVALID_USERNAME = "Invalid username provided!";
    private static final String REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB = "User %s has been successfully registered!";

    private final Users users;

    private static final int USERNAME_INDEX = 0;
    private static final int EMAIL_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    public RegisterCommand(Users users) {
        this.users = users;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != REGISTER_COMMAND_ARGUMENTS_LENGTH) {
            return REGISTER_COMMAND_INVALID_COMMAND_USAGE;
        }

        return register(
            input[USERNAME_INDEX],
            input[EMAIL_INDEX],
            input[PASSWORD_INDEX],
            key
        );
    }

    private String register(String userName,
                            String email,
                            String password,
                            SelectionKey key) {
        if (key.attachment() != null) {
            return REGISTER_COMMAND_USER_LOGGED_IN.formatted(userName);
        }

        User foundUser = this.users.findUser(email);

        if (foundUser != null) {
            return REGISTER_COMMAND_USER_ALREADY_REGISTERED.formatted(userName);
        }

        if (!email.matches(EMAIL_REGEX)) {
            return REGISTER_COMMAND_INVALID_EMAIL;
        }

        if (!userName.matches(NAME_REGEX)) {
            return REGISTER_COMMAND_INVALID_USERNAME;
        }

        User toAdd = new User(userName, email, password);
        this.users.addUser(toAdd);

        return REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB.formatted(userName);
    }
}
