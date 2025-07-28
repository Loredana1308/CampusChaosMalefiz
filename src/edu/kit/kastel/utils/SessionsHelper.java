package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CellType;
import edu.kit.kastel.game.CellPosition;
import edu.kit.kastel.game.Forest;
import edu.kit.kastel.game.GameCell;
import edu.kit.kastel.game.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.kit.kastel.game.GameCell.EMPTY_CELL_SYMBOL;
import static edu.kit.kastel.game.GameCell.EMPTY_FOREST_SYMBOL;
import static edu.kit.kastel.game.GameExecutor.MIN_NR_OF_PARAMS_FOR_START_COMMAND;
import static edu.kit.kastel.utils.Utility.INVALID_INTEGER_VALUE;

/**
 * This class stores helper methods related to sessions.
 *
 * @author Programmieren-Team
 */
public final class SessionsHelper {
    /**
     * Reserved characters user for field cells.
     */
    public static final char[] RESERVED_CHARACTERS =
            new char[]{CellType.OBSTACLE.getSymbol(),
                    CellType.PROTECTED_ZONE.getSymbol(),
                    CellType.PATHWAY.getSymbol(),
                    CellType.TARGET.getSymbol(),
                    CellType.OCCUPIED_FOREST.getSymbol(),
            };
    private static final int MIN_NR_OF_PLAYERS = 2;
    private static final String TO_MUCH_PLAYERS_ERROR_MESSAGE = "wrong command or parameters.";
    private static final String WRONG_START_SESSION_PARAMS_ERROR_MESSAGE = "wrong command parameters.";
    private static final String ALREADY_EXISTING_SESSION_ERROR_MESSAGE = "this session already exists.";
    private static final String NOT_ALPHANUMERICAL_SESSION_ERROR_MESSAGE = "this session is not alphanumerical.";
    private static final String NOT_IN_RANGE_ERROR_MESSAGE = "the entered numbers are not integers or number of "
            + "players do not fall within the range 2-21.";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final int START_ITERATION_INDEX = 0;
    private static final int MAX_NR_OF_PLAYERS = 21;
    private static final int SESSION_ID_PARAM_INDEX = 0;
    private static final int FILE_PATH_PARAM_INDEX = 1;
    private static final int NR_OF_PLAYERS_PARAM_INDEX = 2;
    private static final int SEED_PARAM_INDEX = 3;

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private SessionsHelper() {
    }

    /**
     * This method finds a session by id.
     * If the list is empty or the id is null, the method returns null.
     *
     * @param list the list of sessions
     * @param id   the session id
     * @return the session
     */
    public static Session findSession(List<Session> list, String id) {
        if (Utility.isListEmpty(list) || id == null) {
            return null;
        }

        for (Session s : list) {
            if (s.getId().equals(id)) {
                return s;
            }
        }

        return null;
    }

    /**
     * This method checks if nrOfPlayers provided for a session is valid.
     * If the number of players is not in the range [2, 21] or the seed is not provided, the method returns false.
     *
     * @param params      parameters provided with start session command
     * @param nrOfPlayers number of players
     * @param seed        seed of session
     * @return true if number of players is valid
     */
    private static boolean checkIfValidNrOfPlayersAndSeed(String[] params, int nrOfPlayers, int seed) {
        boolean isInvalidSeed = params.length > MIN_NR_OF_PARAMS_FOR_START_COMMAND && seed == INVALID_INTEGER_VALUE;
        boolean isInvalidNrOfPlayer = nrOfPlayers < MIN_NR_OF_PLAYERS
                || nrOfPlayers > MAX_NR_OF_PLAYERS;

        return !isInvalidSeed && !isInvalidNrOfPlayer;
    }

    /**
     * This method removes a session from the list.
     * If the list is empty or the session is null, the method does nothing.
     * If the session is removed, the method prints the session id.
     *
     * @param list the list of sessions
     * @param s    the session
     */
    public static void removeSession(List<Session> list, Session s) {
        if (Utility.isListEmpty(list)) {
            return;
        }

        if (list.remove(s)) {
            InputOutputHandler.printSessionId(s.getId());
        }
    }

