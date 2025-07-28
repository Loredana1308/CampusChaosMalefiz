package edu.kit.kastel.game;

import java.util.Objects;

/**
 * This class represents cell position.
 *
 * @author Programmieren-Team
 */
public final class CellPosition {
    private static final int INVALID_POSITION_VALUE = -1;

    private final int row;
    private final int column;

    /**
     * Cell position constructor.
     * @param row row index
     * @param column column index
     */
    public CellPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Cell position copy constructor.
     * @param position position to copy
     */
    public CellPosition(CellPosition position) {
        this.row = position.getRow();
        this.column = position.getColumn();
    }

    /**
     * Method that checks if a position has valid row and column indexes.
     * @return true if valid
     */
    public boolean isValidPosition() {
        return row != INVALID_POSITION_VALUE && column != INVALID_POSITION_VALUE;
    }

    /**
     * Equals method override.
     * @param pos object to compare with
     * @return true if are equal
     */
    @Override
    public boolean equals(Object pos) {
        if (pos instanceof CellPosition anotherPos) {
            return this.row == anotherPos.getRow() && this.column == anotherPos.getColumn();
        }

        return false;
    }

    /**
     * Hashcode override.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Getter for row index.
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for column index.
     * @return column
     */
    public int getColumn() {
        return column;
    }
}
