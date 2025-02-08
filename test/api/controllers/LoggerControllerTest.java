package api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerControllerTest {

    private static final String DIRECTORY = "errors";
    private static final String LOGS_FILE = "logs.log";
    private static final Path LOGS_DB = Path.of(DIRECTORY + File.separator + LOGS_FILE);

    @Test
    void testWriteLogsError() throws IOException {
        Files.deleteIfExists(LOGS_DB);

        LoggerController.writeLogsErrors("Test error!");

        assertTrue(Files.exists(LOGS_DB), "File should exist!");

        String expected = "Test error!" + System.lineSeparator();
        assertEquals(expected, Files.readString(LOGS_DB),
            "Log error messages should be the same!");
    }
}
