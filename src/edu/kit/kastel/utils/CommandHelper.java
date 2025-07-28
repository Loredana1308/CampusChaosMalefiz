package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CommandType;
import edu.kit.kastel.game.Session;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static edu.kit.kastel.game.GameExecutor.MIN_NR_OF_PARAMS_FOR_START_COMMAND;
import static edu.kit.kastel.game.Session.NON_EXISTENT_DICE_NUMBER_VALUE;

/**
 * This class stores all session information.
 *
 * @author Programmieren-Team
 */
public final class CommandHelper {
    private static final String WRONG_COMMAND_ERROR_MESSAGE = "wrong command or parameters.";
    private static final String COMMAND_SEPARATOR_SYMBOL = " ";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final int MAX_NR_OF_PARAMS_FOR_START_COMMAND = 4;

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private CommandHelper() {
    }

    /**
     * This method checks if the command is the desired command.
     *
     * @param command               the command
     * @param type                  the command type
     * @param commandWithParameters the command with parameters
     * @return true if the command is the desired command
     */
    public static boolean isDesiredCommand(String command, CommandType type, boolean commandWithParameters) {
        return commandWithParameters
                ? command.trim().startsWith(type.toString())
                : command.trim().equals(type.toString());
    }

    /**
     * This method handles one parameter commands.
     * It shows, deletes or switches the session.
     *
     * @param command     the command to be handled
     * @param commandType the command type
     * @param sessions    the list of sessions
     * @return the session
     */
    public static Session handleOneParameterCommand(String command, CommandType commandType, List<Session> sessions) {
        String sessionID = CommandHelper.getSingleParameter(command, commandType);

        if (sessionID == null && commandType == CommandType.SHOW_SESSION) {
            SessionsHelper.showSessionList(sessions);
            return null;
        }

        Session session = SessionsHelper.findSession(sessions, sessionID);

        if (session == null) {
            InputOutputHandler.showError(WRONG_COMMAND_ERROR_MESSAGE);
            return null;
        }

        return switch (commandType) {
            case SHOW_SESSION -> {
                InputOutputHandler.printSession(session);
                yield session;
            }
            case DELETE_SESSION, SWITCH_SESSION -> session;
            default -> null;
        };
    }

    /**
     * This method returns the single parameter from command.
     * For example if the command is "show session TestSession", the method will return "TestSession".
     *
     * @param command the command
     * @param type    the command type
     * @return the parameter
     */
    public static String getSingleParameter(String command, CommandType type) {
        if (command == null || type == null) {
            return null;
        }

        String commandPrefix = command.length() > type.toString().length()
                ? type + COMMAND_SEPARATOR_SYMBOL
                : type.toString();

        String parameter = command.replace(commandPrefix, EMPTY_STRING_SYMBOL).trim();
        return !parameter.isEmpty() ? parameter : null;
    }

