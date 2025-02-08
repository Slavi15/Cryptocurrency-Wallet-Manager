package api.commands;

import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicReference;

public final class SummaryOverallCommand extends Command {

    private static final String SUMMARY_OVERALL_INVALID_USAGE = "Usage: get-wallet-overall-summary";
    private static final String SUMMARY_OVERALL_NOT_LOGGED = "You must login to get wallet overall summary!";
    private static final String SUMMARY_OVERALL_EMPTY_WALLET = "Empty wallet provided!";
    private static final String SUMMARY_OVERALL_SUCCESSFUL_OPERATION =
        "Wallet Overall Summary" + System.lineSeparator() +
        "Total income: $%f" + System.lineSeparator() +
        "Total expenses: $%f" + System.lineSeparator() +
        "Overall earnings: $%f" + System.lineSeparator();

    private final CryptoAPIClient cryptoAPIClient;

    public SummaryOverallCommand(CryptoAPIClient cryptoAPIClient) {
        this.cryptoAPIClient = cryptoAPIClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return SUMMARY_OVERALL_INVALID_USAGE;
        }

        return getWalletOverallSummary(key);
    }

    private String getWalletOverallSummary(SelectionKey key) {
        if (key.attachment() == null) {
            return SUMMARY_OVERALL_NOT_LOGGED;
        }

        User loggedUser = (User) key.attachment();

        if (loggedUser.getWallet().isEmpty()) {
            return SUMMARY_OVERALL_EMPTY_WALLET;
        }

        return calculateEarnings(loggedUser);
    }

    private String calculateEarnings(User loggedUser) {
        AtomicReference<Double> totalIncome = new AtomicReference<>(0.0);
        AtomicReference<Double> totalExpenses = new AtomicReference<>(0.0);

        CryptoResponse offerings = this.cryptoAPIClient.getCryptoAssets();

        for (Asset asset : loggedUser.getWallet().values()) {
            offerings.assets()
                .forEach(s -> {
                    if (s.assetID().equals(asset.assetID())) {
                        double currentPrice = s.priceUSD();
                        totalIncome.updateAndGet(v -> v + asset.amount() * currentPrice);
                        totalExpenses.updateAndGet(v -> v + asset.amount() * asset.priceUSD());
                    }
                });
        }

        double overallEarnings = totalIncome.get() - totalExpenses.get();

        return SUMMARY_OVERALL_SUCCESSFUL_OPERATION.formatted(
            totalIncome.getAcquire(),
            totalExpenses.getAcquire(),
            overallEarnings
        );
    }
}
