package api.commands;

import api.models.users.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.nio.channels.SelectionKey;
import java.util.LinkedHashSet;
import java.util.Set;

public class LogoutCommandTest {

    private static final String LOGOUT_COMMAND_INVALID_COMMAND_USAGE = "Usage: logout";
    private static final String LOGOUT_COMMAND_NOT_LOGGED_IN = "You must login before logging out!";
    private static final String LOGOUT_COMMAND_SUCCESSFUL_OPERATION = "You have successfully logged out!";

    @InjectMocks
    private LogoutCommand logoutCommand;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    private Set<User> loggedUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.loggedUsers = new LinkedHashSet<>();
        this.logoutCommand = new LogoutCommand(loggedUsers);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "param" };
        String result = this.logoutCommand.execute(input, mockKey);

        assertEquals(LOGOUT_COMMAND_INVALID_COMMAND_USAGE, result);
    }

    @Test
    void testLogout_userNotFound() {
        when(mockKey.attachment()).thenReturn(null);

        String[] input = {};
        String result = this.logoutCommand.execute(input, mockKey);

        assertEquals(LOGOUT_COMMAND_NOT_LOGGED_IN, result);
    }

    @Test
    void testLogout_successfulLogout() {
        loggedUsers.add(this.mockUser);
        when(mockKey.attachment()).thenReturn(this.mockUser);

        String[] input = {};
        String result = this.logoutCommand.execute(input, mockKey);

        assertEquals(LOGOUT_COMMAND_SUCCESSFUL_OPERATION, result);
        assertFalse(loggedUsers.contains(this.mockUser));

        verify(mockKey).attach(null);
    }
}
