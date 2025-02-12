package api.controllers;

import api.models.users.Users;
import api.utility.FilesCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class UsersController {

    private static final String DIRECTORY = "datasets";
    private static final String DB_FILE = "users.json";
    private static final Path USERS_DB = Path.of(DIRECTORY + File.separator + DB_FILE);

    private static final String INITIAL_OBJECT = "{\"users\": []}";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String LOGIN_FILE_ERROR_MESSAGE =
        "Unable to connect to the server! " +
            "Try again later or contact administrator by providing the logs in errors/logs.log!";

    public static Users readUsers() throws IOException {
        FilesCreator.checkPath(USERS_DB, INITIAL_OBJECT);

        try (RandomAccessFile file = new RandomAccessFile(USERS_DB.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            String json = new String(bytes, StandardCharsets.UTF_8);
            return GSON.fromJson(json, Users.class);
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            throw new IOException(LOGIN_FILE_ERROR_MESSAGE);
        }
    }

    public static void writeUsers(Users users) throws IOException {
        if (users == null) {
            LoggerController.writeLogsErrors("Error when trying to write null users to DB!");
            return;
        }

        FilesCreator.checkPath(USERS_DB, INITIAL_OBJECT);
        String jsonUsers = GSON.toJson(users);

        try (RandomAccessFile file = new RandomAccessFile(USERS_DB.toFile(), "rw");
             FileChannel channel = file.getChannel()) {
            byte[] bytes = jsonUsers.getBytes(StandardCharsets.UTF_8);
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            buffer.put(bytes);
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
            throw new IOException(LOGIN_FILE_ERROR_MESSAGE);
        }
    }
}
