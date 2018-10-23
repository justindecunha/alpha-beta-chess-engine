package engine.board;

import engine.Team;
import engine.pieces.Piece;
import java.util.Random;

/**
 * This class assists in creating zobrist hashes for a particular board state
 */
public final class Zobrist {

    // These variables represent randomly generated longs used to represent each particular facet of a board state
    private static long blackToMove;
    private static final long[] castles = new long[4];
    private static final long[] enpassent = new long[8];
    private static final long[][][] pieceVals = new long[2][6][64];

    private static final Zobrist INSTANCE = new Zobrist();


    private Zobrist() {
        Random random = new Random();

        blackToMove = random.nextLong();

        for(int i = 0; i < 4; i++) {
            castles[i] = random.nextLong();
        }

        for(int i = 0; i < enpassent.length; i++) {
            enpassent[i] = random.nextLong();
        }

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 6; j++) {
                for (int k = 0; k < 64; k++) {
                    pieceVals[i][j][k] = random.nextLong();
                }
            }
        }
    }

    /**
     * Generates a zobrist hash for the given board state. Ideally should only be used for the initial board state -
     * future hashes should be derived from the initially generated hash to save computation time.
     *
     * @param board The given board state
     * @return The calculated hash
     */
    long getFullHash(final Board board) {
        long hash = 0L;

        // Turn
        if(board.getTurn() == Team.BLACK)
            hash ^= blackToMove;


        // Pieces
        for(int i = 0; i < 64; i++) {
            Square square = board.getSquare(i);
            if(!square.isEmpty()) {
                Piece piece = square.getPiece();
                Team team = piece.getTeam();
                Piece.PieceType type = piece.getPieceType();
                hash ^= pieceVals[team.getValue()][type.getValue()][i];
            }
        }

        // Castles
        if(board.canShortCastle(Team.WHITE)) {
            hash ^= castles[0];
        }
        if(board.canShortCastle(Team.BLACK)) {
            hash ^= castles[1];
        }
        if(board.canLongCastle(Team.WHITE)) {
            hash ^= castles[2];
        }
        if(board.canLongCastle(Team.BLACK)) {
            hash ^= castles[3];
        }

        // Enpassents
        // If the last move was a pawn jump, check both the left and right sides of said pawn to see if an enemy pawn exists
        // if yes, then the jumping pawn's column is an enpassent column, which hash is XORed with the cumulative hash.
        if(board.peekLastMove() != null && board.peekLastMove().getType() == Move.moveType.PAWN_JUMP) {
            Square leftSquare = board.getSquare(board.peekLastMove().getEndPosition() + 1);
            if(!leftSquare.isEmpty()) {
                Piece leftPiece = leftSquare.getPiece();
                if(leftPiece.getTeam() == board.getTurn() && leftPiece.getPieceType() == Piece.PieceType.PAWN) {
                    hash ^= enpassent[board.peekLastMove().getEndPosition() % 8];
                    return hash; // ensure's we don't XOR enpassent twice
                }
            }

            Square rightSquare = board.getSquare(board.peekLastMove().getEndPosition() - 1);
            if(!rightSquare.isEmpty()) {
                Piece rightPiece = rightSquare.getPiece();
                if(rightPiece.getTeam() == board.getTurn() && rightPiece.getPieceType() == Piece.PieceType.PAWN) {
                    hash ^= enpassent[board.peekLastMove().getEndPosition() % 8];
                }
            }
        }
        return hash;
    }

    public static Zobrist getInstance() {
        return INSTANCE;
    }

    /**
     * Updates the supplied hashCode with the appropriate values for the specified move
     * @param hashCode The hashcode supplied
     * @param move The move to update the hash with
     * @return The updated hash
     */
    public static long updateHash(long hashCode, final Move move, Board board) {
        int startPos = move.getStartPosition();
        int endPos = move.getEndPosition();

        Piece piece = move.getPiece();
        Piece.PieceType type = piece.getPieceType();
        Team team = piece.getTeam();

        if(board.canShortCastle(Team.WHITE)) {
            hashCode ^= castles[0];
        }
        if(board.canShortCastle(Team.BLACK)) {
            hashCode ^= castles[1];
        }
        if(board.canLongCastle(Team.WHITE)) {
            hashCode ^= castles[2];
        }
        if(board.canLongCastle(Team.BLACK)) {
            hashCode ^= castles[3];
        }


        // Enpassents
        // If the last move was a pawn jump, check both the left and right sides of said pawn to see if an enemy pawn exists
        // if yes, then the jumping pawn's column is an enpassent column, which hash is XORed with the cumulative hash.
        if(board.peekLastMove() != null && board.peekLastMove().getType() == Move.moveType.PAWN_JUMP) {
            Square leftSquare = board.getSquare(board.peekLastMove().getEndPosition() + 1);
            if(!leftSquare.isEmpty()) {
                Piece leftPiece = leftSquare.getPiece();
                if(leftPiece.getTeam() == board.getTurn() && leftPiece.getPieceType() == Piece.PieceType.PAWN) {
                    hashCode ^= enpassent[board.peekLastMove().getEndPosition() % 8];
                    return hashCode; // ensure's we don't XOR enpassent twice
                }
            }

            Square rightSquare = board.getSquare(board.peekLastMove().getEndPosition() - 1);
            if(!rightSquare.isEmpty()) {
                Piece rightPiece = rightSquare.getPiece();
                if(rightPiece.getTeam() == board.getTurn() && rightPiece.getPieceType() == Piece.PieceType.PAWN) {
                    hashCode ^= enpassent[board.peekLastMove().getEndPosition() % 8];
                }
            }
        }
        hashCode ^= pieceVals[team.getValue()][type.getValue()][startPos];
        hashCode ^= pieceVals[team.getValue()][type.getValue()][endPos];

        return hashCode;
    }
}