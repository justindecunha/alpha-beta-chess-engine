package engine.board;

import engine.pieces.Piece;

/**
 * A class representing an individual move on a chessboard.
 */
public class Move {
    private final Piece piece; // The piece we are moving
    private final Piece attackedPiece; // The attacked piece, null if not an attacking move
    private final int startPosition; // The pieces start position (0 - 63)
    private final int endPosition; // The pieces end position (0 - 63)
    private final moveType moveType; // The type of move
    private final boolean firstMove; // If this is the first move said piece has made
    private final boolean attackingMove; // If this move is considered an attacking move


    /**
     * Constructor for a basic move.
     * @param piece The moving piece.
     * @param endPosition The destination position of the piece.
     * @param moveType The type of move this is.
     */
    public Move(final Piece piece, final int endPosition, final moveType moveType) {
        this.piece = piece;
        this.startPosition = piece.getPiecePosition();
        this.endPosition = endPosition;
        this.moveType = moveType;
        this.attackedPiece = null;
        this.attackingMove = false;
        firstMove = piece.isFirstMove();
    }

    /**
     * Constructor for an attacking move.
     * @param piece The moving piece.
     * @param endPosition The destination position of the piece.
     * @param attackedPiece A clone of the piece being attacked.
     * @param moveType The type of attack move this is.
     */
    public Move(final Piece piece, final int endPosition, final Piece attackedPiece, final moveType moveType) {
        this.piece = piece;
        this.endPosition = endPosition;
        this.startPosition = piece.getPiecePosition();
        this.moveType = moveType;
        this.attackedPiece = attackedPiece.copyOf();
        this.attackingMove = true;
        firstMove = piece.isFirstMove();
    }

    public int getEndPosition() {
        return endPosition;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getAttackedPiece() {
        return attackedPiece == null ? null : attackedPiece.copyOf();
    }

    public moveType getType() {
        return moveType;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public boolean isAttackingMove() {
        return attackingMove;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public enum moveType {
        HEAVY,
        ATTACK,
        HEAVY_ATTACK,
        PAWN,
        PAWN_JUMP,
        PAWN_ATTACK,
        ENPASSENT,
        SHORT_CASTLE,
        LONG_CASTLE
    }

    @Override
    public String toString() {
        final char x = (char) ((this.startPosition % 8) + 97);
        final int y = 8 - ((this.startPosition / 8));

        final char x2 = (char) ((this.endPosition % 8) + 97);
        final int y2 = 8 - ((this.endPosition / 8));
        return piece + "" + x + Integer.toString(y) + x2 + Integer.toString(y2);
    }
}
