package api.controllers;

import api.models.users.Users;
import api.utility.FilesCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UsersController {

    private static final String DIRECTORY = "datasets";
    private static final String DB_FILE = "users.json";
    private static final Path USERS_DB = Path.of(DIRECTORY + File.separator + DB_FILE);

    private static final String INITIAL_OBJECT = "{\"users\": []}";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String LOGIN_FILE_ERROR_MESSAGE =
        "Unable to connect to the server. " +
            "Try again later or contact administrator by providing the logs in errors/logs.log";

    public static Users readUsers() throws IOException {
        FilesCreator.checkPath(USERS_DB, INITIAL_OBJECT);

        try (BufferedReader reader = Files.newBufferedReader(USERS_DB)) {
            return GSON.fromJson(reader, Users.class);
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            throw new IOException(LOGIN_FILE_ERROR_MESSAGE);
        }
    }

    public static void writeUsers(Users users) throws IOException {
        if (users == null) {
            LoggerController.writeLogsErrors("Error when trying to write null users to DB!");
        }

        FilesCreator.checkPath(USERS_DB, INITIAL_OBJECT);
        String jsonUsers = GSON.toJson(users);

        try (BufferedWriter writer = Files.newBufferedWriter(USERS_DB)) {
            writer.write(jsonUsers);
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            throw new IOException(LOGIN_FILE_ERROR_MESSAGE);
        }
    }
}
