package api.commands;

import api.commands.type.CommandType;
import api.controllers.LoggerController;
import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutionException;

public final class SellCommand extends Command {

    private static final String SELL_COMMAND_INVALID_USAGE = "Usage: sell --offering=<offering_code>";
    private static final String SELL_COMMAND_NOT_LOGGED_IN = "You must login before selling!";
    private static final String SELL_COMMAND_LOGS_ERROR = "Log: Server error when fetching %s!";
    private static final String SELL_COMMAND_INVALID_ASSET = "Invalid offering code: %s provided!";
    private static final String SELL_COMMAND_ASSET_NOT_OWNED = "You do not own any %s!";
    private static final String SELL_COMMAND_SUCCESSFUL_OPERATION = "Successfully sold %f of %s for $%f!";

    private final Users users;
    private final CryptoAPIClient httpClient;

    private static final String DELIMITER = "=";
    private static final int ASSET_ID_INDEX = 0;
    private static final int COMMAND_ARGS_LENGTH = 2;

    public SellCommand(Users users, CryptoAPIClient httpClient) {
        this.users = users;
        this.httpClient = httpClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.SELL.getArgs()) {
            return SELL_COMMAND_INVALID_USAGE;
        }

        String[] assetID = input[ASSET_ID_INDEX].split(DELIMITER);

        if (assetID.length != COMMAND_ARGS_LENGTH) {
            return SELL_COMMAND_INVALID_USAGE;
        }

        return sell(assetID[1].trim(), key);
    }

    private String sell(String assetID, SelectionKey key) {
        if (key.attachment() == null) {
            return SELL_COMMAND_NOT_LOGGED_IN;
        }

        try {
            return sellHelper(assetID, key);
        } catch (ExecutionException | InterruptedException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            return SELL_COMMAND_LOGS_ERROR.formatted(assetID);
        }
    }

    private String sellHelper(String assetID, SelectionKey key)
        throws ExecutionException, InterruptedException {
        User loggedUser = (User) key.attachment();

        CryptoResponse assetsByID = this.httpClient.getAssetByID(assetID);

        if (assetsByID.assets() == null || assetsByID.assets().isEmpty()) {
            return SELL_COMMAND_INVALID_ASSET.formatted(assetID);
        }

        Asset asset = assetsByID.assets().getFirst();
        double priceUSD = asset.priceUSD();

        Asset existingAsset = loggedUser.getAsset(assetID);

        if (existingAsset == null) {
            return SELL_COMMAND_ASSET_NOT_OWNED.formatted(assetID);
        }

        double profit = priceUSD * existingAsset.amount();

        this.users.removeUser(loggedUser);
        loggedUser.sellAsset(assetID);
        loggedUser.depositMoney(profit);
        this.users.addUser(loggedUser);

        return SELL_COMMAND_SUCCESSFUL_OPERATION.formatted(existingAsset.amount(), assetID, profit);
    }
}
