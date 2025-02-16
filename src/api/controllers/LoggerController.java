package api.controllers;

import api.utility.FilesCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;

public class LoggerController {

    private static final String DIRECTORY = "errors";
    private static final String LOGS_FILE = "logs.log";
    private static final Path LOGS_DB = Path.of(DIRECTORY + File.separator + LOGS_FILE);

    public static void writeLogsErrors(String error, StackTraceElement[] stackTrace) {
        FilesCreator.checkPath(LOGS_DB, "");

        String logMessage = LocalDateTime.now().toString() + " - " + error + " - " + Arrays.toString(stackTrace);

        try (BufferedWriter writer = Files.newBufferedWriter(LOGS_DB, StandardOpenOption.APPEND)) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }
}
