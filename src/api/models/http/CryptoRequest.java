package api.models.http;

public class CryptoRequest {

    private final String assetID;

    private CryptoRequest(Builder builder) {
        this.assetID = builder.assetID;
    }

    public String getAssetID() {
        return this.assetID;
    }

    public static class Builder {
        private String assetID = "";

        public Builder assetID(String assetID) {
            this.assetID = assetID;
            return this;
        }

        public CryptoRequest build() {
            return new CryptoRequest(this);
        }
    }
}
