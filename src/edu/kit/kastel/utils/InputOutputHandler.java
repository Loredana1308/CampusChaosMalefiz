package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CellType;
import edu.kit.kastel.enums.CommandType;
import edu.kit.kastel.game.Figure;
import edu.kit.kastel.game.GameCell;
import edu.kit.kastel.game.Player;
import edu.kit.kastel.game.Session;

import java.util.List;
import java.util.Scanner;

/**
 * This class handles all inputs and outputs.
 *
 * @author Programmieren-Team
 */
public final class InputOutputHandler {
    private static final String WRONG_COMMAND_ERROR_MESSAGE = "wrong command or parameters.";
    private static final String ERROR_PREFIX = "Error, ";
    private static final String GREETING_MESSAGE = "Welcome to CampusChaos 2024. Enter 'help' for more details.";
    private static final String SENTENCE_WORDS_SEPARATOR_SYMBOL = " ";
    private static final String SENTENCE_SEPARATOR_SYMBOL = ".";
    private static final String PLAYER_INFO_STRING = "Player";
    private static final String PLAYER_TURN_PREFIX_MESSAGE = "It's player ";
    private static final String PLAYER_TURN_SUFFIX_MESSAGE = "'s turn.";
    private static final String HAS_HIT_MESSAGE = " has hit ";
    private static final String HAS_WON_MESSAGE = " has won!";

    /**
     * Scanner to read input.
     */
    private final Scanner scanner;

    /**
     * Constructs a new InputHandler.
     */
    public InputOutputHandler() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Function that shows a greeting.
     */
    public static void showGreeting() {
        System.out.println(GREETING_MESSAGE);
    }

    /**
     * This method handles the help command.
     * It prints all available commands.
     *
     * @param sessions      current list of sessions
     * @param activeSession the active session
     */
    public static void handleHelpCommand(List<Session> sessions, Session activeSession) {
        for (CommandType key : HelpCommandHelper.SORTED_COMMAND_DESCRIPTIONS.keySet()) {
            if (CommandHelper.isAvailableCommand(key, activeSession, sessions)) {
                System.out.println(HelpCommandHelper.SORTED_COMMAND_DESCRIPTIONS.get(key));
            }
        }
    }

    /**
     * This method prints the matrix.
     * It prints the matrix with the current player's figures.
     * It also prints the player's starting positions.
     * @param gameMatrix game matrix
     * @param currentPlayerName current player name
     */
    public static void printMatrix(GameCell[][] gameMatrix, char currentPlayerName) {
        for (GameCell[] matrix : gameMatrix) {
            for (GameCell cell : matrix) {
                handleShowCell(currentPlayerName, cell);
            }
            System.out.println();
        }
    }

    private static void handleShowCell(char currentPlayerName, GameCell cell) {
        if (cell.hasFigure() && !cell.isForest()) {
            Figure figure = cell.getFigure();
            printMatrixCell(figure, currentPlayerName);
        } else if (cell.getType() == CellType.PLAYER_STARTING_POSITION) {
            System.out.print(cell.getPlayerSymbol());
        } else if (cell.getType() != CellType.NON_EXISTING_CELL) {
            System.out.print(cell.getType().getSymbol());
        }
    }

    /**
     * Function that prints session id.
     *
     * @param sessionId session id to show
     */
    public static void printSessionId(String sessionId) {
        System.out.println(sessionId);
    }

    /**
     * Function that prints player turn.
     *
     * @param name player name to print.
     */
    public static void printPlayerTurn(char name) {
        System.out.println(PLAYER_TURN_PREFIX_MESSAGE + name + PLAYER_TURN_SUFFIX_MESSAGE);
    }

    /**
     * Function that prints player hit.
     * It shows the player who hit and the player who was hit.
     *
     * @param firstPlayerName  winner player name
     * @param secondPlayerName hit player name
     */
    public static void printPlayerHit(char firstPlayerName, char secondPlayerName) {
        String firstPlayerInfo = PLAYER_INFO_STRING + SENTENCE_WORDS_SEPARATOR_SYMBOL + firstPlayerName;
        String secondPlayerInfo = PLAYER_INFO_STRING + SENTENCE_WORDS_SEPARATOR_SYMBOL + secondPlayerName;

        System.out.println(firstPlayerInfo + HAS_HIT_MESSAGE + secondPlayerInfo + SENTENCE_SEPARATOR_SYMBOL);
    }

    /**
     * Function that prints the winner.
     *
     * @param name winner name
     */
    public static void printWinner(char name) {
        System.out.println(PLAYER_INFO_STRING + SENTENCE_WORDS_SEPARATOR_SYMBOL + name + HAS_WON_MESSAGE);
    }

    /**
     * Function that shows an error.
     *
     * @param error error message to show
     */
    public static void showError(String error) {
        System.err.println(ERROR_PREFIX + error);
    }

    /**
     * Function that shows an error for start command.
     *
     * @param error error message to show
     * @param matrix game matrix
     * @param currentPlayer current player of the active game
     */
    public static void showStartCommandError(String error, GameCell[][] matrix, char currentPlayer) {
        printMatrix(matrix, currentPlayer);
        System.err.println(ERROR_PREFIX + error);
    }

    /**
     * Function that shows a error message if commandType is null.
     *
     * @param commandType the command type
     * @return true if commandType is null
     */
    public static boolean showErrorIfNeeded(CommandType commandType) {
        if (commandType == null) {
            InputOutputHandler.showError(WRONG_COMMAND_ERROR_MESSAGE);
            return true;
        }

        return false;
    }

    /**
     * Function that prints the matrix cell.
     *
     * @param figure the figure
     * @param name   the name
     */
    private static void printMatrixCell(Figure figure, char name) {
        System.out.print(figure.getLetter() == name ? figure.getIndex() : String.valueOf(figure.getLetter()));
    }

    /**
     * Function that prints the session.
     *
     * @param session the figure
     */
    public static void printSession(Session session) {
        System.out.println(session);
    }

    /**
     * Function that prints the player.
     *
     * @param currentPlayer the player
     */
    public static void printPlayer(Player currentPlayer) {
        System.out.println(currentPlayer);
    }

    /**
     * Function that prints the dice.
     *
     * @param intDice the dice
     */
    public static void printDice(int intDice) {
        System.out.println(intDice);
    }

    /**
     * Function that closes the scanner.
     * This method is called when the quit command is entered.
     */
    public void closeScanner() {
        this.scanner.close();
    }

    /**
     * Function that reads a command.
     *
     * @return the command
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }
}
