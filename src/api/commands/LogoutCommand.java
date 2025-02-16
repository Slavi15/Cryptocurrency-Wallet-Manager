package api.commands;

import api.commands.type.CommandType;
import api.models.users.User;

import java.nio.channels.SelectionKey;
import java.util.Set;

public final class LogoutCommand extends Command {

    private static final String LOGOUT_COMMAND_INVALID_COMMAND_USAGE = "Usage: logout";
    private static final String LOGOUT_COMMAND_NOT_LOGGED_IN = "You must login before logging out!";
    private static final String LOGOUT_COMMAND_SUCCESSFUL_OPERATION = "You have successfully logged out!";

    private final Set<User> loggedUsers;

    public LogoutCommand(Set<User> loggedUsers) {
        this.loggedUsers = loggedUsers;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.LOGOUT.getArgs()) {
            return LOGOUT_COMMAND_INVALID_COMMAND_USAGE;
        }

        return logout(key);
    }

    private String logout(SelectionKey key) {
        if (key.attachment() == null) {
            return LOGOUT_COMMAND_NOT_LOGGED_IN;
        }

        User loggedUser = (User) key.attachment();
        this.loggedUsers.remove(loggedUser);
        key.attach(null);

        return LOGOUT_COMMAND_SUCCESSFUL_OPERATION;
    }
}
