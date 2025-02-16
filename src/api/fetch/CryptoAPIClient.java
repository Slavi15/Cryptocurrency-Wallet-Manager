package api.fetch;

import api.models.asset.Asset;
import api.models.http.CryptoResponse;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class CryptoAPIClient extends CryptoAPIHttpClient {

    public CryptoAPIClient(String apiKey) {
        super(apiKey);
    }

    protected CryptoAPIClient(String apiKey, HttpClient httpClient, Map<String, CryptoResponse> cryptoAssets) {
        super(apiKey, httpClient, cryptoAssets);
    }

    public CryptoResponse getCryptoAssets() {
        return super.cryptoAssets.get(ASSETS_ENDPOINT);
    }

    public CryptoResponse getCryptoAssets(int page, int pageSize) {
        int toSkip = (page - 1) * pageSize;

        List<Asset> assets = super.cryptoAssets.get(ASSETS_ENDPOINT)
            .assets()
            .stream()
            .skip(toSkip)
            .limit(pageSize)
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
