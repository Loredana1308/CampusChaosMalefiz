package edu.kit.kastel.game;

import edu.kit.kastel.enums.CommandType;
import edu.kit.kastel.utils.CommandHelper;
import edu.kit.kastel.utils.GameCommandHelper;
import edu.kit.kastel.utils.InputOutputHandler;
import edu.kit.kastel.utils.SessionsHelper;

import java.util.ArrayList;

/**
 * This class is a game executor.
 *
 * @author Programmieren-Team
 */
public final class GameExecutor {
    /**
     * This constant stores minimum number of parameters for the start command.
     */
    public static final int MIN_NR_OF_PARAMS_FOR_START_COMMAND = 3;
    private static final String ALREADY_ACTIVE_SESSION_ERROR_MESSAGE = "this session is already active.";
    private static final String NOT_AVAILABLE_COMMAND_ERROR_MESSAGE = "this command is not available.";
    private final InputOutputHandler inoutHandler;
    private final ArrayList<Session> sessions;
    private String activeSessionId;
    private Session activeSession;

    /**
     * This constructor initializes the game executor.
     */
    public GameExecutor() {
        this.inoutHandler = new InputOutputHandler();
        this.sessions = new ArrayList<>();
    }

    /**
     * This method runs the game.
     * It reads user commands and handles them.
     * It runs until the user quits the game.
     */
    public void run() {
        InputOutputHandler.showGreeting();

        String command = this.inoutHandler.readCommand();
        while (!CommandHelper.isDesiredCommand(command, CommandType.QUIT, false)) {
            this.handleCommands(command);
            command = this.inoutHandler.readCommand();
        }

        inoutHandler.closeScanner();
    }

    /**
     * This method handles user commands.
     * It reads the command and decides which action to take.
     * It also prints the result of the action.
     * It also handles the case when the command is not available.
     *
     * @param command the command to be handled
     */
    private void handleCommands(String command) {
        CommandType commandType = CommandHelper.getCommandType(command);
        if (InputOutputHandler.showErrorIfNeeded(commandType)) {
            return;
        }

        if (!CommandHelper.isAvailableCommand(commandType, activeSession, sessions)) {
            InputOutputHandler.showError(NOT_AVAILABLE_COMMAND_ERROR_MESSAGE);
            return;
        }

        switch (commandType) {
            case HELP:
                InputOutputHandler.handleHelpCommand(this.sessions, activeSession);
                break;
            case SHOW_SESSION:
            case DELETE_SESSION:
            case SWITCH_SESSION:
                Session s = CommandHelper.handleOneParameterCommand(command, commandType, this.sessions);
                processOneParameterCommand(s, commandType);
                break;
            case START_SESSION:
                handleStartSessionCommand(command);
                break;
            case SHOW,
                 CURRENT_PLAYER,
                 ROLL_DICE,
                 NEW_FIGURE,
                 MOVE,
                 MOVE_OBSTACLE,
                 SKIP_TURN,
                 REMATCH:
                GameCommandHelper.handleActiveSessionCommand(commandType, this.activeSession, command);
                break;
            default:
                break;
        }
    }

    private void processOneParameterCommand(Session s, CommandType commandType) {
        if (s != null) {
            if (commandType == CommandType.DELETE_SESSION) {
                this.deleteSession(s);
            } else if (commandType == CommandType.SWITCH_SESSION) {
                this.switchSession(s.getId());
            }
        }
    }

    /**
     * This method handles the start session command.
     * It creates a new session and sets it as active.
     * It also prints the matrix of the session.
     *
     * @param command the command to be handled
     */
    private void handleStartSessionCommand(String command) {
        Session newSession = SessionsHelper.handleStartSessionCommand(command, this.sessions);

        if (newSession == null) {
            return;
        }

        this.addSession(newSession);
        setActiveSession(newSession.getId());
        InputOutputHandler.printMatrix(activeSession.getMatrix(false),
                activeSession.getCurrentPlayer().getName());
        InputOutputHandler.printSessionId(activeSessionId);
        InputOutputHandler.printPlayerTurn(activeSession.getCurrentPlayer().getName());
    }

    /**
     * This method handles session deletion.
     * It deletes the session from the list of sessions.
     *
     * @param session to delete
     */
    private void deleteSession(Session session) {
        SessionsHelper.removeSession(this.sessions, session);
        if (session.getId().equals(activeSessionId)) {
            setActiveSession(null);
        }
    }

    /**
     * This method handles the switch session command.
     * It switches the active session to the one with the provided id.
     * It also prints the session id.
     *
     * @param sessionID the session id to be handled
     */
    private void switchSession(String sessionID) {
        if (this.activeSessionId != null && this.activeSessionId.equals(sessionID)) {
            InputOutputHandler.showError(ALREADY_ACTIVE_SESSION_ERROR_MESSAGE);
            return;
        }

        setActiveSession(sessionID);
        InputOutputHandler.printSessionId(sessionID);
    }

    /**
     * This method adds a session.
     *
     * @param session the session to be added
     */
    private void addSession(Session session) {
        this.sessions.add(session);
    }

    /**
     * This method sets the active session.
     *
     * @param sessionID the session ID
     */
    private void setActiveSession(String sessionID) {
        this.activeSessionId = sessionID;
        this.activeSession = SessionsHelper.findSession(this.sessions, sessionID);
        SessionsHelper.setActiveSession(this.sessions, sessionID);
    }
}