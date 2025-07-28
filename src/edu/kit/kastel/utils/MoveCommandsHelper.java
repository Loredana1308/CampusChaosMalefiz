package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CellType;
import edu.kit.kastel.enums.CommandType;
import edu.kit.kastel.enums.Direction;
import edu.kit.kastel.game.CellPosition;
import edu.kit.kastel.game.Figure;
import edu.kit.kastel.game.GameCell;
import edu.kit.kastel.game.Session;

import java.util.Arrays;
import java.util.List;

import static edu.kit.kastel.utils.Utility.INVALID_INTEGER_VALUE;

/**
 * This class stores all methods used in move and move obstacle commands.
 *
 * @author Programmieren-Team
 */
public final class MoveCommandsHelper {
    private static final String INVALID_NUMBER_OF_MOVES_ERROR_MESSAGE = "invalid number of moves.";
    private static final String INVALID_PATH_ERROR_MESSAGE = "an invalid path has been passed!";
    private static final String INVALID_FIGURE_ERROR_MESSAGE = "invalid figure name or this figure is not on the "
            + "playing field.";
    private static final String INVALID_DIRECTION_ERROR_MESSAGE = "invalid direction. "
            + "Please use: up, right, left or down.";
    private static final String COMMAND_SEPARATOR_SYMBOL = " ";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final int START_ITERATION_INDEX = 0;
    private static final int INTEGER_DEFAULT_INITIAL_VALUE = 0;
    private static final int SINGLE_MOVE_DISTANCE = 1;
    private static final int MIN_NR_OF_PARAMS_FOR_MOVE_COMMAND = 3;
    private static final int MIN_NR_OF_PARAMS_FOR_MOVE_OBST_COMMAND = 2;
    private static final int MAX_NR_OF_PARAMS_FOR_MOVE_OBST_COMMAND = 4;
    private static final int MAX_NR_OF_VARIABLES_FOR_MOVE_OBS_COMMAND = 4;
    private static final int FIRST_DIR_IDX = 1;
    private static final int SECOND_DIR_INDEX = 3;
    private static final int FIGURE_LETTER_INDEX = 0;

    private MoveCommandsHelper() {
    }

    /**
     * This method handles the move command.
     * It checks if the figure is on the playing field and if the figure name is valid.
     * If the figure is not on the playing field, it prints an error message.
     *
     * @param activeSession the active session
     * @param params        the parameters
     */
    public static void handleMoveCommand(Session activeSession, String[] params) {
        if (!GameCommandHelper.isValidCommandData(activeSession, params)) {
            return;
        }

        String figureName = params[FIGURE_LETTER_INDEX];
        Figure figure = activeSession.getCurrentPlayer().checkIfFigureOnPlayingField(figureName);
        if (!GameCommandHelper.checkFigure(figureName) || figure == null) {
            InputOutputHandler.showError(INVALID_FIGURE_ERROR_MESSAGE);
            return;
        }

        String[] directions = Arrays.copyOfRange(params, 1, params.length);
        activeSession.handleMove(figure, directions);

    }

    /**
     * Method that processes move command steps. Iterate throw user parameter and checks if new
     * position is valid.
     * @param directions directions given by the user in command
     * @param dice current dice value
     * @param initialPos initial position for figure
     * @param visited list of visited positions
     * @param gameMatrix game matrix
     * @return new position after moves
     */
    public static CellPosition processMoveSteps(String[] directions, int dice, CellPosition initialPos,
                                         List<CellPosition> visited, GameCell[][] gameMatrix) {
        int number = INTEGER_DEFAULT_INITIAL_VALUE;
        int directionSum = INTEGER_DEFAULT_INITIAL_VALUE;
        CellPosition newPosition = new CellPosition(initialPos);

        for (int i = START_ITERATION_INDEX; i < directions.length; i++) {
            if (i % 2 == 0) {
                number = processNumber(directions[i], directionSum, false, dice);

                if (number == INVALID_INTEGER_VALUE) {
                    return null;
                }

                directionSum += number;
            } else {
                newPosition = processDirection(directions[i], newPosition, number,
                        i == directions.length - 1, visited, gameMatrix);

                if (newPosition == null) {
                    return null;
                }
            }
        }

        if (directionSum != dice) {
            InputOutputHandler.showError(INVALID_NUMBER_OF_MOVES_ERROR_MESSAGE);
            return null;
        }

        return newPosition;
    }

