package edu.kit.kastel.game;

import edu.kit.kastel.enums.CellType;
import edu.kit.kastel.utils.InputOutputHandler;
import edu.kit.kastel.utils.MoveCommandsHelper;
import edu.kit.kastel.utils.Utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static edu.kit.kastel.utils.SessionsHelper.RESERVED_CHARACTERS;
import static edu.kit.kastel.utils.Utility.INVALID_INTEGER_VALUE;

/**
 * This class stores all session information.
 * @author Programmieren-Team
 */
public final class Session {
    /**
     * Non-existing dice number value. Used for validation.
     */
    public static final int NON_EXISTENT_DICE_NUMBER_VALUE = -1;
    private static final String NOT_AVAILABLE_COMMAND_ERROR_MESSAGE = "this command is not available.";
    private static final String PROTECTED_ZONE_HIT_FIGURE_ERROR_MESSAGE = "the figure you want to hit is placed "
            + "on protected zone and cannot be hit.";
    private static final String ARROW_REPRESENTATION = " -> ";
    private static final String INFO_SEPARATOR = " | ";
    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final String ACTIVE_SESSION_SYMBOL = "*";
    private static final String PLAYERS_INFO_STRING = "Players";
    private static final String SEED_INFO_STRING = "Seed";
    private static final String MAP_INFO_STRING = "Map";
    private static final int START_ITERATION_INDEX = 0;

    private final String id;
    private final String fileToField;
    private final int seed;
    private final GameCell[][] originalGameMatrix;
    private final Map<Character, CellPosition> startingPositions;
    private final Forest forest;
    private final LinkedList<Player> players;
    private boolean isActive;
    private GameCell[][] gameMatrix;
    private Player currentPlayer;
    private final Random random;
    private Player winner;

    /**
     * This constructor initializes a session.
     * @param id          the session id
     * @param fileToField the file to field
     * @param nrPlayers   number of players
     * @param gameMatrix  the game matrix
     * @param startingPositions starting positions
     * @param seed seed
     * @param forest forest
     */
    public Session(String id, String fileToField, int nrPlayers, GameCell[][] gameMatrix,
                   Map<Character, CellPosition> startingPositions, int seed, Forest forest) {
        this.id = id;
        this.fileToField = fileToField;
        this.startingPositions = startingPositions;
        this.players = Player.createPlayersList(Math.min(nrPlayers, startingPositions.size()), startingPositions);
        this.currentPlayer = this.players.getFirst();
        this.seed = seed;
        this.random = hasSeed() ? new Random(this.seed) : null;
        this.gameMatrix = gameMatrix;
        this.originalGameMatrix = this.getMatrix(false);
        this.forest = forest;
    }

    /**
     * This method return string representation of the session.
     * @return the string representation of a session
     */
    public String toString() {
        String idInfo = id + (isActive ? ACTIVE_SESSION_SYMBOL : EMPTY_STRING_SYMBOL);
        String playerInfo = PLAYERS_INFO_STRING + KEY_VALUE_SEPARATOR + Utility.mergeLettersWithComma(players);
        String mapInfo = MAP_INFO_STRING + KEY_VALUE_SEPARATOR + fileToField;
        String seedInfo = INFO_SEPARATOR + SEED_INFO_STRING + KEY_VALUE_SEPARATOR + seed;

        String s = idInfo + ARROW_REPRESENTATION + playerInfo + INFO_SEPARATOR + mapInfo;
        return !hasSeed() ? s : s + seedInfo;
    }

    /**
     * This method places a figure on the matrix. If the cell has an enemy figure, the enemy is hit.
     * If oldPosRow and oldPosCol are not -1, the old figure is removed from the matrix.
     * @param oldPos the old position
     * @param pos    the row position
     * @param figure    the figure
     * @return the cell
     */
    public GameCell placeFigureOnMatrix(CellPosition oldPos, CellPosition pos, Figure figure) {
        GameCell cell = this.gameMatrix[pos.getRow()][pos.getColumn()];
        Figure f = cell.getFigure();

        // handle hit player method if needed
        if (handleHitPlayer(cell, f)) {
            return null;
        }

        // process visited game cell, remove figure etc.
        handleOldPositionChanges(oldPos, figure);

        // place figure on new cell
        figure.setPosition(pos);
        cell.setPosition(pos);
        cell.setFigure(figure);
        this.gameMatrix[pos.getRow()][pos.getColumn()] = cell;
        return cell;
    }

