package engine.board;

import engine.Team;
import engine.pieces.Piece;

/**
 * Class representing a square on the board.
 */
public class Square {

    private Piece pieceOnSquare;

    public Square(final Piece pieceOnSquare) {
        this.pieceOnSquare = pieceOnSquare;
    }

    public boolean isEmpty() {
        return pieceOnSquare == null;
    }

    public Piece getPiece() {
        return pieceOnSquare;
    }

    @Override
    public String toString() {
        if(this.isEmpty()) {
            return "-";
        } else {
            return this.pieceOnSquare.getTeam() == Team.WHITE ? this.pieceOnSquare.toString() :
                    this.pieceOnSquare.toString().toLowerCase();
        }
    }

    public void setPiece(final Piece piece) {
        this.pieceOnSquare = piece;
    }
}
