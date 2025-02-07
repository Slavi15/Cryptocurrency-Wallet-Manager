package api.models.http;

import api.models.asset.Asset;

import java.util.List;

public record CryptoResponse(
    List<Asset> assets
) {

}
