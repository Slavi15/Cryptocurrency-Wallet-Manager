package api.commands;

import api.commands.type.CommandType;
import api.models.asset.Asset;
import api.models.users.User;

import java.nio.channels.SelectionKey;

public final class SummaryCommand extends Command {

    private static final String SUMMARY_COMMAND_INVALID_USAGE = "Usage: get-wallet-summary";
    private static final String SUMMARY_COMMAND_NOT_LOGGED = "You must login to get wallet summary!";
    private static final String SUMMARY_COMMAND_EMPTY_WALLET = "Nothing to show in wallet!";

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.SUMMARY.getArgs()) {
            return SUMMARY_COMMAND_INVALID_USAGE;
        }

        return getWalletSummary(key);
    }

    private String getWalletSummary(SelectionKey key) {
        if (key.attachment() == null) {
            return SUMMARY_COMMAND_NOT_LOGGED;
        }

        User loggedUser = (User) key.attachment();

        if (loggedUser.getWallet().isEmpty()) {
            return SUMMARY_COMMAND_EMPTY_WALLET;
        }

        StringBuilder result = new StringBuilder("Wallet:" + System.lineSeparator());

        for (Asset asset : loggedUser.getWallet().values()) {
            result.append(asset.getInformation());
        }

        return result.toString();
    }
}
