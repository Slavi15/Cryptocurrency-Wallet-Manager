package api.commands;

import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;
import api.models.users.Users;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.nio.channels.SelectionKey;
import java.util.List;

public class SellCommandTest {

    private static final String SELL_COMMAND_INVALID_USAGE = "Usage: sell --offering=<offering_code>";
    private static final String SELL_COMMAND_NOT_LOGGED_IN = "You must login before selling!";
    private static final String SELL_COMMAND_INVALID_ASSET = "Invalid offering code: %s provided!";
    private static final String SELL_COMMAND_ASSET_NOT_OWNED = "You do not own any %s!";
    private static final String SELL_COMMAND_SUCCESSFUL_OPERATION = "Successfully sold %f of %s for $%f!";

    @InjectMocks
    private SellCommand sellCommand;

    @Mock
    private Users mockUsers;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    @Mock
    private CryptoAPIClient httpClient;

    @Mock
    private Asset mockAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.sellCommand = new SellCommand(this.mockUsers, this.httpClient);
    }

    @Test
    void testExecute_invalidArgsCount() {
        String[] input = { };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "--offering" };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testSelling_userNotLoggedIn() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { "--offering=BTC" };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_NOT_LOGGED_IN, result);
    }

    @Test
    void testSelling_invalidAssetProvided() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.httpClient.getAssetByID("BTC")).thenReturn(new CryptoResponse(List.of()));

        String[] input = { "--offering=BTC" };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_INVALID_ASSET.formatted("BTC"), result);
    }

    @Test
    void testSelling_notOwnedAsset() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        when(this.mockAsset.assetID()).thenReturn("BTC");
        when(this.mockAsset.name()).thenReturn("Bitcoin");
        when(this.mockAsset.priceUSD()).thenReturn(200.0);
        when(this.mockAsset.isCrypto()).thenReturn(1);
        when(this.mockAsset.amount()).thenReturn(0.0);

        when(this.httpClient.getAssetByID("BTC")).thenReturn(
            new CryptoResponse(List.of(this.mockAsset))
        );
        when(this.mockUser.getAsset("BTC")).thenReturn(null);

        String[] input = { "--offering=BTC" };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_ASSET_NOT_OWNED.formatted("BTC"), result);
    }

    @Test
    void testSelling_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        when(this.mockAsset.assetID()).thenReturn("BTC");
        when(this.mockAsset.name()).thenReturn("Bitcoin");
        when(this.mockAsset.priceUSD()).thenReturn(200.0);
        when(this.mockAsset.isCrypto()).thenReturn(1);
        when(this.mockAsset.amount()).thenReturn(1.5);

        when(this.httpClient.getAssetByID("BTC")).thenReturn(
            new CryptoResponse(List.of(this.mockAsset))
        );
        when(this.mockUser.getAsset("BTC")).thenReturn(this.mockAsset);

        String[] input = { "--offering=BTC" };
        String result = sellCommand.execute(input, mockKey);

        assertEquals(SELL_COMMAND_SUCCESSFUL_OPERATION.formatted(1.5, "BTC", 300.0), result);
    }
}
