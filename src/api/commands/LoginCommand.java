package api.commands;

import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;
import java.util.Set;

public final class LoginCommand extends Command {

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
        if (input.length != LOGIN_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: login <email> <password>";
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
            return "User with email " + email + " does not exist in DB!";
        }

        if (!foundUser.isPasswordCorrect(password)) {
            return "Incorrect password!";
        }

        this.loggedUsers.add(foundUser);
        key.attach(foundUser);

        return "User with email " + email + " has been successfully logged into the system!";
    }
}