    private void handleOldPositionChanges(CellPosition oldPos, Figure figure) {
        if (oldPos.isValidPosition()) {
            if (forest != null && oldPos.equals(forest.getPos())) {
                Figure forestFigure = forest.getFigureByName(figure.toString());
                forest.removeFigure(forestFigure);
                this.updateForestSymbol();
            } else {
                this.gameMatrix[oldPos.getRow()][oldPos.getColumn()].setFigure(null);
            }
        }
    }

    private boolean handleHitPlayer(GameCell cell, Figure f) {
        if (cell.hasFigure()) {
            // found enemy player to hit
            Player enemy = Utility.findPlayerByFigure(f, players);

            if (cell.isProtectedZone()) {
                InputOutputHandler.showError(PROTECTED_ZONE_HIT_FIGURE_ERROR_MESSAGE);
                return true;
            }

            if (enemy != null && enemy.getName() != currentPlayer.getName()) {
                enemy.hitPlayer(f, this);
                InputOutputHandler.printPlayerHit(this.currentPlayer.getName(), enemy.getName());
            }
        }
        return false;
    }

    /**
     * This method checks if the starting position is empty.
     * @return true if the starting position is empty, false otherwise
     */
    public boolean isEmptyStartingPosition() {
        CellPosition startingPosition = this.startingPositions.get(currentPlayer.getName());

        return !this.gameMatrix[startingPosition.getRow()][startingPosition.getColumn()].hasFigure();
    }

    /**
     * This method changes the current player.
     */
    public void changeCurrentPlayer() {
        this.currentPlayer.setDice(-1);
        int currentIndex = this.players.indexOf(currentPlayer);

        if (currentIndex != INVALID_INTEGER_VALUE && currentIndex < players.size() - 1) {
            this.currentPlayer = this.players.get(currentIndex + 1);
        } else if (currentIndex + 1 >= players.size()) {
            this.currentPlayer = this.players.getFirst();
        }

        InputOutputHandler.printPlayerTurn(this.currentPlayer.getName());
    }

    /**
     * This method handles the move command. It moves the figure in the given directions.
     * @param figure     the figure
     * @param directions the directions
     */
    public void handleMove(Figure figure, String[] directions) {
        CellPosition initialPos = figure.getPosition();

        List<CellPosition> visited = new ArrayList<>();
        visited.add(new CellPosition(initialPos));

        CellPosition newPos = MoveCommandsHelper.processMoveSteps(directions, currentPlayer.getDice(),
                initialPos, visited, gameMatrix);

        if (newPos == null) {
            return;
        }

        processFigureMoving(initialPos, newPos, figure);
    }

    /**
     * This method processes the figure moving.
     * @param initialPos the initial pos
     * @param newPos     the new pos
     * @param figure     the figure
     */
    private void processFigureMoving(CellPosition initialPos, CellPosition newPos, Figure figure) {
        GameCell lastModifiedCell = this.placeFigureOnMatrix(initialPos, newPos, figure);

        if (lastModifiedCell != null) {
            if (lastModifiedCell.hasObstacle()) {
                this.removeObstacleFromMatrix(lastModifiedCell);
            } else if (!lastModifiedCell.isTarget()) {
                this.changeCurrentPlayer();
            } else if (lastModifiedCell.isTarget()) {
                this.winner = this.currentPlayer;
                InputOutputHandler.printWinner(this.currentPlayer.getName());
            }
        }
    }

