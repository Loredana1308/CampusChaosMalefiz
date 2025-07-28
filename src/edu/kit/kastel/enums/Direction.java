package edu.kit.kastel.enums;

/**
 * This enum stores all available directions for move commands.
 *
 * @author Programmieren-Team
 */
public enum Direction {
    /**
     * This direction represents moving up.
     */
    UP,
    /**
     * This direction represents moving down.
     */
    DOWN,
    /**
     * This direction represents moving left.
     */
    LEFT,
    /**
     * This direction represents moving right.
     */
    RIGHT;

    /**
     * This method returns string representation of the direction.
     *
     * @return the direction
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
