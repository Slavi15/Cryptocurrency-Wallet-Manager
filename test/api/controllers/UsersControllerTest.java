package api.controllers;

import api.models.users.Users;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UsersControllerTest {

    private static final String DIRECTORY = "datasets";
    private static final String DB_FILE = "users.json";
    private static final Path USERS_DB = Path.of(DIRECTORY + File.separator + DB_FILE);

    @Mock
    private Users mockUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadUsers() throws IOException {
        Users users = UsersController.readUsers();
        assertNotNull(users, "Users should not be null!");
    }

    @Test
    void testWriteUsers_validWriting() throws IOException {
        UsersController.writeUsers(this.mockUsers);
        assertTrue(Files.exists(USERS_DB), "File should exist!");
    }
}
