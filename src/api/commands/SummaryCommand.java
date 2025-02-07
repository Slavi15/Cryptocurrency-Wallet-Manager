package api.commands;

import api.models.asset.Asset;
import api.models.users.User;

import java.nio.channels.SelectionKey;

public final class SummaryCommand extends Command {

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: get-wallet-summary";
        }

        return getWalletSummary(key);
    }

    private String getWalletSummary(SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login to get wallet summary!";
        }

        User loggedUser = (User) key.attachment();

        if (loggedUser.getWallet().isEmpty()) {
            return "Nothing to show in wallet!";
        }

        StringBuilder result = new StringBuilder(loggedUser.getUserName() + "'s wallet: " + System.lineSeparator());

        for (Asset asset : loggedUser.getWallet().values()) {
            result.append(asset.getInformation());
        }

        return result.toString();
    }
}
