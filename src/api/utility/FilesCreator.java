package api.utility;

import api.controllers.LoggerController;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesCreator {

    public static void checkPath(Path path, String initialObject) {
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectory(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            if (Files.size(path) == 0) {
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(initialObject);
                }
            }
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage(), exc.getStackTrace());
        }
    }
}
