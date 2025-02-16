package api.fetch;

import api.controllers.LoggerController;
import api.models.http.CryptoRequest;
import api.models.http.CryptoResponse;

public class CryptoAPIClientRunnable implements Runnable {

    private final CryptoAPIClient cryptoAPIClient;

    public CryptoAPIClientRunnable(CryptoAPIClient cryptoAPIClient) {
        this.cryptoAPIClient = cryptoAPIClient;
    }

    @Override
    public void run() {
        try {
            CryptoRequest request = new CryptoRequest.Builder().build();
            CryptoResponse cryptoResponse = this.cryptoAPIClient.fetchCryptoAssets(request).join();
            System.out.println("Fetched " + cryptoResponse.assets().size() + " crypto assets!");
        } catch (Exception exc) {
            LoggerController.writeLogsErrors(exc.getMessage(), exc.getStackTrace());
        }
    }
}
