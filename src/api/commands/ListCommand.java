package api.commands;

import api.commands.type.CommandType;
import api.fetch.CryptoAPIClient;
import api.models.http.CryptoResponse;

import java.nio.channels.SelectionKey;

public final class ListCommand extends Command {

    private static final String LIST_COMMAND_INVALID_USAGE = "Usage: list-offerings";
    private static final String LIST_COMMAND_INVALID_PAGES = "You must provide valid page and page size sizes!";
    private static final String LIST_COMMAND_NOT_LOGGED_IN = "You must login before listing offerings!";

    private final CryptoAPIClient cryptoAPIClient;

    private static final String DELIMITER = "=";
    private static final int PAGE_INDEX = 0;
    private static final int PAGE_SIZE_INDEX = 1;
    private static final int COMMAND_ARGS_LENGTH = 2;

    public ListCommand(CryptoAPIClient cryptoAPIClient) {
        this.cryptoAPIClient = cryptoAPIClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.LIST.getArgs()) {
            return LIST_COMMAND_INVALID_USAGE;
        }

        String[] page = input[PAGE_INDEX].split(DELIMITER);
        String[] pageSize = input[PAGE_SIZE_INDEX].split(DELIMITER);

        if (page.length != COMMAND_ARGS_LENGTH || pageSize.length != COMMAND_ARGS_LENGTH) {
            return LIST_COMMAND_INVALID_USAGE;
        }

        try {
            int pageInteger = Integer.parseInt(page[1].trim());
            int pageSizeInteger = Integer.parseInt(pageSize[1].trim());

            if (pageInteger <= 0 || pageSizeInteger <= 0) {
                return LIST_COMMAND_INVALID_PAGES;
            }

            return list(pageInteger, pageSizeInteger, key);
        } catch (NullPointerException | NumberFormatException exc) {
            return LIST_COMMAND_INVALID_PAGES;
        }
    }

    private String list(int page, int pageSize, SelectionKey key) {
        if (key.attachment() == null) {
            return LIST_COMMAND_NOT_LOGGED_IN;
        }

        CryptoResponse offerings = this.cryptoAPIClient.getCryptoAssets(page, pageSize);

        StringBuilder result = new StringBuilder("Cryptocurrency exchange:" + System.lineSeparator());

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
