package edu.kit.kastel.utils;

import edu.kit.kastel.enums.CellType;
import edu.kit.kastel.game.CellPosition;
import edu.kit.kastel.game.Figure;
import edu.kit.kastel.game.GameCell;
import edu.kit.kastel.game.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static edu.kit.kastel.game.GameCell.EMPTY_CELL_SYMBOL;
import static edu.kit.kastel.game.GameCell.NON_EXISTING_CELL_SYMBOL;

/**
 * This class stores utility methods for the game.
 *
 * @author Programmieren-Team
 */
public final class Utility {
    /**
     * Invalid integer value.
     */
    public static final int INVALID_INTEGER_VALUE = -1;
    private static final String INVALID_FIELD_ERROR_MESSAGE = "the given game field is not valid. "
            + "Please add new file path.";
    private static final String INVALID_FOREST_VILLAGE_ERROR_MESSAGE = "invalid forest or village.";
    private static final String INVALID_FIELD_CHARACTER_ERROR_MESSAGE = "invalid character found.";
    private static final String CANNOT_VISIT_SAME_FIELD_ERROR_MESSAGE = "it is forbidden to move a figure within one "
            + "move over the same field multiple times.";
    private static final String CANNOT_MOVE_FIGURE_ERROR_MESSAGE = "the figure cannot be moves across the given path.";
    private static final String OUT_OF_BOUNDS_ERROR_MESSAGE = "figure cannot go out of bounds in the matrix.";
    private static final char FIRST_ALPHABET_FIGURE_NAME_CHARACTER = 'A';
    private static final String PLAYER_NAME_SEPARATOR_SYMBOL = ",";
    private static final String EMPTY_STRING_SYMBOL = "";
    private static final int START_ITERATION_INDEX = 0;
    private static final int INTEGER_DEFAULT_INITIAL_VALUE = 0;
    private static final String POSITIVE_INTEGER_REGEX = "\\d+";
    private static final int MIN_ROW_COL_INDEX = 0;
    private static final String ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+";
    private static final ArrayList<Character> INVALID_FIELD_CHARACTERS =
            new ArrayList<>(Arrays.asList(Character.toLowerCase(CellType.PROTECTED_ZONE.getSymbol()),
                    Character.toLowerCase(CellType.TARGET.getSymbol())));
    private static final int RANDOM_NEXT_INT_MAX_VALUE = 6;
    private static final int RANDOM_NEXT_INT_INCREMENT_VALUE = 1;

    /**
     * This constructor is private to prevent instantiation of this class.
     */
    private Utility() {
    }

    /**
     * This method converts a list of strings to a matrix.
     * It fills the empty spaces with spaces.
     * It also converts the characters to cell types.
     * It converts the player starting position to a cell with a player symbol.
     *
     * @param rows the list of strings
     * @return the matrix
     */
    public static GameCell[][] convertFromListToMatrix(List<String> rows) {
        if (rows == null || rows.isEmpty()) {
            InputOutputHandler.showError(INVALID_FIELD_ERROR_MESSAGE);
            return null;
        }

        int numRows = rows.size();
        int numCols = getLongestRow(rows);
        boolean hasForest = false;
        boolean hasVillage = false;
        boolean hasInvalidCharacters = false;

        GameCell[][] matrix = new GameCell[numRows][numCols];
        for (int i = START_ITERATION_INDEX; i < numRows; i++) {
            String row = rows.get(i);
            String currentRow = fillRowsWithSpaces(row, numCols - row.length());
            for (int j = START_ITERATION_INDEX; j < numCols; j++) {

                char currentChar = currentRow.charAt(j);

                CellType cellType = GameCell.getCellTypeFromSymbol(currentChar);
                CellPosition position = new CellPosition(i, j);

                matrix[i][j] = getGameCell(cellType, position, currentChar);

                if (cellType == CellType.EMPTY_FOREST) {
                    hasForest = true;
                } else if (cellType == CellType.OBSTACLE_VILLAGE || cellType == CellType.PATHWAY_VILLAGE) {
                    hasVillage = true;
                } else if (INVALID_FIELD_CHARACTERS.contains(currentChar)) {
                    hasInvalidCharacters = true;
                }
            }
        }

        if (isValidMatrix(hasVillage, hasForest, hasInvalidCharacters, matrix)) {
            return null;
        }

        return matrix;
    }

