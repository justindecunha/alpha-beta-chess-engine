package engine.board;

import engine.Team;
import engine.pieces.Piece;

/**
 * This class contains various utility methods and functions relating to board calculations
 */
public class Utility {
    public static final int WHITE_KING_START_POSITION = 60;
    public static final int BLACK_KING_START_POSITION = 4;
    public static final int BOARD_SQUARE_COUNT = 64; // Total number of squares on the board
    public static final int RANK_SQUARE_COUNT = 8; // Number of squares in a row/column

    // Arrays with first specified files set to true. Used to efficiently detect if a piece is in a particular file.
    public static final boolean[] IS_FIRST_FILE = initializeColumn(0);
    public static final boolean[] IS_SECOND_FILE = initializeColumn(1);
    public static final boolean[] IS_SEVENTH_FILE = initializeColumn(6);
    public static final boolean[] IS_EIGHTH_FILE = initializeColumn(7);

    /**
     * Initializes all the cells in column i to true
     * @param i the specified column
     * @return an array with the initialized column
     */
    private static boolean[] initializeColumn(int i) {
        final boolean[] boardMatrix = new boolean[BOARD_SQUARE_COUNT];

        for(; i < BOARD_SQUARE_COUNT; i+=RANK_SQUARE_COUNT)
            boardMatrix[i] = true;

        return boardMatrix;
    }

    private Utility() {
        throw new RuntimeException("You cannot instantiate this class");
    }

    /**
     * Checks if position is on the board
     *
     * @param position The position to check
     * @return Boolean value indicating if proposed position is on the board
     */
    public static boolean isValidPosition(final int position) {
        return position >= 0 && position <= Utility.BOARD_SQUARE_COUNT-1;
    }

    /**
     * Checks if the piece is on the opposite home row, used for determining pawn promotions
     * @param piece The piece to check
     * @return If its on the row or not
     */
    public static boolean isBackRank(final Piece piece) {
        final int rank = piece.getPiecePosition() / Utility.RANK_SQUARE_COUNT;
        return (rank == 0 && piece.getTeam() == Team.WHITE) || (rank == 7 && piece.getTeam() == Team.BLACK);
    }
}
