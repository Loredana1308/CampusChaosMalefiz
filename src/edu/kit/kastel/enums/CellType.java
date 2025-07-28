package edu.kit.kastel.enums;

import static edu.kit.kastel.game.GameCell.EMPTY_CELL_SYMBOL;
import static edu.kit.kastel.game.GameCell.EMPTY_FOREST_SYMBOL;
import static edu.kit.kastel.game.GameCell.NON_EXISTING_CELL_SYMBOL;
import static edu.kit.kastel.game.GameCell.OBSTACLE_SYMBOL;
import static edu.kit.kastel.game.GameCell.OBSTACLE_VILLAGE_SYMBOL;
import static edu.kit.kastel.game.GameCell.OCCUPIED_FOREST_SYMBOL;
import static edu.kit.kastel.game.GameCell.PATHWAY_SYMBOL;
import static edu.kit.kastel.game.GameCell.PATHWAY_VILLAGE_SYMBOL;
import static edu.kit.kastel.game.GameCell.PLAYER_STARTING_POSITION_SYMBOL;
import static edu.kit.kastel.game.GameCell.PROTECTED_ZONE_SYMBOL;
import static edu.kit.kastel.game.GameCell.TARGET_SYMBOL;

/**
 * This enum stores all cell types.
 *
 * @author Programmieren-Team
 */
public enum CellType {
    /**
     * This cell type represents a pathway.
     */
    PATHWAY(PATHWAY_SYMBOL),
    /**
     * This cell type represents a pathway within game of type village.
     */
    PATHWAY_VILLAGE(PATHWAY_VILLAGE_SYMBOL),
    /**
     * This cell type represents an obstacle.
     */
    OBSTACLE(OBSTACLE_SYMBOL),
    /**
     * This cell type represents an obstacle within game of type village.
     */
    OBSTACLE_VILLAGE(OBSTACLE_VILLAGE_SYMBOL),
    /**
     * This cell type represents a target.
     */
    TARGET(TARGET_SYMBOL),
    /**
     * This cell type represents protected zone, where a player cannot be hit.
     */
    PROTECTED_ZONE(PROTECTED_ZONE_SYMBOL),
    /**
     * This cell type represents a forest without players.
     */
    EMPTY_FOREST(EMPTY_FOREST_SYMBOL),
    /**
     * This cell type represents a forest with players.
     */
    OCCUPIED_FOREST(OCCUPIED_FOREST_SYMBOL),
    /**
     * This cell type represents an empty cell.
     */
    EMPTY(EMPTY_CELL_SYMBOL),
    /**
     * This cell type represents a player starting position.
     */
    PLAYER_STARTING_POSITION(PLAYER_STARTING_POSITION_SYMBOL),
    /**
     * This cell type represents an empty symbol.
     */
    NON_EXISTING_CELL(NON_EXISTING_CELL_SYMBOL);

    private final char symbol;

    /**
     * Constructor for cell type.
     *
     * @param symbol the symbol associated with the cell type
     */
    CellType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol associated with the cell type.
     *
     * @return the symbol associated with the cell type
     */
    public char getSymbol() {
        return symbol;
    }
}