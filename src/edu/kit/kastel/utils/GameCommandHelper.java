package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CommandType;
import edu.kit.kastel.enums.Direction;
import edu.kit.kastel.game.CellPosition;
import edu.kit.kastel.game.Figure;
import edu.kit.kastel.game.Player;
import edu.kit.kastel.game.Session;

import static edu.kit.kastel.game.Session.NON_EXISTENT_DICE_NUMBER_VALUE;
import static edu.kit.kastel.utils.Utility.INVALID_INTEGER_VALUE;

/**
 * A helper class for game commands.
 *
 * @author Programmieren-Team
 */
public final class GameCommandHelper {
    private static final String UP_DIRECTION_STRING = "up";
    private static final String DOWN_DIRECTION_STRING = "down";
    private static final String LEFT_DIRECTION_STRING = "left";
    private static final String RIGHT_DIRECTION_STRING = "right";
    private static final String INVALID_PATH_ERROR_MESSAGE = "an invalid path has been passed!";
    private static final String INVALID_DICE_ROLL_PARAMETER_ERROR_MESSAGE = "an invalid parameter for the command "
            + "dice roll. Please parse an valid digit between 1 and 6.";
    private static final String ALREADY_ROLLED_PARAMETER_ERROR_MESSAGE = "this player already rolled the dice.";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final int FIGURE_LETTER_INDEX = 0;
    private static final int FIGURE_NUMBER_INDEX = 1;
    private static final int MIN_ROLL_DICE_VALUE = 1;
    private static final int MAX_ROLL_DICE_VALUE = 6;

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private GameCommandHelper() {
    }

    /**
     * This method handles active session commands.
     * It calls the appropriate method based on the command type.
     * If the active session is not valid, the method returns.
     *
     * @param type          the command type
     * @param activeSession the active session
     * @param command       the command
     */
    public static void handleActiveSessionCommand(CommandType type, Session activeSession, String command) {
        if (!isValidActiveSession(activeSession)) {
            return;
        }

        switch (type) {
            case SHOW:
                handleShowCommand(activeSession);
                break;
            case CURRENT_PLAYER:
                handleCurrentPlayerCommand(activeSession);
                break;
            case ROLL_DICE:
                handleRollDiceCommand(activeSession, command);
                break;
            case NEW_FIGURE:
                handleNewFigureCommand(activeSession);
                break;
            case MOVE:
                String[] params = MoveCommandsHelper.getParamsFromMoveCommand(command);
                MoveCommandsHelper.handleMoveCommand(activeSession, params);
                break;
            case MOVE_OBSTACLE:
                String[] obstacleParams = MoveCommandsHelper.getParamsFromMoveObstacleCommand(command);
                MoveCommandsHelper.handleMoveObstacleCommand(activeSession, obstacleParams);
                break;
            case SKIP_TURN:
                GameCommandHelper.handleSkipTurnCommand(activeSession);
                break;
            case REMATCH:
                GameCommandHelper.handleRematchCommand(activeSession);
                break;
            default:
                break;
        }
    }

