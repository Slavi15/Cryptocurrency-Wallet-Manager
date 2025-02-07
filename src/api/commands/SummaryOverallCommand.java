package api.commands;

import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicReference;

public final class SummaryOverallCommand extends Command {

    private final CryptoAPIClient cryptoAPIClient;

    public SummaryOverallCommand(CryptoAPIClient cryptoAPIClient) {
        this.cryptoAPIClient = cryptoAPIClient;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: get-wallet-overall-summary";
        }

        return getWalletOverallSummary(key);
    }

    private String getWalletOverallSummary(SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login to get wallet overall summary!";
        }

        User loggedUser = (User) key.attachment();

        if (loggedUser.getWallet().isEmpty()) {
            return "Empty wallet provided!";
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

        return "Wallet Overall Summary" + System.lineSeparator() +
            "Total income: " + totalIncome + "$" + System.lineSeparator() +
            "Total expenses: " + totalExpenses + "$" + System.lineSeparator() +
            "Overall earnings: " + overallEarnings + "$" + System.lineSeparator();
    }
}
