package api.fetch;

import api.models.asset.Asset;
import api.models.http.CryptoResponse;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class CryptoAPIClient extends CryptoAPIHttpClient {

    public CryptoAPIClient() {

    }

    protected CryptoAPIClient(HttpClient httpClient, Map<String, CryptoResponse> cryptoAssets) {
        super(httpClient, cryptoAssets);
    }

    public CryptoResponse getCryptoAssets() {
        return super.cryptoAssets.get(ASSETS_ENDPOINT);
    }

    public CryptoResponse getAssetByID(String assetID) {
        List<Asset> assetByID = getCryptoAssets().assets()
            .stream()
            .filter(asset -> asset.assetID().equals(assetID))
            .toList();

        return new CryptoResponse(assetByID);
    }
}