    /**
     * This method returns direction from string.
     *
     * @param dir the direction as string
     * @return the direction
     */
    public static Direction getDirectionFromString(String dir) {
        return switch (dir) {
            case UP_DIRECTION_STRING -> Direction.UP;
            case DOWN_DIRECTION_STRING -> Direction.DOWN;
            case LEFT_DIRECTION_STRING -> Direction.LEFT;
            case RIGHT_DIRECTION_STRING -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * This method handles the show command.
     * It prints the game matrix.
     *
     * @param activeSession the active session
     */
    private static void handleShowCommand(Session activeSession) {
        InputOutputHandler.printMatrix(activeSession.getMatrix(false),
                activeSession.getCurrentPlayer().getName());
    }

    /**
     * This method handles the rematch command.
     * It starts a new game with the same players.
     *
     * @param activeSession the active session
     */
    private static void handleRematchCommand(Session activeSession) {
        activeSession.rematchSession();
    }

    /**
     * This method handles the skip turn command.
     * It changes the current player.
     *
     * @param activeSession the active session
     */
    private static void handleSkipTurnCommand(Session activeSession) {
        activeSession.changeCurrentPlayer();
    }

    /**
     * This method handles the current player command.
     * It prints the current player.
     *
     * @param activeSession the active session
     */
    private static void handleCurrentPlayerCommand(Session activeSession) {
        InputOutputHandler.printPlayer(activeSession.getCurrentPlayer());
    }

    /**
     * This method checks if the figure name is valid.
     *
     * @param figureName the figure name
     * @return true if the figure name is valid
     */
    public static boolean checkFigure(String figureName) {
        char figureLetter = figureName.charAt(FIGURE_LETTER_INDEX);
        char figureNumber = figureName.charAt(FIGURE_NUMBER_INDEX);

        return Character.isLetter(figureLetter)
                && Utility.parseIntegerParam(figureNumber + EMPTY_STRING_SYMBOL) != INVALID_INTEGER_VALUE;
    }

    /**
     * This method handles the roll dice command.
     * It checks if the dice is already rolled and if the dice is valid.
     * If the dice is not valid, it prints an error message.
     * If the dice is valid, it sets the dice value to the current player.
     *
     * @param activeSession the active session
     * @param command       the command
     */
    private static void handleRollDiceCommand(Session activeSession, String command) {
        if (!isValidActiveSession(activeSession)) {
            return;
        }

        if (activeSession.getCurrentPlayer().getDice() != NON_EXISTENT_DICE_NUMBER_VALUE) {
            InputOutputHandler.showError(ALREADY_ROLLED_PARAMETER_ERROR_MESSAGE);
            return;
        }

        boolean hasError = false;
        String dice = CommandHelper.getSingleParameter(command, CommandType.ROLL_DICE);
        if (dice != null && activeSession.hasSeed() || dice == null && !activeSession.hasSeed()) {
            hasError = true;
        }

        int intDice = -1;
        if (!hasError) {
            intDice = getIntDice(activeSession, dice);

            if (intDice < MIN_ROLL_DICE_VALUE || intDice > MAX_ROLL_DICE_VALUE) {
                hasError = true;
            }
        }

        if (!hasError) {
            activeSession.getCurrentPlayer().setDice(intDice);
            InputOutputHandler.printDice(intDice);
        } else {
            InputOutputHandler.showError(INVALID_DICE_ROLL_PARAMETER_ERROR_MESSAGE);
        }
    }

    private static int getIntDice(Session activeSession, String dice) {
        int intDice;
        intDice = !activeSession.hasSeed()
                ? Utility.parseIntegerParam(dice)
                : Utility.getRandomRollDiceNumber(activeSession.getRandom());
        return intDice;
    }

    /**
     * This method handles the new figure command.
     * It adds a new figure to the game.
     * It places the figure on the matrix and prints the figure.
     *
     * @param activeSession the active session
     */
    private static void handleNewFigureCommand(Session activeSession) {
        Player currentPlayer = activeSession.getCurrentPlayer();
        Figure figure = currentPlayer.addNewFigureToGame();

        Player player = activeSession.getCurrentPlayer();
        CellPosition startPosition = player.getStartPos();
        CellPosition oldPosition = new CellPosition(INVALID_INTEGER_VALUE, INVALID_INTEGER_VALUE);

        activeSession.placeFigureOnMatrix(oldPosition, startPosition, figure);
        System.out.println(figure);
    }

    /**
     * This method checks if the active session is valid.
     *
     * @param activeSession the active session
     * @return true if the active session is valid
     */
    private static boolean isValidActiveSession(Session activeSession) {
        return activeSession != null;
    }

    /**
     * This method checks if the command data is valid.
     * It prints an error message if the command data is invalid.
     *
     * @param activeSession the active session
     * @param params        the parameters
     * @return true if the command data is valid
     */
    public static boolean isValidCommandData(Session activeSession, String[] params) {
        if (isValidActiveSession(activeSession) && params == null) {
            InputOutputHandler.showError(INVALID_PATH_ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
