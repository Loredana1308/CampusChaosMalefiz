package edu.kit.kastel.enums;

/**
 * This enum stores all user commands.
 *
 * @author Programmieren-Team
 */
public enum CommandType {
    /**
     * This command is used to quit the game.
     */
    QUIT,
    /**
     * This command is used to show all available commands.
     */
    HELP,
    /**
     * This command is used to show all available sessions.
     */
    SHOW_SESSION,
    /**
     * This command is used to start a new session.
     */
    START_SESSION,
    /**
     * This command is used to delete a session.
     */
    DELETE_SESSION,
    /**
     * This command is used to switch to a session.
     */
    SWITCH_SESSION,
    /**
     * This command is used to show game matrix of current session.
     */
    SHOW,
    /**
     * This command is used to show current player.
     */
    CURRENT_PLAYER,
    /**
     * This command rolls the dice.
     */
    ROLL_DICE,
    /**
     * This command brings a new figure to game.
     */
    NEW_FIGURE,
    /**
     * This command moves the figure.
     */
    MOVE,
    /**
     * This command moves obstacle.
     */
    MOVE_OBSTACLE,
    /**
     * This command skip the turn for current player.
     */
    SKIP_TURN,
    /**
     * This command starts the game one more time.
     */
    REMATCH;

    private static final String USER_COMMAND_SEPARATOR = " ";
    private static final String ENUM_COMMAND_SEPARATOR = "_";

    /**
     * This method returns a string defining enum CommandType.
     *
     * @return a string from enum
     */
    @Override
    public String toString() {
        return this.name().toLowerCase().replace(ENUM_COMMAND_SEPARATOR, USER_COMMAND_SEPARATOR);
    }
}
