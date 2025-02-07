package api.controllers;

import api.utility.FilesCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LoggerController {

    private static final String DIRECTORY = "errors";
    private static final String LOGS_FILE = "logs.log";
    private static final Path LOGS_DB = Path.of(DIRECTORY + File.separator + LOGS_FILE);

    public static void writeLogsErrors(String error) {
        FilesCreator.checkPath(LOGS_DB, "");

        try (BufferedWriter writer = Files.newBufferedWriter(LOGS_DB, StandardOpenOption.APPEND)) {
            writer.write(error);
            writer.newLine();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }
}
