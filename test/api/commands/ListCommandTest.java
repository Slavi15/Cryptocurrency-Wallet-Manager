package api.commands;

import api.fetch.CryptoAPIClient;
import api.models.asset.Asset;
import api.models.http.CryptoResponse;
import api.models.users.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.nio.channels.SelectionKey;
import java.util.List;

public class ListCommandTest {

    private static final String LIST_COMMAND_INVALID_USAGE = "Usage: list-offerings --page=<page> --size=<size>";
    private static final String LIST_COMMAND_NOT_LOGGED_IN = "You must login before listing offerings!";

    @InjectMocks
    private ListCommand listCommand;

    @Mock
    private User mockUser;

    @Mock
    private Asset mockAsset1, mockAsset2;

    @Mock
    private CryptoAPIClient httpClient;

    @Mock
    private SelectionKey mockKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.listCommand = new ListCommand(this.httpClient);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "test" };
        String result = listCommand.execute(input, mockKey);

        assertEquals(LIST_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testList_userNotLogged() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { "--page=1", "--size=20" };
        String result = listCommand.execute(input, mockKey);

        assertEquals(LIST_COMMAND_NOT_LOGGED_IN, result);
    }

    @Test
    void testList_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        when(this.mockAsset1.assetID()).thenReturn("BTC");
        when(this.mockAsset1.name()).thenReturn("Bitcoin");
        when(this.mockAsset1.priceUSD()).thenReturn(200.0);
        when(this.mockAsset1.isCrypto()).thenReturn(1);
        when(this.mockAsset1.amount()).thenReturn(0.0);

        when(this.mockAsset2.assetID()).thenReturn("ETH");
        when(this.mockAsset2.name()).thenReturn("Ethereum");
        when(this.mockAsset2.priceUSD()).thenReturn(100.0);
        when(this.mockAsset2.isCrypto()).thenReturn(1);
        when(this.mockAsset2.amount()).thenReturn(0.0);

        when(this.httpClient.getCryptoAssets(1, 20)).thenReturn(
            new CryptoResponse(List.of(this.mockAsset1, this.mockAsset2))
        );

        String[] input = { "--page=1", "--size=20" };
        String result = listCommand.execute(input, mockKey);

        String expected = "Cryptocurrency exchange:" + System.lineSeparator() +
            "BTC: Bitcoin - $200.0" + System.lineSeparator() +
            "ETH: Ethereum - $100.0" + System.lineSeparator();

        assertEquals(expected, result);
    }
}
