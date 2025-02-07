package api.commands;

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

public class DepositCommandTest {

    private static final String DEPOSIT_COMMAND_INVALID_USAGE = "Usage: deposit-money <amount>";
    private static final String DEPOSIT_COMMAND_NOT_LOGGED_IN = "You must login before making a deposit!";
    private static final String DEPOSIT_COMMAND_INVALID_AMOUNT = "Invalid deposit amount provided!";
    private static final String DEPOSIT_COMMAND_SUCCESSFUL_OPERATION = "User %s successfully added %f to their wallet!";

    @InjectMocks
    private DepositCommand depositCommand;

    @Mock
    private Users mockUsers;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.depositCommand = new DepositCommand(this.mockUsers);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { };
        String result = this.depositCommand.execute(input, mockKey);

        assertEquals(DEPOSIT_COMMAND_INVALID_USAGE, result);
    }

    @Test
    void testDeposit_userNotLogged() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = { "100" };
        String result = this.depositCommand.execute(input, mockKey);

        assertEquals(DEPOSIT_COMMAND_NOT_LOGGED_IN, result);
    }

    @Test
    void testDeposit_negativeDepositAmount() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        String[] input = { "-100" };
        String result = this.depositCommand.execute(input, mockKey);

        assertEquals(DEPOSIT_COMMAND_INVALID_AMOUNT, result);
    }

    @Test
    void testDeposit_invalidDepositAmount() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        String[] input = { "abc" };
        String result = this.depositCommand.execute(input, mockKey);

        assertEquals(DEPOSIT_COMMAND_INVALID_AMOUNT, result);
    }

    @Test
    void testDeposit_successfulOperation() {
        when(mockKey.attachment()).thenReturn(this.mockUser);
        when(this.mockUser.getUserName()).thenReturn("Test");

        String[] input = { "100" };
        String result = this.depositCommand.execute(input, mockKey);

        assertEquals(DEPOSIT_COMMAND_SUCCESSFUL_OPERATION.formatted("Test", 100.0), result);
    }
}
