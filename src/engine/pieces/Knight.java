package engine.pieces;

import engine.board.Board;
import engine.Team;
import engine.board.Move;
import engine.board.Utility;
import engine.board.Square;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a knight
 */
public class Knight extends Piece {

    // Offsets from current position - used to generate places knight can move to
    private static final int[] KNIGHT_MOVE_OFFSETS = {6, 10, 15, 17};

    public Knight(final Team team, final int position, final boolean isFirstMove) {
        super(PieceType.KNIGHT, team, position, isFirstMove);
    }

    /**
     * Generates all the possible moves a particular knight can make.
     *
     * Each of the offsets added or subtracted from a knight's current position should result in the possible moves
     * the knight can make. Once generated, this position is checked to ensure that a) the position is on the board,
     * and b) if the position the knight wishes to move to is occupied, make sure it is occupied by an opposing piece.
     *
     * @param board the board the knight sits on
     * @return the list of possible moves
     */
    @Override
    public List<Move> generatePossibleMoves(final Board board) {

        final List<Move> moveList = new ArrayList<>();

        for(final int offset : KNIGHT_MOVE_OFFSETS) {
            for(int i = -1; i <= 1; i += 2) { // i is used to consider both incrementing and decrementing for each offset

                if(isFileEdgeCase(piecePosition, offset * i)) { // resolves file edge cases
                    continue;
                }
                final int destinationPosition = piecePosition + offset * i;
                if(Utility.isValidPosition(destinationPosition)) { // resolves rank edge cases
                    final Square proposedDestinationSquare = board.getSquare(destinationPosition);

                    if(proposedDestinationSquare.isEmpty()) {
                        moveList.add(new Move(this, destinationPosition, Move.moveType.HEAVY));
                    } else {
                        final Piece destinationPiece = proposedDestinationSquare.getPiece();
                        final Team destinationTeam = destinationPiece.getTeam();

                        if(this.team  != destinationTeam) {
                            moveList.add(new Move(this, destinationPosition, destinationPiece, Move.moveType.ATTACK));
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * The offsets work for almost all positions, but there are edge cases present when a knight sits in the first,
     * second, seventh, and eighth file (columns). This method is used to detect these edge cases.
     *
     * @param position The knight's position
     * @param offset The proposed offset
     * @return Whether the offset/position pair is an edge case
     */

    private static boolean isFileEdgeCase(final int position, final int offset) {
        final boolean isFirstFileEdgeCase = Utility.IS_FIRST_FILE[position] &&
                (offset == -17 || offset == -10 || offset == 6 || offset == 15);
        final boolean isSecondFileEdgeCase =  Utility.IS_SECOND_FILE[position] && (offset == -10 || offset == 6);
        final boolean isSeventhFileEdgeCase = Utility.IS_SEVENTH_FILE[position] && (offset == -6 || offset == 10);
        final boolean isEighthFileEdgeCase = Utility.IS_EIGHTH_FILE[position] && (offset == -15 || offset == -6 ||
                offset == 10 || offset == 17);


        return isFirstFileEdgeCase || isSecondFileEdgeCase || isSeventhFileEdgeCase || isEighthFileEdgeCase;
    }

}
