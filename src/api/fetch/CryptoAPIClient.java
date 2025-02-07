package api.fetch;

import api.models.asset.Asset;
import api.models.http.CryptoResponse;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class CryptoAPIClient extends CryptoAPIHttpClient {

    private static final int MAX_RESULTS = 100;

    public CryptoAPIClient() {

    }

    protected CryptoAPIClient(HttpClient httpClient, Map<String, CryptoResponse> cryptoAssets) {
        super(httpClient, cryptoAssets);
    }

    public CryptoResponse getCryptoAssets() {
        List<Asset> assets = super.cryptoAssets.get(ASSETS_ENDPOINT)
            .assets()
            .stream()
            .limit(MAX_RESULTS)
            .toList();

        return new CryptoResponse(assets);
    }

    public CryptoResponse getAssetByID(String assetID) {
        List<Asset> assetByID = super.cryptoAssets.get(ASSETS_ENDPOINT)
                .assets()
                .stream()
                .filter(asset -> asset.assetID().equals(assetID))
                .toList();

        return new CryptoResponse(assetByID);
    }
}
