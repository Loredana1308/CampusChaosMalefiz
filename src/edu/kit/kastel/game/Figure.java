package edu.kit.kastel.game;

/**
 * This class represents a figure.
 *
 * @author Programmieren-Team
 */
public final class Figure {
    private final char letter;
    private final int index;
    private CellPosition position;

    /**
     * This constructor creates a figure with given letter and index.
     *
     * @param letter the letter
     * @param index  the index
     */
    public Figure(char letter, int index) {
        this.letter = letter;
        this.index = index;
    }

    /**
     * This method returns string representation of the figure.
     *
     * @return the figure
     */
    @Override
    public String toString() {
        return String.valueOf(letter) + index;
    }

    /**
     * This method returns letter of the figure.
     *
     * @return the letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * This method returns index of the figure.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * This method returns position of the figure.
     *
     * @return the row position
     */
    public CellPosition getPosition() {
        return position;
    }

    /**
     * This method sets position of the figure.
     *
     * @param position    the position
     */
    public void setPosition(CellPosition position) {
        this.position = position;
    }
}
