package api.commands;

import api.commands.type.CommandType;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;
import java.util.Set;

public final class LoginCommand extends Command {

    private static final String LOGIN_INVALID_COMMAND_USAGE = "Usage: login <email> <password>";
    private static final String LOGIN_COMMAND_USER_NOT_FOUND_DB = "User with email %s does not exist in DB!";
    private static final String LOGIN_COMMAND_INCORRECT_PASSWORD = "Incorrect password!";
    private static final String LOGIN_COMMAND_SUCCESSFUL_LOGIN_DB =
        "User with email %s has been successfully logged into the system!";

    private final Users users;
    private final Set<User> loggedUsers;

    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    public LoginCommand(Users users, Set<User> loggedUsers) {
        this.users = users;
        this.loggedUsers = loggedUsers;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.LOGIN.getArgs()) {
            return LOGIN_INVALID_COMMAND_USAGE;
        }

        return login(
            input[EMAIL_INDEX],
            input[PASSWORD_INDEX],
            key
        );
    }

    private String login(String email, String password, SelectionKey key) {
        User foundUser = this.users.findUser(email);

        if (foundUser == null) {
            return LOGIN_COMMAND_USER_NOT_FOUND_DB.formatted(email);
        }

        if (!foundUser.isPasswordCorrect(password)) {
            return LOGIN_COMMAND_INCORRECT_PASSWORD;
        }

        this.loggedUsers.add(foundUser);
        key.attach(foundUser);

        return LOGIN_COMMAND_SUCCESSFUL_LOGIN_DB.formatted(email);
    }
}
