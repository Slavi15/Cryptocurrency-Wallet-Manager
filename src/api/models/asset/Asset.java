package api.models.asset;

import com.google.gson.annotations.SerializedName;

public record Asset(
    @SerializedName("asset_id")
    String assetID,
    @SerializedName("name")
    String name,
    @SerializedName("price_usd")
    double priceUSD,
    @SerializedName("type_is_crypto")
    int isCrypto,
    double amount
) {

    public String getInformation() {
        return assetID + ": " + name + " - $" + priceUSD + System.lineSeparator();
    }
}
