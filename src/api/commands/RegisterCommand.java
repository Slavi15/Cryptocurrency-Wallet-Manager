package api.commands;

import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;

public final class RegisterCommand extends Command {

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
            return "Usage: register <username> <email> <password>";
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
            return "User " + userName + " is already logged in!";
        }

        User foundUser = this.users.findUser(email);

        if (foundUser != null) {
            return "User " + userName + " already exists in DB!";
        }

        if (!email.matches(EMAIL_REGEX)) {
            return "Invalid email address provided!";
        }

        if (!userName.matches(NAME_REGEX)) {
            return "Invalid username provided!";
        }

        User toAdd = new User(userName, email, password);
        this.users.addUser(toAdd);

        return "User " + userName + " has been successfully registered!";
    }
}
