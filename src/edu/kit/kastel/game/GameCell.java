package edu.kit.kastel.game;

import edu.kit.kastel.enums.CellType;

/**
 * This class represents a cell in the game matrix.
 *
 * @author Programmieren-Team
 */
public class GameCell {
    /**
     * Symbol used to indicate pathway cell.
     */
    public static final char PATHWAY_SYMBOL = 'P';
    /**
     * Symbol used to indicate pathway village cell.
     */
    public static final char PATHWAY_VILLAGE_SYMBOL = 'p';
    /**
     * Symbol used to indicate target cell.
     */
    public static final char TARGET_SYMBOL = 'T';
    /**
     * Symbol used to indicate protected zone cell.
     */
    public static final char PROTECTED_ZONE_SYMBOL = 'Z';
    /**
     * Symbol used to indicate empty forest cell.
     */
    public static final char EMPTY_FOREST_SYMBOL = 'f';
    /**
     * Symbol used to indicate occupied forest cell.
     */
    public static final char OCCUPIED_FOREST_SYMBOL = 'F';
    /**
     * Symbol used to indicate player starting position symbol cell.
     */
    public static final char PLAYER_STARTING_POSITION_SYMBOL = '\u0000';
    /**
     * Symbol used to indicate non-existing cell.
     */
    public static final char NON_EXISTING_CELL_SYMBOL = '\u0000';
    /**
     * Symbol used to indicate empty cell.
     */
    public static final char EMPTY_CELL_SYMBOL = ' ';
    /**
     * Symbol used to indicate obstacle cell.
     */
    public static final char OBSTACLE_SYMBOL = 'O';
    /**
     * Symbol used to indicate obstacle cell located in a village.
     */
    public static final char OBSTACLE_VILLAGE_SYMBOL = 'o';

    protected CellType type;
    protected CellPosition position;
    private char playerSymbol;
    private boolean hasFigure;
    private Figure figure;
    private boolean hasObstacle;

    /**
     * This constructor creates a cell with given type, position.
     *
     * @param type   the type
     * @param position position
     */
    public GameCell(CellType type, CellPosition position) {
        this.type = type;
        this.position = position;
    }

    /**
     * This constructor creates a cell with given type, position and player symbol.
     *
     * @param type         the type
     * @param position       the position
     * @param playerSymbol the player symbol
     */
    public GameCell(CellType type, CellPosition position, char playerSymbol) {
        this(type, position);
        this.playerSymbol = playerSymbol;
        this.hasFigure = false;
        this.figure = null;
    }

    /**
     * This constructor creates a cell with given type, position, player symbol and figure.
     *
     * @param type         the type
     * @param position       the position
     * @param playerSymbol the player symbol
     * @param hasFigure    the figure
     * @param figure       the figure
     */
    public GameCell(CellType type, CellPosition position, char playerSymbol, boolean hasFigure, Figure figure) {
        this(type, position, playerSymbol);
        this.hasFigure = hasFigure;
        this.figure = figure;
    }

    /**
     * This constructor creates a cell with given member values.
     *
     * @param type         the type
     * @param position     the position
     * @param playerSymbol the player symbol
     * @param hasFigure    the figure
     * @param figure       the figure
     * @param hasObstacle  the obstacle
     */
    public GameCell(CellType type, CellPosition position, char playerSymbol, boolean hasFigure,
                    Figure figure, boolean hasObstacle) {
        this(type, position, playerSymbol, hasFigure, figure);
        this.hasObstacle = hasObstacle;
    }

    /**
     * This constructor creates a cell with given type, position and hasObstacle.
     *
     * @param type        the type
     * @param position      the position
     * @param hasObstacle the obstacle
     */
    public GameCell(CellType type, CellPosition position, boolean hasObstacle) {
        this(type, position);
        this.hasObstacle = hasObstacle;
    }

    /**
     * This is a copy constructor.
     *
     * @param cell the cell to copy
     */
    public GameCell(GameCell cell) {
        if (cell != null) {
            this.type = cell.getType();
            this.position = new CellPosition(cell.getPosition());
            this.playerSymbol = cell.getPlayerSymbol();
            this.hasFigure = cell.hasFigure();
            this.figure = cell.getFigure();
            this.hasObstacle = cell.hasObstacle;
        }
    }

    private CellPosition getPosition() {
        return this.position;
    }