    /**
     * This method returns the command type from a given command text.
     * It checks if the command is available.
     * It also checks if the command has the needed parameters.
     * It returns the command type.
     *
     * @param command the command
     * @return the command type
     */
    public static CommandType getCommandType(String command) {
        Map<CommandType, Boolean> options = getCommandTypeMap();

        for (Map.Entry<CommandType, Boolean> entry : options.entrySet()) {
            if (isDesiredCommand(command, entry.getKey(), entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    private static Map<CommandType, Boolean> getCommandTypeMap() {
        // key is command type, value is true if it has command parameters
        Map<CommandType, Boolean> options = new LinkedHashMap<>();
        options.put(CommandType.SHOW_SESSION, true);
        options.put(CommandType.QUIT, false);
        options.put(CommandType.HELP, false);
        options.put(CommandType.DELETE_SESSION, true);
        options.put(CommandType.SWITCH_SESSION, true);
        options.put(CommandType.START_SESSION, true);
        options.put(CommandType.SHOW, false);
        options.put(CommandType.CURRENT_PLAYER, false);
        options.put(CommandType.ROLL_DICE, true);
        options.put(CommandType.NEW_FIGURE, false);
        options.put(CommandType.MOVE_OBSTACLE, true);
        options.put(CommandType.MOVE, true);
        options.put(CommandType.SKIP_TURN, false);
        options.put(CommandType.REMATCH, false);
        return options;
    }

    /**
     * This method returns the parameters from a start command.
     * It returns the session id, file path, number of players and seed.
     * If the command is not valid, it returns null.
     *
     * @param command the command type
     * @return the parameters, session id, file path, number of players and seed
     */
    public static String[] getStartCommandParameters(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        String[] paramArray = command.replace(CommandType.START_SESSION + COMMAND_SEPARATOR_SYMBOL,
                        EMPTY_STRING_SYMBOL)
                .trim().split(COMMAND_SEPARATOR_SYMBOL);

        return paramArray.length < MIN_NR_OF_PARAMS_FOR_START_COMMAND
                || paramArray.length > MAX_NR_OF_PARAMS_FOR_START_COMMAND
                ? null
                : paramArray;
    }

    /**
     * This method checks if the command is available.
     * It checks if the command is available based on the command type, active session and list of sessions.
     *
     * @param type          the command type
     * @param activeSession the active session
     * @param sessions      the list of sessions
     * @return true if the command is available
     */
    public static boolean isAvailableCommand(CommandType type, Session activeSession, List<Session> sessions) {
        boolean hasActiveSession = activeSession != null;
        boolean hasWinner = false;
        boolean hasRolledDice = false;
        boolean allPiecesAreInPlay = false;
        boolean hasObstacle = false;
        boolean isEmptyStartingPosition = false;

        if (hasActiveSession) {
            hasWinner = activeSession.getWinner() != null;
            hasRolledDice = activeSession.getCurrentPlayer().getDice() != NON_EXISTENT_DICE_NUMBER_VALUE;
            allPiecesAreInPlay = activeSession.getCurrentPlayer().areUsedAllFigures();
            hasObstacle = activeSession.getCurrentPlayer().getPendingObstacle() != null;
            isEmptyStartingPosition = activeSession.isEmptyStartingPosition();
        }

        // Check if the command is available
        return switch (type) {
            case DELETE_SESSION, SWITCH_SESSION, SHOW_SESSION ->
                    checkIfAvailableOneParameterCommand(type, sessions, activeSession);

            case HELP, START_SESSION, QUIT -> true;

            case SHOW -> hasActiveSession;

            case CURRENT_PLAYER -> hasActiveSession && !hasWinner;

            case ROLL_DICE -> hasActiveSession && !hasWinner && !hasRolledDice;

            case NEW_FIGURE -> hasActiveSession && !hasWinner && !allPiecesAreInPlay
                    && isEmptyStartingPosition;

            case MOVE ->
                    hasActiveSession && !hasWinner && hasRolledDice && !hasObstacle;

            case SKIP_TURN ->
                    hasActiveSession && !hasWinner && hasRolledDice;

            case MOVE_OBSTACLE -> hasActiveSession && !hasWinner && hasObstacle;

            case REMATCH -> hasActiveSession && hasWinner;
        };
    }

    /**
     * This method checks if the switch, delete and start commands are available.
     * It checks if the command is available based on the command type, list of sessions and active session.
     *
     * @param commandType   current command type
     * @param sessions      current list of sessions
     * @param activeSession current active session
     * @return true if the command is available
     */
    private static boolean checkIfAvailableOneParameterCommand(CommandType commandType,
                                                   List<Session> sessions, Session activeSession) {
        return switch (commandType) {
            case DELETE_SESSION, SHOW_SESSION -> !sessions.isEmpty();
            case SWITCH_SESSION -> sessions.size() == 1 && activeSession == null || sessions.size() >= 2;
            default -> false;
        };
    }
}