    /**
     * This method handles the move obstacle command. It moves the obstacle in the given direction.
     * @param directions the directions
     */
    public void handleMoveObstacle(String[] directions) {
        if (this.getCurrentPlayer().getPendingObstacle() == null) {
            InputOutputHandler.showError(NOT_AVAILABLE_COMMAND_ERROR_MESSAGE);
        }

        if (MoveCommandsHelper.processMoveObstacleSteps(directions, this)) {
            processObstacleMoving();
        }
    }

    /**
     * This method processes the obstacle moving.
     */
    private void processObstacleMoving() {
        GameCell obstacle = currentPlayer.getPendingObstacle();
        obstacle.setHasFigure(false);
        CellPosition obstaclePos = obstacle.getPos();
        this.gameMatrix[obstaclePos.getRow()][obstaclePos.getColumn()] = obstacle;
        this.currentPlayer.setPendingObstacle(null);
        this.changeCurrentPlayer();
    }

    /**
     * This method is called when a figure removes an obstacle from matrix
     * and places it temporarily in the player's pending obstacle.
     * @param cell the cell where teh obstacle is placed
     */
    private void removeObstacleFromMatrix(GameCell cell) {
        this.getCurrentPlayer().setPendingObstacle(cell);
        CellType cellType = cell.getType();
        CellPosition pos = cell.getPos();
        this.gameMatrix[pos.getRow()][pos.getColumn()].setHasObstacle(false,
                cellType == CellType.OBSTACLE_VILLAGE);
    }

    /**
     * Getter for session id.
     * @return the session id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for isActive.
     * @param active the active
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Getter for original and simple game matrix.
     * @param isOriginalMatrix true if originalGameMatrix needs to be copied.
     * @return the original fgame matrix
     */
    public GameCell[][] getMatrix(boolean isOriginalMatrix) {
        return Utility.getMatrixCopy(isOriginalMatrix && originalGameMatrix != null ? originalGameMatrix : gameMatrix);
    }

    /**
     * Rematch session. Starts a new game with the same players.
     */
    public void rematchSession() {
        int nrPlayers = this.players.size();
        this.players.clear();

        for (int i = START_ITERATION_INDEX; i < nrPlayers; i++) {
            char playerChar = Utility.getLetterFromNumber(i, RESERVED_CHARACTERS);
            CellPosition pos = startingPositions.get(playerChar);

            Player p = new Player(playerChar, NON_EXISTENT_DICE_NUMBER_VALUE, pos);
            this.players.add(p);

            if (i == START_ITERATION_INDEX) {
                this.currentPlayer = this.players.getFirst();
                InputOutputHandler.printPlayerTurn(this.currentPlayer.getName());
            }
        }

        this.winner = null;
        this.gameMatrix = getMatrix(true);
    }

    /**
     * Getter for winner.
     * @return the player winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Getter for matrix row length.
     * @return row length
     */
    public int getMatrixRowLength() {
        return this.gameMatrix.length;
    }

    /**
     * Getter for matrix column length.
     * @return column length
     */
    public int getMatrixColumnLength() {
        return this.gameMatrix[0].length;
    }

    /**
     * Getter for current player.
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for hasSeed.
     * @return true if it has seed, false otherwise
     */
    public boolean hasSeed() {
        return this.seed != -1;
    }

    /**
     * Getter for forest.
     * @return the forest
     */
    public Forest getForest() {
        return forest;
    }

    /**
     * Updates the forest with needed symbol (f or F).
     */
    public void updateForestSymbol() {
        this.gameMatrix[forest.position.getRow()][forest.position.getColumn()].setType(forest.getType());
    }

    /**
     * Getter for cell type for a cell in the matrix.
     * @param position position
     * @return cell type
     */
    public CellType getCellType(CellPosition position) {
        return this.gameMatrix[position.getRow()][position.getColumn()].getType();
    }

    /**
     * Method that checks if a matrix cell has a figure.
     * @param pos position
     * @return true if it has a figure, false otherwise
     */
    public boolean hasMatrixCellFigure(CellPosition pos) {
        return this.gameMatrix[pos.getRow()][pos.getColumn()].hasFigure();
    }

    /**
     * Getter for random.
     * @return the random
     */
    public Random getRandom() {
        return random;
    }
}