    private static boolean isValidMatrix(boolean hasVillage, boolean hasForest, boolean hasInvalidCharacters,
                                         GameCell[][] matrix) {

        if (hasVillage != hasForest || hasInvalidCharacters) {
            InputOutputHandler.showStartCommandError(
                    hasInvalidCharacters
                            ? INVALID_FIELD_CHARACTER_ERROR_MESSAGE
                            : INVALID_FOREST_VILLAGE_ERROR_MESSAGE,
                    matrix, EMPTY_CELL_SYMBOL);
            return true;
        }
        return false;
    }

    private static GameCell getGameCell(CellType cellType, CellPosition position, char currentChar) {
        return switch (cellType) {
            case PLAYER_STARTING_POSITION -> new GameCell(cellType, position, currentChar);

            case OBSTACLE, OBSTACLE_VILLAGE -> new GameCell(cellType, position, true);
            default -> new GameCell(cellType, position);
        };
    }

    /**
     * This method checks if a row and column position is already visited.
     * It returns true if the position is already visited.
     *
     * @param visited the list of pairs
     * @param position  the position
     * @return true if the position is already visited
     */
    private static boolean alreadyVisited(List<CellPosition> visited, CellPosition position) {
        for (CellPosition visitedPos : visited) {
            if (position.equals(visitedPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the letter from a number.
     * It skips reserved characters.
     * It returns the letter at the given index.
     *
     * @param index              the number
     * @param reservedCharacters the reserved characters
     * @return the letter
     */
    public static char getLetterFromNumber(int index, char[] reservedCharacters) {
        int counter = INTEGER_DEFAULT_INITIAL_VALUE;

        for (int i = START_ITERATION_INDEX; ; i++) {
            char currentChar = (char) ((int) FIRST_ALPHABET_FIGURE_NAME_CHARACTER + i);

            // Check if the current character is reserved
            boolean isReserved = false;
            for (char reserved : reservedCharacters) {
                if (currentChar == reserved) {
                    isReserved = true;
                    break;
                }
            }

            // If it's reserved, skip it
            if (isReserved) {
                continue;
            }

            if (counter == index) {
                return currentChar;
            }

            counter++;
        }
    }

    /**
     * This method fills rows with spaces.
     * It adds spaces at the end of the row.
     *
     * @param row    the row
     * @param length the length
     * @return the row with spaces
     */
    private static String fillRowsWithSpaces(String row, int length) {
        String spaces = String.valueOf(NON_EXISTING_CELL_SYMBOL).repeat(length);
        return row + spaces;
    }

    /**
     * This method parses an integer parameter.
     * It returns -1 if the parameter is not a positive integer.
     *
     * @param param the parameter to be parsed
     * @return the parsed integer
     */
    public static int parseIntegerParam(String param) {
        if (param != null && Utility.checkIfPositiveInteger(param)) {
            return Integer.parseInt(param);
        }
        return INVALID_INTEGER_VALUE;
    }

    /**
     * This method returns a random dice number. This method is used when the seed is set.
     * @param random random instance
     * @return the players
     */
    public static int getRandomRollDiceNumber(Random random) {
        return random.nextInt(RANDOM_NEXT_INT_MAX_VALUE) + RANDOM_NEXT_INT_INCREMENT_VALUE;
    }

    /**
     * This method checks if a position is out of bounds.
     * It returns true if the position is out of bounds.
     *
     * @param position          the position
     * @param rowLength    the row length
     * @param columnLength the column length
     * @return true if the position is out of bounds
     */
    public static boolean isOutOfBounds(CellPosition position, int rowLength, int columnLength) {
        return position.getRow() < MIN_ROW_COL_INDEX
                || position.getColumn() < MIN_ROW_COL_INDEX
                || position.getRow() >= rowLength
                || position.getColumn() >= columnLength;
    }

    /**
     * This method checks if a string is a positive integer.
     *
     * @param number the string
     * @return true if the string is an integer
     */
    private static boolean checkIfPositiveInteger(String number) {
        return number.matches(POSITIVE_INTEGER_REGEX);
    }

    /**
     * This method checks if a string contains only letters and digits.
     *
     * @param s string to check
     * @return true if is alphanumeric
     */
    public static boolean isAlphanumeric(String s) {
        return s.matches(ALPHANUMERIC_REGEX);
    }

    /**
     * This method gets the longest row from a given list.
     *
     * @param rows the list of strings
     * @return the longest row
     */
    private static int getLongestRow(List<String> rows) {
        String longest = EMPTY_STRING_SYMBOL;

        for (String row : rows) {
            if (row.length() > longest.length()) {
                longest = row;
            }
        }

        addEmptySpacesAtTheEnd(longest);

        return longest.length();
    }

    /**
     * This method checks if a list is empty.
     *
     * @param rows the list
     * @return true if the list is empty
     */
    public static boolean isListEmpty(List<?> rows) {
        return rows == null || rows.isEmpty();
    }

    /**
     * This method merges letters with a comma.
     *
     * @param array the array
     * @return the merged letters
     */
    public static String mergeLettersWithComma(List<Player> array) {
        String result = EMPTY_STRING_SYMBOL;

        for (int i = START_ITERATION_INDEX; i < array.size(); i++) {
            result += array.get(i).getName();

            // Add a space after each character except the last one
            if (i < array.size() - 1) {
                result += PLAYER_NAME_SEPARATOR_SYMBOL;
            }
        }

        return result;
    }

    /**
     * This method adds empty spaces at the end.
     *
     * @param row the row
     */
    private static void addEmptySpacesAtTheEnd(String row) {
        int emptySpaces = INTEGER_DEFAULT_INITIAL_VALUE;
        int index = INTEGER_DEFAULT_INITIAL_VALUE;
        while (index < row.length() && row.charAt(index) == EMPTY_CELL_SYMBOL) {
            emptySpaces++;
            index++;
        }

        fillRowsWithSpaces(row, emptySpaces);
    }

    /**
     * This method returns the player by figure.
     *
     * @param f the figure
     * @param players list of players
     * @return the player
     */
    public static Player findPlayerByFigure(Figure f, List<Player> players) {
        for (Player player : players) {
            if (player.getName() == f.getLetter()) {
                return player;
            }
        }

        return null;
    }

    /**
     * This method checks if a figure position is valid.
     *
     * @param visited  the figure
     * @param pos position
     * @param isLastMove last move
     * @param gameMatrix game matrix
     * @return true if the figure is in the list
     */
    public static boolean checkNewFigurePosition(List<CellPosition> visited, CellPosition pos, boolean isLastMove,
                                                 GameCell[][] gameMatrix) {
        if (Utility.isOutOfBounds(pos, gameMatrix.length, gameMatrix[0].length)) {
            InputOutputHandler.showError(OUT_OF_BOUNDS_ERROR_MESSAGE);
            return false;
        }

        if (!gameMatrix[pos.getRow()][pos.getColumn()].isFreeToMove(isLastMove)) {
            InputOutputHandler.showError(CANNOT_MOVE_FIGURE_ERROR_MESSAGE);
            return false;
        }

        if (Utility.alreadyVisited(visited, pos)) {
            InputOutputHandler.showError(CANNOT_VISIT_SAME_FIELD_ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * This method returns a copy of the matrix.
     *
     * @param matrixToCopy the matrix to copy
     * @return the copy of the matrix
     */
    public static GameCell[][] getMatrixCopy(GameCell[][] matrixToCopy) {
        GameCell[][] copy = new GameCell[matrixToCopy.length][matrixToCopy[START_ITERATION_INDEX].length];

        for (int i = START_ITERATION_INDEX; i < matrixToCopy.length; i++) {
            for (int j = START_ITERATION_INDEX; j < matrixToCopy[START_ITERATION_INDEX].length; j++) {
                CellPosition position = new CellPosition(i, j);
                copy[i][j] = getGameCellCopy(j, position, matrixToCopy[i]);
            }
        }
        return copy;
    }

    private static GameCell getGameCellCopy(int j, CellPosition position, GameCell[] matrixToCopy) {
        return new GameCell(matrixToCopy[j].getType(), position,
                matrixToCopy[j].getPlayerSymbol(), matrixToCopy[j].hasFigure(),
                matrixToCopy[j].getFigure(), matrixToCopy[j].hasObstacle());
    }
}
