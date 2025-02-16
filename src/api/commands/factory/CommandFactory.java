package api.commands.factory;

import api.commands.BuyCommand;
import api.commands.Command;
import api.commands.DepositCommand;
import api.commands.HelpCommand;
import api.commands.ListCommand;
import api.commands.LoginCommand;
import api.commands.LogoutCommand;
import api.commands.RegisterCommand;
import api.commands.SellCommand;
import api.commands.SummaryCommand;
import api.commands.SummaryOverallCommand;
import api.commands.type.CommandType;
import api.controllers.CommandController;
import api.fetch.CryptoAPIClient;
import api.models.input.Input;
import api.models.users.User;
import api.models.users.Users;

import java.util.Arrays;
import java.util.Set;

public record CommandFactory(Input input, CommandController commandController) {

    public static Command of(String inputCommand,
                             Users users,
                             Set<User> loggedUsers,
                             CryptoAPIClient cryptoAPIClient) {
        CommandType commandType = getCommandType(inputCommand);

        return switch (commandType) {
            case LOGIN-> new LoginCommand(users, loggedUsers);
            case REGISTER -> new RegisterCommand(users);
            case DEPOSIT -> new DepositCommand(users);
            case LIST -> new ListCommand(cryptoAPIClient);
            case BUY -> new BuyCommand(users, cryptoAPIClient);
            case SELL -> new SellCommand(users, cryptoAPIClient);
            case SUMMARY -> new SummaryCommand();
            case SUMMARY_OVERALL -> new SummaryOverallCommand(cryptoAPIClient);
            case HELP -> new HelpCommand();
            case LOGOUT -> new LogoutCommand(loggedUsers);
            case null -> null;
        };
    }

    private static CommandType getCommandType(String inputCommand) {
        return Arrays.stream(CommandType.values())
            .filter(type -> type.getCommand().equalsIgnoreCase(inputCommand))
            .findFirst()
            .orElse(null);
    }
}
