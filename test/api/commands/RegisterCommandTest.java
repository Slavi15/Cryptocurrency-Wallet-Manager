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

public class RegisterCommandTest {

    private static final String REGISTER_COMMAND_INVALID_COMMAND_USAGE = "Usage: register <username> <email> <password>";
    private static final String REGISTER_COMMAND_USER_LOGGED_IN = "User %s is already logged in!";
    private static final String REGISTER_COMMAND_USER_ALREADY_REGISTERED = "User $%s already exists in DB!";
    private static final String REGISTER_COMMAND_INVALID_EMAIL = "Invalid email address provided!";
    private static final String REGISTER_COMMAND_INVALID_USERNAME = "Invalid username provided!";
    private static final String REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB = "User %s has been successfully registered!";

    @InjectMocks
    private RegisterCommand registerCommand;

    @Mock
    private Users mockUsers;

    @Mock
    private User mockUser;

    @Mock
    private SelectionKey mockKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.registerCommand = new RegisterCommand(this.mockUsers);
    }

    @Test
    void testExecute_invalidCommandUsage() {
        String[] input = { "username", "email" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_INVALID_COMMAND_USAGE, result);
    }

    @Test
    void testRegister_userAlreadyLoggedIn() {
        when(mockKey.attachment()).thenReturn(this.mockUser);

        String[] input = { "Username", "email", "password" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_USER_LOGGED_IN.formatted("Username"), result);
    }

    @Test
    void testRegister_userAlreadyRegistered() {
        when(this.mockUsers.findUser("email")).thenReturn(this.mockUser);

        String[] input = { "username", "email", "password" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_USER_ALREADY_REGISTERED.formatted("username"), result);
    }

    @Test
    void testRegister_invalidEmailFormat() {
        String[] input = { "username", "email", "password" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_INVALID_EMAIL, result);
    }

    @Test
    void testRegister_invalidUsernameFormat() {
        String[] input = { "username", "test@test.com", "password" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_INVALID_USERNAME, result);
    }

    @Test
    void testRegister_successfulRegister() {
        when(this.mockUsers.findUser("test@test.com")).thenReturn(null);

        String[] input = { "Username", "test@test.com", "password" };
        String result = this.registerCommand.execute(input, mockKey);

        assertEquals(REGISTER_COMMAND_SUCCESSFUL_REGISTER_DB.formatted("Username"), result);
    }
}
