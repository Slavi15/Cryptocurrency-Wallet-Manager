package api.commands;

import api.fetch.CryptoAPIClient;
import api.models.http.CryptoResponse;

import java.nio.channels.SelectionKey;

public final class ListCommand extends Command {

    private final CryptoAPIClient cryptoAPIClient;

    public ListCommand(CryptoAPIClient cryptoAPIClient) {
        this.cryptoAPIClient = cryptoAPIClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: list-offerings";
        }

        return list(key);
    }

    private String list(SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login before listing offerings!";
        }

        CryptoResponse offerings = this.cryptoAPIClient.getCryptoAssets();

        StringBuilder result = new StringBuilder("Cryptocurrency exchange: " + System.lineSeparator());

        offerings.assets()
            .forEach(asset ->
                result.append(asset.assetID())
                    .append(": ")
                    .append(asset.name())
                    .append(" - $")
                    .append(asset.priceUSD())
                    .append(System.lineSeparator())
            );

        return result.toString();
    }
}
