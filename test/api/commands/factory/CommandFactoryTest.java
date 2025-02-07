package api.commands.factory;

import api.commands.BuyCommand;
import api.commands.Command;
import api.commands.DepositCommand;
import api.commands.HelpCommand;
import api.commands.ListCommand;
import api.commands.LoginCommand;
import api.commands.LogoutCommand;
import api.commands.RegisterCommand;
import api.commands.SellCommand;
import api.commands.SummaryCommand;
import api.commands.SummaryOverallCommand;
import api.fetch.CryptoAPIClient;
import api.models.users.User;
import api.models.users.Users;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

public class CommandFactoryTest {

    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String DEPOSIT = "deposit-money";
    private static final String LIST_OFFERINGS = "list-offerings";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
    private static final String GET_WALLET_SUMMARY = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY = "get-wallet-overall-summary";
    private static final String HELP = "help";
    private static final String LOGOUT = "logout";

    @Mock
    private Users mockUsers;

    @Mock
    private CryptoAPIClient httpClient;

    @Mock
    private Set<User> loggedUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCommandFactory_loginCommand() {
        Command command = CommandFactory.of(LOGIN, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(LoginCommand.class, command, "Command should be of login type!");
    }

    @Test
    void testCommandFactory_registerCommand() {
        Command command = CommandFactory.of(REGISTER, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(RegisterCommand.class, command,
            "Command should be of register type!");
    }

    @Test
    void testCommandFactory_depositCommand() {
        Command command = CommandFactory.of(DEPOSIT, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(DepositCommand.class, command,
            "Command, should be of deposit type!");
    }

    @Test
    void testCommandFactory_listCommand() {
        Command command = CommandFactory.of(LIST_OFFERINGS, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(ListCommand.class, command,
            "Command should be of list-offerings type!");
    }

    @Test
    void testCommandFactory_buyCommand() {
        Command command = CommandFactory.of(BUY, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(BuyCommand.class, command,
            "Command should be of buy type!");
    }

    @Test
    void testCommandFactory_sellCommand() {
        Command command = CommandFactory.of(SELL, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(SellCommand.class, command,
            "Command should be of sell type!");
    }

    @Test
    void testCommandFactory_summaryCommand() {
        Command command = CommandFactory.of(GET_WALLET_SUMMARY, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(SummaryCommand.class, command,
            "Command should be of summary type!");
    }

    @Test
    void testCommandFactory_summaryOverallCommand() {
        Command command = CommandFactory.of(GET_WALLET_OVERALL_SUMMARY, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(SummaryOverallCommand.class, command,
            "Command should be of overall summary type!");
    }

    @Test
    void testCommandFactory_helpCommand() {
        Command command = CommandFactory.of(HELP, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(HelpCommand.class, command,
            "Command should be of help type!");
    }

    @Test
    void testCommandFactory_logoutCommand() {
        Command command = CommandFactory.of(LOGOUT, this.mockUsers, this.loggedUsers, this.httpClient);

        assertNotNull(command, "Command should not be null!");
        assertInstanceOf(LogoutCommand.class, command,
            "Command should be of logout type!");
    }

    @Test
    void testCommandFactory_invalidCommand() {
        Command command = CommandFactory.of("invalid", this.mockUsers, this.loggedUsers, this.httpClient);
        assertNull(command);
    }
}
