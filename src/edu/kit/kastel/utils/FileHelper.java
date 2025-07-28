package edu.kit.kastel.utils;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A helper class wrapping readAllLines method for convenient usage.
 *
 * @author Programmieren-Team
 */
public final class FileHelper {
    private static final String INVALID_PATH_ERROR_MESSAGE = "an invalid path has been passed!";

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private FileHelper() {
    }

    /**
     * Returns all lines of a file specified by the given path.
     *
     * @param path the path to the file to read
     * @return all lines of the specified file
     */
    public static List<String> readAllLines(String path) {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            InputOutputHandler.showError(INVALID_PATH_ERROR_MESSAGE);
        }

        return null;
    }
}

