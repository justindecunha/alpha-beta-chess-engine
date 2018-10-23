package engine.pieces;

import engine.board.Board;
import engine.Team;
import engine.board.Move;

import java.util.List;

/**
 * Abstract class denoting the concept of a piece on a chessboard.
 */
public abstract class Piece {

    private final PieceType peiceType; // The type of piece
    int piecePosition;   // The position of the piece on the board
    final Team team;      // The team the piece belongs to
    private boolean isFirstMove; // If the piece has moved yet or not

    Piece(final PieceType pieceType, final Team team, final int position, final boolean isFirstMove) {
        this.peiceType = pieceType;
        this.team = team;
        this.piecePosition = position;
        this.isFirstMove = isFirstMove;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    @Override
    public String toString() {
        return this.peiceType.toString();
    }

    /**
     * Generates a list of possible moves the piece can make
     * @param board The board the piece currently sits on
     * @return The list of possible moves
     */
    public abstract List<Move> generatePossibleMoves(final Board board);

    public Piece copyOf() {
        final Team team = (this.team == Team.WHITE) ? Team.WHITE : Team.BLACK;
        final int position = this.piecePosition;
        final boolean isFirstMove = this.isFirstMove();

        switch(this.getPieceType()) {
            case PAWN:
                return new Pawn(team, position, isFirstMove);
            case KNIGHT:
                return new Knight(team, position, isFirstMove);
            case BISHOP:
                return new Bishop(team, position, isFirstMove);
            case ROOK:
                return new Rook(team, position, isFirstMove);
            case QUEEN:
                return new Queen(team, position, isFirstMove);
            default:
                return new King(team, position, isFirstMove);
        }
    }

    public void setIsFirstMove(final boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
    }
    public PieceType getPieceType() {
        return peiceType;
    }

    public void setPiecePosition(final int piecePosition) {
        this.piecePosition = piecePosition;
    }

    public enum PieceType {
        PAWN(0),
        ROOK(1),
        KNIGHT(2),
        BISHOP(3),
        KING(4),
        QUEEN(5);

        private final int value;

        PieceType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            switch(this) {
                case PAWN:
                    return "P";
                case KING:
                    return "A";
                case ROOK:
                    return "R";
                case QUEEN:
                    return "Q";
                case BISHOP:
                    return "B";
                case KNIGHT:
                    return "K";
                default:
                    return "?";
            }
        }
    }

}
