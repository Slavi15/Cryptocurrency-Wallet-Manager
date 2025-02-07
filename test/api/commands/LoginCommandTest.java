package api.commands;

import api.models.users.User;
import api.models.users.Users;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

public class LoginCommandTest {

    private static final String LOGIN_INVALID_COMMAND_USAGE = "Usage: login <email> <password>";
    private static final String LOGIN_COMMAND_USER_NOT_FOUND_DB = "User with email %s does not exist in DB!";
    private static final String LOGIN_COMMAND_INCORRECT_PASSWORD = "Incorrect password!";
    private static final String LOGIN_COMMAND_SUCCESSFUL_LOGIN_DB = "User with email %s has been successfully logged into the system!";

    @InjectMocks
    private LoginCommand loginCommand;

    @Mock
    private Users mockUsers;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    private Set<User> loggedUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.loggedUsers = new LinkedHashSet<>();
        this.loginCommand = new LoginCommand(this.mockUsers, loggedUsers);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "emailOnly" };
        String result = loginCommand.execute(input, mockKey);

        assertEquals(LOGIN_INVALID_COMMAND_USAGE, result);
    }

    @Test
    void testLogin_userNotFoundDB() {
        when(this.mockUsers.findUser("test@test.com")).thenReturn(null);

        String[] input = { "test@test.com", "test123" };
        String result = loginCommand.execute(input, mockKey);

        assertEquals(LOGIN_COMMAND_USER_NOT_FOUND_DB.formatted("test@test.com"), result);
    }

    @Test
    void testLogin_incorrectPassword() {
        when(this.mockUsers.findUser("test@test.com")).thenReturn(this.mockUser);
        when(this.mockUser.isPasswordCorrect("wrong")).thenReturn(false);

        String[] input = { "test@test.com", "wrong" };
        String result = loginCommand.execute(input, mockKey);

        assertEquals(LOGIN_COMMAND_INCORRECT_PASSWORD, result);
    }

    @Test
    void testLogin_successfulLogin() {
        when(this.mockUsers.findUser("test@test.com")).thenReturn(this.mockUser);
        when(this.mockUser.isPasswordCorrect("test123")).thenReturn(true);

        String[] input = { "test@test.com", "test123" };
        String result = loginCommand.execute(input, mockKey);

        assertEquals(LOGIN_COMMAND_SUCCESSFUL_LOGIN_DB.formatted("test@test.com"), result);
        assertTrue(loggedUsers.contains(this.mockUser));

        verify(mockKey).attach(this.mockUser);
    }
}
