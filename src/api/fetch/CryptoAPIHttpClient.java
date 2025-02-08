package api.fetch;

import api.exceptions.CoinAPIException;
import api.models.asset.Asset;
import api.models.http.CryptoRequest;
import api.models.http.CryptoResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoAPIHttpClient {

    protected static final String ASSETS_ENDPOINT = "assets";

    private static final String API_SCHEME = "https";
    private static final String API_HOST = "rest.coinapi.io";
    private static final String API_PATH = "/v1/assets/%s";

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    protected final Map<String, CryptoResponse> cryptoAssets;

    protected CryptoAPIHttpClient(String apiKey) {
        this.apiKey = apiKey;

        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.cryptoAssets = new ConcurrentHashMap<>();
    }

    protected CryptoAPIHttpClient(String apiKey, HttpClient httpClient, Map<String, CryptoResponse> cryptoAssets) {
        this.apiKey = apiKey;

        this.httpClient = httpClient;
        this.gson = new Gson();
        this.cryptoAssets = cryptoAssets;
    }

    protected CompletableFuture<CryptoResponse> fetchCryptoAssets(CryptoRequest request) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(getURI(request))
            .header("X-CoinAPI-Key", this.apiKey)
            .GET()
            .build();

        return this.httpClient
            .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(res -> {
                List<Asset> assets = this.gson.fromJson(
                    res.body(),
                    new TypeToken<List<Asset>>() { }.getType());

                List<Asset> filtered = assets.stream()
                    .filter(asset -> asset.isCrypto() == 1 && asset.priceUSD() > 0)
                    .sorted((a, b) -> Double.compare(b.priceUSD(), a.priceUSD()))
                    .toList();

                this.cryptoAssets.put(ASSETS_ENDPOINT, new CryptoResponse(filtered));
                return new CryptoResponse(filtered);
            })
            .exceptionally(exc -> {
                throw new CoinAPIException("Could not fetch Coin API!", exc.getCause());
            });
    }

    private URI getURI(CryptoRequest request) throws Exception {
        return new URI(API_SCHEME, API_HOST, API_PATH.formatted(request.getAssetID()), null);
    }
}
