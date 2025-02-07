package api.commands;

import api.controllers.LoggerController;
import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutionException;

public final class SellCommand extends Command {

    private final Users users;
    private final CryptoAPIClient httpClient;

    private static final String DELIMITER = "=";
    private static final int ASSET_ID_INDEX = 0;

    public SellCommand(Users users, CryptoAPIClient httpClient) {
        this.users = users;
        this.httpClient = httpClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != SELL_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: sell --offering=<offering_code>";
        }

        String assetID = input[ASSET_ID_INDEX].split(DELIMITER)[1];

        return sell(assetID, key);
    }

    private String sell(String assetID, SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login before selling!";
        }

        try {
            return sellHelper(assetID, key);
        } catch (ExecutionException | InterruptedException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            return "Log: Server error when fetching " + assetID + "!";
        }
    }

    private String sellHelper(String assetID, SelectionKey key)
        throws ExecutionException, InterruptedException {
        User loggedUser = (User) key.attachment();

        CryptoResponse assetsByID = this.httpClient.getAssetByID(assetID);

        if (assetsByID.assets() == null || assetsByID.assets().isEmpty()) {
            return "Invalid offering code: " + assetID + " provided!";
        }

        Asset asset = assetsByID.assets().getFirst();
        double priceUSD = asset.priceUSD();

        Asset existingAsset = loggedUser.getAsset(assetID);

        if (existingAsset == null) {
            return "You do not own any " + assetID + "!";
        }

        double profit = priceUSD * existingAsset.amount();

        this.users.removeUser(loggedUser);
        loggedUser.sellAsset(assetID);
        loggedUser.depositMoney(profit);
        this.users.addUser(loggedUser);

        return "Successfully sold " + existingAsset.amount() + " of " + assetID + " for $" + profit + "!";
    }
}
