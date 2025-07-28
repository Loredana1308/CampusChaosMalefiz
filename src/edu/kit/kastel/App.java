package edu.kit.kastel;

import edu.kit.kastel.game.GameExecutor;
import edu.kit.kastel.utils.InputOutputHandler;

/**
 * This class is the entry point of the game.
 *
 * @author Programmieren-Team
 */
public final class App {
    private static final String ARGS_ERROR_MESSAGE = "no command lines required.";

    private App() {
    }

    /**
     * Start of the app.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            InputOutputHandler.showError(ARGS_ERROR_MESSAGE);
            return;
        }

        GameExecutor gameExecutor = new GameExecutor();
        gameExecutor.run();
    }
}
