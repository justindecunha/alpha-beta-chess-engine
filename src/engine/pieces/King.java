package engine.pieces;

import engine.Team;
import engine.board.Board;
import engine.board.Move;
import engine.board.Square;
import engine.board.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a king
 */
public class King extends Piece {

    private final static int[] KING_MOVE_OFFSETS = {1, 7, 8, 9};

    public King(final Team team, final int position, final boolean isFirstMove) {
        super(PieceType.KING, team, position, isFirstMove);
    }

    @Override
    public List<Move> generatePossibleMoves(final Board board) {
        final List<Move> moveList = new ArrayList<>();

        for (final int offset : KING_MOVE_OFFSETS) {
            for (int i = -1; i <= 1; i += 2) {
                int proposedPosition = this.piecePosition;
                if (isFileEdgeCase(proposedPosition, offset*i)) {
                    continue;
                }

                proposedPosition += offset * i;

                if (Utility.isValidPosition(proposedPosition)) {
                    final Square proposedSquare = board.getSquare(proposedPosition);
                    if (proposedSquare.isEmpty()) {
                        moveList.add(new Move(this, proposedPosition, Move.moveType.HEAVY));
                    } else {
                        final Piece destinationPiece = proposedSquare.getPiece();
                        if (this.team != destinationPiece.team) {
                            moveList.add(new Move(this, proposedPosition, destinationPiece, Move.moveType.HEAVY_ATTACK));
                        }
                    }
                }
            }
        }

        return moveList;
    }

    private static boolean isFileEdgeCase(final int position, final int offset) {
        final boolean isFirstFileEdgeCase = Utility.IS_FIRST_FILE[position] && (offset == -1 || offset == 7 || offset == -9);
        final boolean isEighthFileEdgeCase = Utility.IS_EIGHTH_FILE[position] && (offset == 1 || offset == 9 || offset == -7);
        return isFirstFileEdgeCase || isEighthFileEdgeCase;
    }
}
