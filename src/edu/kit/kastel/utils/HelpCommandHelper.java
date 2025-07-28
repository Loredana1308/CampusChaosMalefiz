package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CommandType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class stores all command descriptions. Used for help command.
 *
 * @author Programmieren-Team
 */
public final class HelpCommandHelper {
    /**
     * This map stores all command descriptions.
     */
    public static final Map<CommandType, String> SORTED_COMMAND_DESCRIPTIONS;
    private static final String HELP_COMMAND_DESCRIPTION = "help: This command displays a list of available commands.";
    private static final String DELETE_SESSION_COMMAND_DESCRIPTION = "delete session: This command deletes a session. "
            + "Please add a valid session_id in order to delete it. Example: delete session TestSession.";
    private static final String QUIT_COMMAND_DESCRIPTION = "quit: This command ends the game.";
    private static final String SHOW_SESSION_COMMAND_DESCRIPTION = "show session: This command shows all details "
            + "about existing sessions. The session that is active has an * as a suffix. If a session id is provided "
            + "after the command, only the details about it are shown.";
    private static final String SWITCH_SESSION_COMMAND_DESCRIPTION = "switch session: This command change the active "
            + "game. Please add a valid session_id in order to switch it. Example: switch session TestSession";
    private static final String START_SESSION_COMMAND_DESCRIPTION = "start session: This command creates and starts "
            + "a game session. The command must have three or four parameters, each separated by a space: session_id, "
            + "file_to_field, num_of_players and optionally seed. Number of players must be between 2 and 21.";
    private static final String SHOW_COMMAND_DESCRIPTION = "show: This command displays the playing field of the "
            + "current session. No parameters needed.";
    private static final String CURRENT_PLAYER_COMMAND_DESCRIPTION = "current player: This command displays the current "
            + "player and rolled dice. If not dice was rolled, a question mark will be shown. No parameters needed.";
    private static final String ROLL_DICE_COMMAND_DESCRIPTION = "roll dice: This command rolls a dice. Please add a "
            + "number between 1 and 6 after command. If the session was created with seed, no number is needed. "
            + "Example: roll dice 6.";
    private static final String NEW_FIGURE_COMMAND_DESCRIPTION = "new figure: This command brings a new figure "
            + "into play. No need for parameters.";
    private static final String MOVE_COMMAND_DESCRIPTION = "move: Moves a piece of the current player. Please add "
            + "a valid figure name, followed by a non-empty list of pair of distances and directions. "
            + "Example: move A1 1 up 2 left.";
    private static final String MOVE_OBSTACLE_COMMAND_DESCRIPTION = "move obstacle: This command moves an obstacle "
            + "that is currently reached by a figure. The command must contain 2 distances and 2 directions. "
            + "Example : move obstacle 3 up 4 right.";
    private static final String SKIP_TURN_COMMAND_DESCRIPTION = "skip turn: This command skips the current players "
            + "turn. No need for parameters.";
    private static final String REMATCH_COMMAND_DESCRIPTION = "rematch: This command the same game one more time. "
            + "No need for parameters.";

    static {
        Map<CommandType, String> commandDescriptions = new HashMap<>();
        commandDescriptions.put(CommandType.HELP, HELP_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.DELETE_SESSION, DELETE_SESSION_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.QUIT, QUIT_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.SHOW_SESSION, SHOW_SESSION_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.SWITCH_SESSION, SWITCH_SESSION_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.START_SESSION, START_SESSION_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.SHOW, SHOW_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.CURRENT_PLAYER, CURRENT_PLAYER_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.ROLL_DICE, ROLL_DICE_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.NEW_FIGURE, NEW_FIGURE_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.MOVE, MOVE_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.MOVE_OBSTACLE, MOVE_OBSTACLE_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.SKIP_TURN, SKIP_TURN_COMMAND_DESCRIPTION);
        commandDescriptions.put(CommandType.REMATCH, REMATCH_COMMAND_DESCRIPTION);

        SORTED_COMMAND_DESCRIPTIONS = sortByEnumKeyLexicographically(commandDescriptions);
    }

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private HelpCommandHelper() {
    }

    /**
     * This method sorts the map by the enum key lexicographically.
     *
     * @param map the map to be sorted
     * @return the sorted map
     */
    private static Map<CommandType, String> sortByEnumKeyLexicographically(Map<CommandType, String> map) {
        // Convert the map entries to a list
        List<Map.Entry<CommandType, String>> entryList = new ArrayList<>(map.entrySet());

        // Sort the list based on the toString() value of the keys
        entryList.sort(Comparator.comparing(e -> e.getKey().toString()));

        // Create a new LinkedHashMap to maintain the order of the sorted entries
        Map<CommandType, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<CommandType, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
