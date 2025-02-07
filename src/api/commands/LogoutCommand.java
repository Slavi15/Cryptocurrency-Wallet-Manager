package api.commands;

import api.models.users.User;

import java.nio.channels.SelectionKey;
import java.util.Set;

public final class LogoutCommand extends Command {

    private final Set<User> loggedUsers;

    public LogoutCommand(Set<User> loggedUsers) {
        this.loggedUsers = loggedUsers;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: logout";
        }

        return logout(key);
    }

    private String logout(SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login before logging out!";
        }

        User loggedUser = (User) key.attachment();
        this.loggedUsers.remove(loggedUser);
        key.attach(null);

        return "You have successfully logged out!";
    }
}
