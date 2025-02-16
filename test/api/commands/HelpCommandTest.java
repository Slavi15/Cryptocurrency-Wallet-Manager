package api.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.channels.SelectionKey;

public class HelpCommandTest {

    private static final String HELP_COMMAND_INVALID_USAGE = "Usage: help";

    @InjectMocks
    private HelpCommand helpCommand;

    @Mock
    private SelectionKey mockKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.helpCommand = new HelpCommand();
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "help" };
        String result = this.helpCommand.execute(input, mockKey);

        assertEquals(HELP_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testHelp_successfulOperation() {
        String[] input = { };
        String result = this.helpCommand.execute(input, mockKey);

        String expected = "Commands:" + System.lineSeparator() +
            "login <email> <password>" + System.lineSeparator() +
            "register <email> <password>" + System.lineSeparator() +
            "deposit <amount>" + System.lineSeparator() +
            "list-offerings --page=<page> --size=<size>" + System.lineSeparator() +
            "buy --offering=<offering_code> --money=<amount>" + System.lineSeparator() +
            "sell --offering=<offering_code>" + System.lineSeparator() +
            "get-wallet-summary" + System.lineSeparator() +
            "get-wallet-overall-summary" + System.lineSeparator() +
            "help" + System.lineSeparator() +
            "logout" + System.lineSeparator();

        assertEquals(expected, result);
    }
}
