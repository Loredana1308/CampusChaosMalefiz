package edu.kit.kastel.game;

import edu.kit.kastel.utils.InputOutputHandler;
import edu.kit.kastel.utils.Utility;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static edu.kit.kastel.game.Session.NON_EXISTENT_DICE_NUMBER_VALUE;
import static edu.kit.kastel.utils.SessionsHelper.RESERVED_CHARACTERS;

/**
 * This class represents a player.
 *
 * @author Programmieren-Team
 */
public final class Player {
    /**
     * Maximum number of figures for a player.
     */
    public static final int MAX_NUMBER_OF_FIGURES = 5;
    private static final String INVALID_FIGURE_TO_HIT_ERROR_MESSAGE = "the figure does not exist in enemy list.";
    private static final String SENTENCE_WORDS_SEPARATOR_SYMBOL = " ";
    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int START_ITERATION_INDEX = 0;
    private static final int START_PLAYER_COUNTING_INDEX_VALUE = 1;
    private static final String NOT_ROLLED_DICE_SYMBOL = "?";
    private static final String DICE_ROLL_INFO_STRING = "Dice Roll";
    private static final String PLAYER_PREFIX_MESSAGE = "It's player ";
    private static final String PLAYER_TURN_SUFFIX_MESSAGE = "'s turn.";

    private final Queue<Figure> unusedFiguresQueue = new LinkedList<>();
    private final Queue<Figure> playingFiguresQueue = new LinkedList<>();
    private final char name;
    private final CellPosition startPos;
    private int dice;
    private GameCell pendingObstacle;

    /**
     * This constructor creates a player with given name, dice, start position.
     *
     * @param name        the name
     * @param dice        the dice
     * @param startPos the start position
     */
    public Player(char name, int dice, CellPosition startPos) {
        this.name = name;
        this.dice = dice;
        this.startPos = new CellPosition(startPos);

        for (int i = START_PLAYER_COUNTING_INDEX_VALUE; i <= MAX_NUMBER_OF_FIGURES; i++) {
            unusedFiguresQueue.add(new Figure(name, i));
        }
        this.pendingObstacle = null;
    }

    /**
     * Create players list method.
     * This method creates a list of players with given number of players and starting positions.
     * It returns the list of players.
     *
     * @param nrPlayers          the number of players
     * @param startingPositions  the starting positions
     * @return the list of players
     */
    public static LinkedList<Player> createPlayersList(int nrPlayers,
                                                       Map<Character, CellPosition> startingPositions) {
        LinkedList<Player> players = new LinkedList<>();
        for (int i = START_ITERATION_INDEX; i < nrPlayers; i++) {
            char playerChar = Utility.getLetterFromNumber(i, RESERVED_CHARACTERS);
            // get starting position
            CellPosition position = startingPositions.get(playerChar);

            Player p = new Player(playerChar, NON_EXISTENT_DICE_NUMBER_VALUE, position);
            players.add(p);
        }

        return players;
    }

    /**
     * This method returns string representation of the player.
     * It includes the player name and the dice number.
     *
     * @return the player
     */
    @Override
    public String toString() {
        String playerInfo = PLAYER_PREFIX_MESSAGE + name + PLAYER_TURN_SUFFIX_MESSAGE
                + SENTENCE_WORDS_SEPARATOR_SYMBOL;
        String diceInfo = DICE_ROLL_INFO_STRING + KEY_VALUE_SEPARATOR
                + (dice == NON_EXISTENT_DICE_NUMBER_VALUE ? NOT_ROLLED_DICE_SYMBOL : dice);

        return playerInfo + diceInfo;
    }

    /**
     * This method adds a new figure to the game.
     * If there are no unused figures, it returns null.
     *
     * @return created figure
     */
    public Figure addNewFigureToGame() {
        if (unusedFiguresQueue.isEmpty()) {
            return null;
        }

        Figure figure = unusedFiguresQueue.poll();
        playingFiguresQueue.add(figure);
        return figure;
    }

    /**
     * This method checks if the figure is on the playing field.
     * If it is, it returns the figure. If it is not, it returns null.
     *
     * @param figureName the figure name
     * @return the figure
     */
    public Figure checkIfFigureOnPlayingField(String figureName) {
        for (Figure figure : this.playingFiguresQueue) {
            if (figure.toString().equals(figureName)) {
                return figure;
            }
        }

        return null;
    }

    /**
     * Getter for pending obstacle.
     *
     * @return game cell of the pending obstacle
     */
    public GameCell getPendingObstacle() {
        return pendingObstacle;
    }

    /**
     * Setter for pending obstacle.
     * If the pending obstacle is null, it is set to null.
     *
     * @param pendingObstacle the game cell of the pending obstacle
     */
    public void setPendingObstacle(GameCell pendingObstacle) {
        if (pendingObstacle == null) {
            this.pendingObstacle = null;
            return;
        }

        this.pendingObstacle = new GameCell(pendingObstacle);
    }

    /**
     * This method checks if all figures are used.
     *
     * @return true if all figures are used, false otherwise
     */
    public boolean areUsedAllFigures() {
        return this.unusedFiguresQueue.isEmpty();
    }

    /**
     * This method hits the players figure.
     * If the figure is found, it is removed from the playing field and added to the unused figures.
     * If the figure is not found, a message is printed.
     *
     * @param figure the figure to be hit
     * @param session session
     */
    public void hitPlayer(Figure figure, Session session) {
        if (session.getForest() != null) {
            session.getForest().addFigure(figure);
            figure.setPosition(session.getForest().getPos());
            session.updateForestSymbol();
            return;
        }

        Queue<Figure> tempQueue = new LinkedList<>();

        boolean removed = false;

        while (!playingFiguresQueue.isEmpty()) {
            Figure current = playingFiguresQueue.poll();

            if (current.toString().equals(figure.toString()) && !removed) {
                unusedFiguresQueue.add(current);
                removed = true;
            } else {
                // If it doesn't match, add it to the temporary queue
                tempQueue.add(current);
            }
        }

        // Restore the elements back to the playingFiguresQueue
        playingFiguresQueue.addAll(tempQueue);

        if (!removed) {
            InputOutputHandler.showError(INVALID_FIGURE_TO_HIT_ERROR_MESSAGE);
        }
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public char getName() {
        return name;
    }

    /**
     * Getter for dice.
     *
     * @return the dice
     */
    public int getDice() {
        return dice;
    }

    /**
     * Setter for dice.
     *
     * @param dice the dice
     */
    public void setDice(int dice) {
        this.dice = dice;
    }

    /**
     * Getter for start position.
     *
     * @return the start position
     */
    public CellPosition getStartPos() {
        return startPos;
    }
}
