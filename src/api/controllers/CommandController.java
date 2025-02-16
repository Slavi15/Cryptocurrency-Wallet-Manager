package api.controllers;

import api.commands.Command;
import api.commands.factory.CommandFactory;
import api.fetch.CryptoAPIClient;
import api.models.input.Input;
import api.models.users.User;
import api.models.users.Users;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.LinkedHashSet;
import java.util.Set;

public class CommandController {

    private final Users users;
    private final CryptoAPIClient cryptoAPIClient;
    private final Set<User> loggedUsers;

    public CommandController(Users users, CryptoAPIClient cryptoAPIClient) {
        this.users = users;
        this.cryptoAPIClient = cryptoAPIClient;
        this.loggedUsers = new LinkedHashSet<>();
    }

    public String executeCommand(String input, SelectionKey key) {
        Input inputCommand = Input.of(input);

        Command command = CommandFactory.of(
            inputCommand.command(),
            this.users,
            this.loggedUsers,
            this.cryptoAPIClient
        );

        String result = command != null ?
            command.execute(inputCommand.args(), key) :
            "Invalid command!";

        try {
            UsersController.writeUsers(this.users);
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage(), exc.getStackTrace());
        }

        return result;
    }

    public Users getUsers() {
        return this.users;
    }
}
