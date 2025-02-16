package api.commands;

import api.models.asset.Asset;
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
import java.util.Map;

public class SummaryCommandTest {

    private static final String SUMMARY_COMMAND_INVALID_USAGE = "Usage: get-wallet-summary";
    private static final String SUMMARY_COMMAND_NOT_LOGGED = "You must login to get wallet summary!";
    private static final String SUMMARY_COMMAND_EMPTY_WALLET = "Nothing to show in wallet!";

    @InjectMocks
    private SummaryCommand summaryCommand;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.summaryCommand = new SummaryCommand();
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "summary" };
        String result = this.summaryCommand.execute(input, mockKey);

        assertEquals(SUMMARY_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testSummary_userNotLogged() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { };
        String result = this.summaryCommand.execute(input, mockKey);

        assertEquals(SUMMARY_COMMAND_NOT_LOGGED, result);
    }

    @Test
    void testSummary_emptyWallet() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.mockUser.getWallet()).thenReturn(new HashMap<>());

        String[] input = { };
        String result = this.summaryCommand.execute(input, mockKey);

        assertEquals(SUMMARY_COMMAND_EMPTY_WALLET, result);
    }

    @Test
    void testSummary_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        Map<String, Asset> wallet = new HashMap<>();
        wallet.put("BTC", new Asset("BTC", "Bitcoin", 200.0, 1, 1.0));
        wallet.put("ETH", new Asset("ETH", "Ethereum", 100.0, 1, 1.0));

        when(this.mockUser.getWallet()).thenReturn(wallet);

        String[] input = { };
        String result = this.summaryCommand.execute(input, mockKey);

        String expected = "Wallet:" + System.lineSeparator() +
            "BTC: Bitcoin - $200.0" + System.lineSeparator() +
            "ETH: Ethereum - $100.0" + System.lineSeparator();

        assertEquals(expected, result);
    }
}