    /**
     * This method reads the game matrix from a file.
     * If the file is not found, the method returns null.
     * Otherwise, it converts the list of lines to a matrix.
     *
     * @param filePath the file path
     * @return the game matrix
     */
    private static GameCell[][] getSessionGameMatrix(String filePath) {
        List<String> matrixLinesList =
                FileHelper.readAllLines(filePath);
        return matrixLinesList == null ? null : Utility.convertFromListToMatrix(matrixLinesList);
    }

    /**
     * This method gets the starting position of the players.
     * It returns a map with the player symbol as key and the starting position as value.
     * If the matrix is null, the method returns an empty map.
     * If the matrix is not null, it iterates through the matrix and adds the starting positions to the map.
     *
     * @param matrix      the game matrix
     * @param nrOfPlayers the number of players
     * @return the starting positions
     */
    private static Map<Character, CellPosition> getForestAndStartingPosition(GameCell[][] matrix, int nrOfPlayers) {
        ArrayList<Character> letters = new ArrayList<>();
        Map<Character, CellPosition> positions = new HashMap<>();

        setLettersToFind(nrOfPlayers, letters);

        for (int i = START_ITERATION_INDEX; i < matrix.length; i++) {
            for (int j = START_ITERATION_INDEX; j < matrix[i].length; j++) {
                GameCell cell = matrix[i][j];
                boolean foundPlayerStartingPos = isFoundPlayerStartingPos(cell, letters);
                boolean foundForest = cell.getType() == CellType.EMPTY_FOREST;

                if (foundPlayerStartingPos || foundForest) {
                    CellPosition position = new CellPosition(i, j);

                    if (foundPlayerStartingPos) {
                        positions.put(Character.toUpperCase(cell.getPlayerSymbol()), position);
                    } else {
                        positions.put(EMPTY_FOREST_SYMBOL, position);
                    }
                }
            }
        }

        return positions;
    }

    private static boolean isFoundPlayerStartingPos(GameCell cell, ArrayList<Character> letters) {
        return cell.getType() == CellType.PLAYER_STARTING_POSITION
                && letters.contains(cell.getPlayerSymbol());
    }

    private static void setLettersToFind(int nrOfPlayers, ArrayList<Character> letters) {
        for (int i = START_ITERATION_INDEX; i < nrOfPlayers; i++) {
            letters.add(Character.toLowerCase(Utility.getLetterFromNumber(i, RESERVED_CHARACTERS)));
        }
    }

    /**
     * This method shows a list of sessions.
     * If the list is empty, the method does nothing.
     *
     * @param list the list of sessions
     */
    public static void showSessionList(List<Session> list) {
        if (!Utility.isListEmpty(list)) {
            for (Session s : list) {
                InputOutputHandler.printSession(s);
            }
        }
    }

    /**
     * This method handles the start session command.
     * It checks if the parameters are valid. If the parameters are not valid, the method prints an error message.
     * If the parameters are valid, the method creates a new session and adds it to the list.
     *
     * @param command  the command to be handled
     * @param sessions the list of sessions
     * @return the new session
     */
    public static Session handleStartSessionCommand(String command, List<Session> sessions) {
        String[] params = CommandHelper.getStartCommandParameters(command);

        if (params == null || params.length < MIN_NR_OF_PARAMS_FOR_START_COMMAND) {
            InputOutputHandler.showError(WRONG_START_SESSION_PARAMS_ERROR_MESSAGE);
            return null;
        }

        return createAndAddSession(params, sessions);
    }

