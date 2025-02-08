package api.fetch;

import api.exceptions.CoinAPIException;
import api.models.asset.Asset;
import api.models.http.CryptoRequest;
import api.models.http.CryptoResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoAPIClientTest {

    @InjectMocks
    private CryptoAPIClient cryptoAPIClient;

    private Gson gson;

    @Mock
    private HttpClient httpClient;

    @Mock
    private CryptoRequest mockRequest;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @Mock
    private Asset mockAsset;

    private Map<String, CryptoResponse> cryptoAssets;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(this.mockAsset.assetID()).thenReturn("BTC");
        when(this.mockAsset.name()).thenReturn("Bitcoin");
        when(this.mockAsset.priceUSD()).thenReturn(200.0);
        when(this.mockAsset.isCrypto()).thenReturn(1);
        when(this.mockAsset.amount()).thenReturn(1.0);

        this.gson = new Gson();
        this.cryptoAssets = new ConcurrentHashMap<>();
        this.cryptoAPIClient = new CryptoAPIClient("test-api-key", this.httpClient, this.cryptoAssets);
    }

    @Test
    void testGetCryptoAssets_existingAssets() {
        List<Asset> assets = List.of(this.mockAsset);
        this.cryptoAssets.put("assets", new CryptoResponse(assets));

        CryptoResponse response = this.cryptoAPIClient.getCryptoAssets();

        assertNotNull(response, "Response should not be null!");
        assertEquals(1, response.assets().size(), "Response size should be of correct size!");
        assertEquals("BTC", response.assets().getFirst().assetID(),
            "Asset ID should be correct!");
    }

    @Test
    void testGetCryptoAssets_nonExistingAssets() {
        this.cryptoAssets.put("assets", new CryptoResponse(List.of()));
        CryptoResponse response = this.cryptoAPIClient.getCryptoAssets();

        assertNotNull(response, "Response should not be null!");
        assertTrue(response.assets().isEmpty(), "Response collection should be empty!");
    }

    @Test
    void testGetAssetByID_existingAsset() {
        List<Asset> assets = List.of(this.mockAsset);
        this.cryptoAssets.put("assets", new CryptoResponse(assets));

        CryptoResponse response = this.cryptoAPIClient.getAssetByID("BTC");

        assertNotNull(response, "Response should not be null!");
        assertEquals(1, response.assets().size(), "Response size should be of correct size!");
        assertEquals("BTC", response.assets().getFirst().assetID(),
            "Asset ID should be correct!");
    }

    @Test
    void testGetAssetByID_nonExistingAsset() {
        List<Asset> assets = List.of(this.mockAsset);
        this.cryptoAssets.put("assets", new CryptoResponse(assets));

        CryptoResponse response = this.cryptoAPIClient.getAssetByID("ETH");

        assertNotNull(response, "Response should not be null!");
        assertTrue(response.assets().isEmpty(), "Response should be empty!");
    }

    @Test
    void testFetchCryptoAssets_validResponse() throws Exception {
        List<Asset> assets = List.of(this.mockAsset);
        CryptoResponse apiResponse = new CryptoResponse(assets);

        String jsonResponse = this.gson.toJson(apiResponse, CryptoResponse.class);

        when(this.mockHttpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(this.mockHttpResponse.body()).thenReturn(jsonResponse);

        when(this.httpClient.sendAsync(
            any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(CompletableFuture.completedFuture(this.mockHttpResponse));

        when(this.mockRequest.getAssetID()).thenReturn("BTC");
        CompletableFuture<CryptoResponse> future = this.cryptoAPIClient.fetchCryptoAssets(this.mockRequest);

        CryptoResponse response = future.join();

        assertNotNull(response, "Response should not be null!");
        assertEquals(1, response.assets().size(),
            "Response should be of correct size!");
        assertEquals("BTC", response.assets().getFirst().assetID(),
            "Asset ID should be correct!");
    }

    @Test
    void testFetchCryptoAssets_serverError() throws Exception {
        when(this.mockHttpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        when(this.mockHttpResponse.body()).thenReturn(null);

        when(this.httpClient.sendAsync(
            any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(CompletableFuture.completedFuture(this.mockHttpResponse));

        CompletionException exception = assertThrows(CompletionException.class,
            () -> this.cryptoAPIClient.fetchCryptoAssets(this.mockRequest).join(),
            "Internal server error!");

        assertInstanceOf(CoinAPIException.class, exception.getCause(),
            "Coin API Exception due to internal server error!");
    }
}
