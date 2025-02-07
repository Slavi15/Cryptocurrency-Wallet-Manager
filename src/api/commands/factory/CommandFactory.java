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
import api.controllers.CommandController;
import api.fetch.CryptoAPIClient;
import api.models.input.Input;
import api.models.users.User;
import api.models.users.Users;

import java.util.Set;

public record CommandFactory(Input input, CommandController commandController) {

    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String DEPOSIT = "deposit-money";
    private static final String LIST_OFFERINGS = "list-offerings";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
    private static final String GET_WALLET_SUMMARY = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY = "get-wallet-overall-summary";
    private static final String HELP = "help";
    private static final String LOGOUT = "logout";

    public static Command of(String inputCommand,
                             Users users,
                             Set<User> loggedUsers,
                             CryptoAPIClient cryptoAPIClient) {
        return switch (inputCommand) {
            case LOGIN -> new LoginCommand(users, loggedUsers);
            case REGISTER -> new RegisterCommand(users);
            case DEPOSIT -> new DepositCommand(users);
            case LIST_OFFERINGS -> new ListCommand(cryptoAPIClient);
            case BUY -> new BuyCommand(users, cryptoAPIClient);
            case SELL -> new SellCommand(users, cryptoAPIClient);
            case GET_WALLET_SUMMARY -> new SummaryCommand();
            case GET_WALLET_OVERALL_SUMMARY -> new SummaryOverallCommand(cryptoAPIClient);
            case HELP -> new HelpCommand();
            case LOGOUT -> new LogoutCommand(loggedUsers);
            default -> null;
        };
    }
}
