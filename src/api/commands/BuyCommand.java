package api.commands;

import api.controllers.LoggerController;
import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutionException;

public final class BuyCommand extends Command {

    private final Users users;
    private final CryptoAPIClient httpClient;

    private static final String DELIMITER = "=";
    private static final int ASSET_ID_INDEX = 0;
    private static final int MONEY_AMOUNT_INDEX = 1;

    public BuyCommand(Users users, CryptoAPIClient httpClient) {
        this.users = users;
        this.httpClient = httpClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != BUY_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: buy --offering=<offering_code> --money=<amount>";
        }

        String assetID = input[ASSET_ID_INDEX].split(DELIMITER)[1];
        String moneyToBuy = input[MONEY_AMOUNT_INDEX].split(DELIMITER)[1];

        try {
            double moneyAmount = Double.parseDouble(moneyToBuy);

            if (Double.compare(moneyAmount, 0) <= 0) {
                return "Invalid buy amount provided!";
            }

            return buy(assetID, moneyAmount, key);
        } catch (NullPointerException | NumberFormatException exc) {
            return "Invalid buy amount provided!";
        }
    }

    private String buy(String assetID, double moneyAmount, SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login before buying an asset!";
        }

        try {
            return buyHelper(assetID, moneyAmount, key);
        } catch (ExecutionException | InterruptedException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            return "Log: Server error when fetching " + assetID + "!";
        }
    }

    private String buyHelper(String assetID, double moneyAmount, SelectionKey key)
        throws ExecutionException, InterruptedException {
        User loggedUser = (User) key.attachment();

        CryptoResponse assetsByID = this.httpClient.getAssetByID(assetID);

        if (assetsByID.assets() == null) {
            return "Invalid offering code: " + assetID + " provided!";
        }

        Asset asset = assetsByID.assets().getFirst();
        double priceUSD = asset.priceUSD();
        double amount = moneyAmount / priceUSD;

        if (moneyAmount > loggedUser.getDeposit()) {
            return "Insufficient funds!";
        }

        this.users.removeUser(loggedUser);
        loggedUser.buyAsset(assetID, asset.name(), priceUSD, amount);
        loggedUser.depositMoney(-moneyAmount);
        this.users.addUser(loggedUser);

        return "Successfully bought " + amount + " of " + assetID + "!";
    }
}
