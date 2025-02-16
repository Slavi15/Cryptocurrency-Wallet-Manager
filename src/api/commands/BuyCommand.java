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

public final class BuyCommand extends Command {

    private static final String BUY_COMMAND_INVALID_USAGE = "Usage: buy --offering=<offering_code> --money=<amount>";
    private static final String BUY_COMMAND_INVALID_AMOUNT = "Invalid buy amount provided!";
    private static final String BUY_COMMAND_NOT_LOGGED_IN = "You must login before buying an asset!";
    private static final String BUY_COMMAND_LOGS_ERROR = "Log: Server error when fetching %s!";
    private static final String BUY_COMMAND_INVALID_ASSET = "Invalid offering code: %s provided!";
    private static final String BUY_COMMAND_INSUFFICIENT_FUNDS = "Insufficient funds!";
    private static final String BUY_COMMAND_SUCCESSFUL_OPERATION = "Successfully bought %f of %s!";

    private final Users users;
    private final CryptoAPIClient httpClient;

    private static final String DELIMITER = "=";
    private static final int ASSET_ID_INDEX = 0;
    private static final int MONEY_AMOUNT_INDEX = 1;
    private static final int COMMAND_ARGS_LENGTH = 2;

    public BuyCommand(Users users, CryptoAPIClient httpClient) {
        this.users = users;
        this.httpClient = httpClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.BUY.getArgs()) {
            return BUY_COMMAND_INVALID_USAGE;
        }

        String[] assetID = input[ASSET_ID_INDEX].split(DELIMITER);
        String[] moneyToBuy = input[MONEY_AMOUNT_INDEX].split(DELIMITER);

        if (assetID.length != COMMAND_ARGS_LENGTH || moneyToBuy.length != COMMAND_ARGS_LENGTH) {
            return BUY_COMMAND_INVALID_USAGE;
        }

        try {
            double moneyAmount = Double.parseDouble(moneyToBuy[1].trim());

            if (Double.compare(moneyAmount, 0) <= 0) {
                return BUY_COMMAND_INVALID_AMOUNT;
            }

            return buy(assetID[1].trim(), moneyAmount, key);
        } catch (NullPointerException | NumberFormatException exc) {
            return BUY_COMMAND_INVALID_AMOUNT;
        }
    }

    private String buy(String assetID, double moneyAmount, SelectionKey key) {
        if (key.attachment() == null) {
            return BUY_COMMAND_NOT_LOGGED_IN;
        }

        try {
            return buyHelper(assetID, moneyAmount, key);
        } catch (ExecutionException | InterruptedException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            return BUY_COMMAND_LOGS_ERROR.formatted(assetID);
        }
    }

    private String buyHelper(String assetID, double moneyAmount, SelectionKey key)
        throws ExecutionException, InterruptedException {
        User loggedUser = (User) key.attachment();

        CryptoResponse assetsByID = this.httpClient.getAssetByID(assetID);

        if (assetsByID.assets() == null || assetsByID.assets().isEmpty()) {
            return BUY_COMMAND_INVALID_ASSET.formatted(assetID);
        }

        Asset asset = assetsByID.assets().getFirst();
        double priceUSD = asset.priceUSD();
        double amount = moneyAmount / priceUSD;

        if (moneyAmount > loggedUser.getDeposit()) {
            return BUY_COMMAND_INSUFFICIENT_FUNDS;
        }

        this.users.removeUser(loggedUser);
        loggedUser.buyAsset(assetID, asset.name(), priceUSD, amount);
        loggedUser.depositMoney(-moneyAmount);
        this.users.addUser(loggedUser);

        return BUY_COMMAND_SUCCESSFUL_OPERATION.formatted(amount, assetID);
    }
}