    /**
     * This method returns a CellType from a given symbol.
     *
     * @param symbol the symbol
     * @return the cell type
     */
    public static CellType getCellTypeFromSymbol(char symbol) {
        return switch (symbol) {
            case PATHWAY_SYMBOL -> CellType.PATHWAY;
            case PATHWAY_VILLAGE_SYMBOL -> CellType.PATHWAY_VILLAGE;
            case OBSTACLE_SYMBOL -> CellType.OBSTACLE;
            case OBSTACLE_VILLAGE_SYMBOL -> CellType.OBSTACLE_VILLAGE;
            case TARGET_SYMBOL -> CellType.TARGET;
            case OCCUPIED_FOREST_SYMBOL -> CellType.OCCUPIED_FOREST;
            case EMPTY_FOREST_SYMBOL -> CellType.EMPTY_FOREST;
            case PROTECTED_ZONE_SYMBOL -> CellType.PROTECTED_ZONE;
            case EMPTY_CELL_SYMBOL -> CellType.EMPTY;
            case NON_EXISTING_CELL_SYMBOL -> CellType.NON_EXISTING_CELL;
            default -> CellType.PLAYER_STARTING_POSITION;
        };
    }

    /**
     * This method checks if a given symbol is free to move.
     *
     * @param isLastMove the last move
     * @return true if free to move, false otherwise
     */
    public boolean isFreeToMove(boolean isLastMove) {
        if (isLastMove) {
            return !isEmptySpace() && !isNonExistingCell() && !isForest();
        }

        return !isObstacle() && !isEmptySpace() && !isNonExistingCell();
    }

    /**
     * This method checks if a cell is a non-existing cell(empty cell).
     *
     * @return true if empty zone, false otherwise
     */
    private boolean isNonExistingCell() {
        return this.type == CellType.NON_EXISTING_CELL;
    }

    /**
     * This method checks if a cell is a protected zone.
     *
     * @return true if protected zone, false otherwise
     */
    public boolean isProtectedZone() {
        return this.type == CellType.PROTECTED_ZONE;
    }

    /**
     * This method checks is a cell is a target.
     *
     * @return true if targeted, false otherwise
     */
    public boolean isTarget() {
        return this.type == CellType.TARGET;
    }

    /**
     * This method checks if a cell is an empty space.
     *
     * @return true if empty space, false otherwise
     */
    private boolean isEmptySpace() {
        return this.type == CellType.EMPTY;
    }

    /**
     * This method checks if a cell is an obstacle.
     *
     * @return true if obstacle, false otherwise
     */
    private boolean isObstacle() {
        return this.type == CellType.OBSTACLE || this.type == CellType.OBSTACLE_VILLAGE || this.hasObstacle;
    }

    /**
     * This method checks if a cell is a forest.
     *
     * @return true if forested, false otherwise
     */
    public boolean isForest() {
        return this.type == CellType.EMPTY_FOREST || this.type == CellType.OCCUPIED_FOREST;
    }

    /**
     * This method checks if cell has an obstacle.
     *
     * @return true if it has obstacle, false otherwise
     */
    public boolean hasObstacle() {
        return hasObstacle;
    }

    /**
     * Getter for position.
     *
     * @return the position
     */
    public CellPosition getPos() {
        return position;
    }

    /**
     * Setter for position.
     * @param position    the position
     */
    public void setPosition(CellPosition position) {
        this.position = position;
    }

    /**
     * Setter for has obstacle.
     *
     * @param hasObstacle the obstacle
     * @param isVillage if the cell is on village
     */
    public void setHasObstacle(boolean hasObstacle, boolean isVillage) {
        this.hasObstacle = hasObstacle;
        if (!hasObstacle) {
            this.type = isVillage ? CellType.PATHWAY_VILLAGE : CellType.PATHWAY;
        }
    }

    /**
     * Getter for cell type.
     *
     * @return the cell type
     */
    public CellType getType() {
        return type;
    }

    /**
     * Setter for cell type.
     * @param cellType cell type
     */
    protected void setType(CellType cellType) {
        this.type = cellType;
    }

    /**
     * Getter for player symbol.
     *
     * @return the player symbol
     */
    public char getPlayerSymbol() {
        return playerSymbol;
    }

    /**
     * Getter for has figure.
     *
     * @return true if it has figure, false otherwise
     */
    public boolean hasFigure() {
        return hasFigure;
    }

    /**
     * Setter for has figure.
     *
     * @param hasFigure the figure
     */
    public void setHasFigure(boolean hasFigure) {
        this.hasFigure = hasFigure;
        if (!this.hasFigure) {
            this.figure = null;
        }
    }

    /**
     * Getter for figure.
     *
     * @return the figure
     */
    public Figure getFigure() {
        return figure;
    }

    /**
     * Setter for figure.
     *
     * @param figure the figure
     */
    public void setFigure(Figure figure) {
        this.figure = figure;
        this.hasFigure = this.figure != null;
    }
}