    /**
     * Method that processes move obstacle steps.
     * @param directions directions given by the user
     * @param activeSession active session
     * @return true if successfully processed
     */
    public static boolean processMoveObstacleSteps(String[] directions, Session activeSession) {
        int number = INVALID_INTEGER_VALUE;
        Direction direction;
        for (int i = START_ITERATION_INDEX; i < directions.length; i++) {
            if (i % 2 == 0) {
                number = processNumber(directions[i], INVALID_INTEGER_VALUE, true, INVALID_INTEGER_VALUE);

                if (number == INVALID_INTEGER_VALUE) {
                    return false;
                }
            } else {

                direction = GameCommandHelper.getDirectionFromString(directions[i]);

                if (isValidDirection(directions, direction)) {
                    InputOutputHandler.showError(INVALID_DIRECTION_ERROR_MESSAGE);
                    return false;
                }

                if (!MoveCommandsHelper.moveObstacle(number, direction, activeSession, i == directions.length - 1)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidDirection(String[] directions, Direction direction) {
        return direction == null || directions.length == MAX_NR_OF_VARIABLES_FOR_MOVE_OBS_COMMAND
                && directions[FIRST_DIR_IDX].equals(directions[SECOND_DIR_INDEX]);
    }

    /**
     * This method moves the figure in the given direction.
     * @param currentPos    the current pos
     * @param numberOfMoves the number of moves
     * @param direction     the direction
     * @param isLastMove    if it is the last move
     * @param visited       the visited cells
     * @param gameMatrix    matrix of the game
     * @return the new position
     */
    private static CellPosition move(CellPosition currentPos, int numberOfMoves, Direction direction,
                              boolean isLastMove, List<CellPosition> visited, GameCell[][] gameMatrix) {
        CellPosition newPos = currentPos;
        for (int j = START_ITERATION_INDEX; j < numberOfMoves; j++) {
            newPos = getNewPositionAfterOneMove(direction, newPos);

            if (!Utility.checkNewFigurePosition(visited, newPos, isLastMove && j == numberOfMoves - 1,
                    gameMatrix)) {
                return null;
            }

            visited.add(newPos);
        }

        return newPos;
    }

    /**
     * This method processes the direction.
     *
     * @param givenDirection the given direction
     * @param newPos         the new position
     * @param number         the number of moves
     * @param lastMove       if it is the last move
     * @param visited        the visited cells
     * @param gameMatrix     matrix of the game
     * @return the new position
     */
    public static CellPosition processDirection(String givenDirection, CellPosition newPos, int number,
                                         boolean lastMove, List<CellPosition> visited, GameCell[][] gameMatrix) {
        Direction direction = GameCommandHelper.getDirectionFromString(givenDirection);
        if (direction == null) {
            InputOutputHandler.showError(INVALID_DIRECTION_ERROR_MESSAGE);
            return null;
        }

        return move(newPos, number, direction, lastMove, visited, gameMatrix);
    }


    /**
     * This method handles the place obstacle command.
     * It places an obstacle on the matrix.
     *
     * @param numberOfMoves the number of moves
     * @param direction     the direction
     * @param activeSession the active session
     * @param isLastMove if it is the last move
     * @return true if move obstacle and false if not
     */
    public static boolean moveObstacle(int numberOfMoves, Direction direction, Session activeSession,
                                       boolean isLastMove) {
        GameCell obstacle = activeSession.getCurrentPlayer().getPendingObstacle();

        int matrixRowLength = activeSession.getMatrixRowLength();
        int matrixColLength = activeSession.getMatrixColumnLength();
        CellPosition newPos = getNewObstaclePos(numberOfMoves, direction, obstacle.getPos(), matrixRowLength, matrixColLength);

        if (newPos == null) {
            InputOutputHandler.showError(INVALID_PATH_ERROR_MESSAGE);
            return false;
        }

        // verify if obstacle new position is on pathway
        if (isLastMove) {
            CellType newCellType = activeSession.getCellType(newPos);
            boolean hasFigure = activeSession.hasMatrixCellFigure(newPos);
            if (newCellType != CellType.PATHWAY && newCellType != CellType.PATHWAY_VILLAGE || hasFigure) {
                InputOutputHandler.showError(INVALID_PATH_ERROR_MESSAGE);
                return false;
            }
        }

        obstacle.setPosition(newPos);
        return true;
    }

    /**
     * This method handles the move obstacle command.
     *
     * @param activeSession the active session
     * @param directions    the directions
     */
    public static void handleMoveObstacleCommand(Session activeSession, String[] directions) {
        if (directions == null || directions.length < 2 || directions.length > MAX_NR_OF_PARAMS_FOR_MOVE_OBST_COMMAND) {
            InputOutputHandler.showError(INVALID_PATH_ERROR_MESSAGE);
            return;
        }
        activeSession.handleMoveObstacle(directions);
    }

    /**
     * This method returns the parameters from a move command.
     * It returns the figure id, directions and distances.
     * If the command is not valid, it returns null.
     *
     * @param command the command
     * @return the parameters, figure id, directions and distances
     */
    public static String[] getParamsFromMoveCommand(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        String[] params = command.replace(CommandType.MOVE + COMMAND_SEPARATOR_SYMBOL,
                EMPTY_STRING_SYMBOL).trim().split(COMMAND_SEPARATOR_SYMBOL);
        return params.length >= MIN_NR_OF_PARAMS_FOR_MOVE_COMMAND && params.length % 2 != 0
                ? params
                : null;
    }


    /**
     * This method returns the parameters from a move obstacle command.
     * It returns the directions and distances.
     * If the command is not valid, it returns null.
     *
     * @param command the command
     * @return the parameters, directions and distances
     */
    public static String[] getParamsFromMoveObstacleCommand(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        String[] params = command.replace(CommandType.MOVE_OBSTACLE + COMMAND_SEPARATOR_SYMBOL,
                EMPTY_STRING_SYMBOL).trim().split(COMMAND_SEPARATOR_SYMBOL);
        return params.length == MIN_NR_OF_PARAMS_FOR_MOVE_OBST_COMMAND
                || params.length == MAX_NR_OF_PARAMS_FOR_MOVE_OBST_COMMAND
                ? params
                : null;
    }

    /**
     * This method returns new position after one move.
     *
     * @param direction the direction
     * @param pos    the position
     * @return the new position
     */
    public static CellPosition getNewPositionAfterOneMove(Direction direction, CellPosition pos) {
        int newRow = pos.getRow();
        int newCol = pos.getColumn();
        switch (direction) {
            case UP:
                newRow -= SINGLE_MOVE_DISTANCE;
                break;
            case DOWN:
                newRow += SINGLE_MOVE_DISTANCE;
                break;
            case LEFT:
                newCol -= SINGLE_MOVE_DISTANCE;
                break;
            case RIGHT:
                newCol += SINGLE_MOVE_DISTANCE;
                break;
            default:
                break;
        }

        return new CellPosition(newRow, newCol);
    }

    /**
     * This method returns new position for an obstacle.
     * If the new position is out of bounds, the method returns null.
     * Otherwise, it returns the new position.
     *
     * @param numberOfMoves the list of sessions
     * @param direction the list of sessions
     * @param pos the position
     * @param matrixRowLength row length
     * @param matrixColLength column length
     * @return the active session
     */
    public static CellPosition getNewObstaclePos(int numberOfMoves, Direction direction, CellPosition pos,
                                                 int matrixRowLength, int matrixColLength) {
        int newRow = pos.getRow();
        int newCol = pos.getColumn();
        switch (direction) {
            case UP:
                newRow -= numberOfMoves;
                break;
            case DOWN:
                newRow += numberOfMoves;
                break;
            case LEFT:
                newCol -= numberOfMoves;
                break;
            case RIGHT:
                newCol += numberOfMoves;
                break;
            default:
                break;
        }

        CellPosition newPos = new CellPosition(newRow, newCol);
        if (Utility.isOutOfBounds(newPos, matrixRowLength, matrixColLength)) {
            return null;
        }

        return newPos;
    }


    /**
     * This method processes the number of moves given as a parameter for move commands.
     *
     * @param direction direction
     * @param directionSum sum of directions
     * @param isMoveObstacle is move obstacle
     * @param dice dice
     * @return number
     */
    public static int processNumber(String direction, int directionSum, boolean isMoveObstacle, int dice) {
        int number = Utility.parseIntegerParam(direction);
        if (number == INVALID_INTEGER_VALUE
                || (!isMoveObstacle && directionSum + number > dice)) {
            InputOutputHandler.showError(INVALID_NUMBER_OF_MOVES_ERROR_MESSAGE);
            return -1;
        }
        return number;
    }
}
