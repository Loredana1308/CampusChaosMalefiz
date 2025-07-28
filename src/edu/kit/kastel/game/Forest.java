package edu.kit.kastel.game;

import edu.kit.kastel.enums.CellType;

import java.util.ArrayList;

/**
 * This class represents a forest cell. It is a subclass of GameCell.
 *
 * @author Programmieren-Team
 */
public final class Forest extends GameCell {
    private final ArrayList<Figure> capturedFigures;

    /**
     * This constructor creates a forest cell with given row and column.
     *
     * @param position the position
     */
    public Forest(CellPosition position) {
        super(CellType.EMPTY_FOREST, position);
        this.capturedFigures = new ArrayList<>();
    }

    /**
     * This method adds a figure to the forest cell.
     * It also changes the cell type to OCCUPIED_FOREST, if needed.
     *
     * @param f the figure
     */
    public void addFigure(Figure f) {
        capturedFigures.add(f);

        if (this.type == CellType.EMPTY_FOREST) {
            this.type = CellType.OCCUPIED_FOREST;
        }
    }

    /**
     * This method removes a figure from the forest cell.
     * It also changes the cell type to EMPTY_FOREST, if needed.
     *
     * @param f the figure
     */
    public void removeFigure(Figure f) {
        if (f != null) {
            capturedFigures.remove(f);

            if (this.capturedFigures.isEmpty()) {
                this.type = CellType.EMPTY_FOREST;
            }
        }
    }

    /**
     * This method returns the figure from captured figures with the given name.
     *
     * @param figureName the figure name
     * @return the figure
     */
    public Figure getFigureByName(String figureName) {
        for (Figure f : capturedFigures) {
            if (f.toString().equals(figureName)) {
                return f;
            }
        }

        return null;
    }
}