    /**
     * This method creates and adds a session.
     * If the session id is not valid, the method returns null.
     * If the number of players is not valid, the method returns null.
     * If the matrix is null, the method returns null.
     * Otherwise, the method creates a new session and adds it to the list.
     *
     * @param params   the parameters for the session
     * @param sessions the list of sessions
     * @return the new session
     */
    private static Session createAndAddSession(String[] params, List<Session> sessions) {
        String sessionID = params[SESSION_ID_PARAM_INDEX];

        String filePath = params[FILE_PATH_PARAM_INDEX];
        int nrOfPlayers = Utility.parseIntegerParam(params[NR_OF_PLAYERS_PARAM_INDEX]);
        int seed = Utility.parseIntegerParam(
                params.length > MIN_NR_OF_PARAMS_FOR_START_COMMAND
                        ? params[SEED_PARAM_INDEX]
                        : null);


        GameCell[][] matrix = SessionsHelper.getSessionGameMatrix(filePath);
        if (!validateStartSessionCommand(matrix, sessions, sessionID, params, nrOfPlayers, seed)) {
            return null;
        }

        Map<Character, CellPosition> startingPositions = getForestAndStartingPosition(matrix, nrOfPlayers);

        if (startingPositions.size() < nrOfPlayers) {
            InputOutputHandler.showStartCommandError(TO_MUCH_PLAYERS_ERROR_MESSAGE,
                    matrix, EMPTY_CELL_SYMBOL);
            return null;
        }

        // find if the session has a forest
        Forest forest = getForest(startingPositions);

        return new Session(sessionID, filePath, nrOfPlayers, matrix, startingPositions, seed, forest);
    }

    private static boolean validateStartSessionCommand(GameCell[][] matrix, List<Session> sessions, String sessionID,
        String[] params, int nrOfPlayers, int seed) {
        if (matrix == null || !SessionsHelper.isStartSessionIdValid(sessions, sessionID, matrix)) {
            return false;
        }

        if (!SessionsHelper.checkIfValidNrOfPlayersAndSeed(params, nrOfPlayers, seed)) {
            InputOutputHandler.showStartCommandError(NOT_IN_RANGE_ERROR_MESSAGE,
                    matrix, EMPTY_CELL_SYMBOL);
            return false;
        }

        return true;
    }

    private static Forest getForest(Map<Character, CellPosition> startingPositions) {
        Forest f = null;
        if (startingPositions.containsKey(EMPTY_FOREST_SYMBOL)) {
            CellPosition forestPosition = startingPositions.remove(EMPTY_FOREST_SYMBOL);
            f = new Forest(forestPosition);
        }
        return f;
    }

    /**
     * This method checks if the session id given at start session command is valid.
     * If the session id is already in the list or not alphanumeric, the method returns false.
     * Otherwise, the method returns true.
     *
     * @param sessions  list of sessions
     * @param sessionID session id to check
     * @param matrix game matrix
     * @return true if is valid
     */
    private static boolean isStartSessionIdValid(List<Session> sessions, String sessionID, GameCell[][] matrix) {
        boolean isValid = true;
        String errorText = EMPTY_STRING_SYMBOL;

        if (findSession(sessions, sessionID) != null || !Utility.isAlphanumeric(sessionID)) {
            errorText = getStartSessionErrorText(sessions, sessionID);
            isValid = false;
        }

        if (!isValid) {
            InputOutputHandler.showStartCommandError(errorText, matrix, EMPTY_CELL_SYMBOL);
        }

        return isValid;
    }

    private static String getStartSessionErrorText(List<Session> sessions, String sessionID) {
        return findSession(sessions, sessionID) != null
                ? ALREADY_EXISTING_SESSION_ERROR_MESSAGE
                : NOT_ALPHANUMERICAL_SESSION_ERROR_MESSAGE;
    }

    /**
     * This method sets the active session.
     * If the list is empty, the method does nothing.
     *
     * @param list          the list of sessions
     * @param activeSession the active session
     */
    public static void setActiveSession(List<Session> list, String activeSession) {
        if (!Utility.isListEmpty(list)) {
            for (Session s : list) {
                s.setActive(s.getId().equals(activeSession));
            }
        }
    }
}
