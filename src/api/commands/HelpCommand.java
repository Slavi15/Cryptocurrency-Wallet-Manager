package api.commands;

import java.nio.channels.SelectionKey;

public final class HelpCommand extends Command {

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: help";
        }

        return help();
    }

    private String help() {
        return "Commands: " + System.lineSeparator() +
            "login <email> <password>" + System.lineSeparator() +
            "register <username> <email> <password>" + System.lineSeparator() +
            "deposit <amount>" + System.lineSeparator() +
            "list-offerings" + System.lineSeparator() +
            "buy --offering=<offering_code> --money=<amount>" + System.lineSeparator() +
            "sell --offering=<offering_code>" + System.lineSeparator() +
            "get-wallet-summary" + System.lineSeparator() +
            "get-wallet-overall-summary" + System.lineSeparator() +
            "help" + System.lineSeparator() +
            "logout" + System.lineSeparator();
    }
}
