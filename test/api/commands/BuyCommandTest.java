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

public class BuyCommandTest {

    private static final String BUY_COMMAND_INVALID_USAGE = "Usage: buy --offering=<offering_code> --money=<amount>";
    private static final String BUY_COMMAND_INVALID_AMOUNT = "Invalid buy amount provided!";
    private static final String BUY_COMMAND_NOT_LOGGED_IN = "You must login before buying an asset!";
    private static final String BUY_COMMAND_INVALID_ASSET = "Invalid offering code: %s provided!";
    private static final String BUY_COMMAND_INSUFFICIENT_FUNDS = "Insufficient funds!";
    private static final String BUY_COMMAND_SUCCESSFUL_OPERATION = "Successfully bought %f of %s!";

    @InjectMocks
    private BuyCommand buyCommand;

    @Mock
    private Users mockUsers;

    @Mock
    private User mockUser;

    @Mock
    private Asset mockAsset;

    @Mock
    private SelectionKey mockKey;

    @Mock
    private CryptoAPIClient httpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.buyCommand = new BuyCommand(this.mockUsers, this.httpClient);
    }

    @Test
    void testExecute_invalidArgsCount() {
        String[] input = { "--offering=", "--money=" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "--offering=BTC" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testExecute_invalidBuyAmount() {
        String[] input = { "--offering=BTC", "--money=abc" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INVALID_AMOUNT, result);
    }

    @Test
    void testExecute_negativeBuyAmount() {
        String[] input = { "--offering=BTC", "--money=-100" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INVALID_AMOUNT, result);
    }

    @Test
    void testBuy_userNotLogged() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { "--offering=BTC", "--money=100" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_NOT_LOGGED_IN, result);
    }

    @Test
    void testBuy_invalidAssetProvided() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        when(this.httpClient.getAssetByID("BTH")).thenReturn(new CryptoResponse(null));

        String[] input = { "--offering=BTH", "--money=100" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INVALID_ASSET.formatted("BTH"), result);
    }

    @Test
    void testBuy_insufficientFunds() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.mockUser.getDeposit()).thenReturn(50.0);

        when(this.mockAsset.assetID()).thenReturn("BTC");
        when(this.mockAsset.name()).thenReturn("Bitcoin");
        when(this.mockAsset.priceUSD()).thenReturn(200.0);
        when(this.mockAsset.isCrypto()).thenReturn(1);
        when(this.mockAsset.amount()).thenReturn(0.0);

        when(this.httpClient.getAssetByID("BTC")).thenReturn(
            new CryptoResponse(List.of(this.mockAsset))
        );

        String[] input = { "--offering=BTC", "--money=100" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_INSUFFICIENT_FUNDS, result);
    }

    @Test
    void testBuy_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.mockUser.getDeposit()).thenReturn(1000.0);

        when(this.mockAsset.assetID()).thenReturn("BTC");
        when(this.mockAsset.name()).thenReturn("Bitcoin");
        when(this.mockAsset.priceUSD()).thenReturn(200.0);
        when(this.mockAsset.isCrypto()).thenReturn(1);
        when(this.mockAsset.amount()).thenReturn(0.0);

        when(this.httpClient.getAssetByID("BTC")).thenReturn(
            new CryptoResponse(List.of(this.mockAsset))
        );

        String[] input = { "--offering=BTC", "--money=100" };
        String result = buyCommand.execute(input, mockKey);

        assertEquals(BUY_COMMAND_SUCCESSFUL_OPERATION.formatted(0.5, "BTC"), result);
    }
}
