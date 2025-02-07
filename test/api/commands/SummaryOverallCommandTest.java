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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryOverallCommandTest {

    private static final String SUMMARY_OVERALL_INVALID_USAGE = "Usage: get-wallet-overall-summary";
    private static final String SUMMARY_OVERALL_NOT_LOGGED = "You must login to get wallet overall summary!";
    private static final String SUMMARY_OVERALL_EMPTY_WALLET = "Empty wallet provided!";
    private static final String SUMMARY_OVERALL_SUCCESSFUL_OPERATION =
        "Wallet Overall Summary" + System.lineSeparator() +
            "Total income: $%f" + System.lineSeparator() +
            "Total expenses: $%f" + System.lineSeparator() +
            "Overall earnings: $%f" + System.lineSeparator();

    @InjectMocks
    private SummaryOverallCommand summaryOverallCommand;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    @Mock
    private CryptoAPIClient httpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "get-wallet-overall" };
        String result = summaryOverallCommand.execute(input, mockKey);

        assertEquals(SUMMARY_OVERALL_INVALID_USAGE, result);
    }

    @Test
    void testSummaryOverall_userNotLogged() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { };
        String result = summaryOverallCommand.execute(input, mockKey);

        assertEquals(SUMMARY_OVERALL_NOT_LOGGED, result);
    }

    @Test
    void testSummaryOverall_emptyWallet() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.mockUser.getWallet()).thenReturn(new HashMap<>());

        String[] input = { };
        String result = summaryOverallCommand.execute(input, mockKey);

        assertEquals(SUMMARY_OVERALL_EMPTY_WALLET, result);
    }

    @Test
    void testSummaryOverall_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        Map<String, Asset> wallet = new HashMap<>();
        wallet.put("BTC", new Asset("BTC", "Bitcoin", 40000.0, 1, 1.0));
        wallet.put("ETH", new Asset("ETH", "Ethereum", 3000.0, 1, 1.0));

        when(this.mockUser.getWallet()).thenReturn(wallet);

        List<Asset> apiAssets = List.of(
            new Asset("BTC", "Bitcoin", 45000.0, 1, 0),
            new Asset("ETH", "Ethereum", 3200.0, 1, 0)
        );
        when(this.httpClient.getCryptoAssets()).thenReturn(new CryptoResponse(apiAssets));

        String[] input = { };
        String result = summaryOverallCommand.execute(input, mockKey);

        String expected = SUMMARY_OVERALL_SUCCESSFUL_OPERATION.formatted(
            48200.0,
            43000.0,
            5200.0
        );

        assertEquals(expected, result);
    }
}
