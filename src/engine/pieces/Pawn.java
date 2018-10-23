package engine.pieces;

import engine.Team;
import engine.board.Board;
import engine.board.Move;
import engine.board.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a pawn
 */
public class Pawn extends Piece {
    public Pawn(final Team team, final int position, final boolean isFirstMove) {
        super(PieceType.PAWN, team, position, isFirstMove);
    }
        private static final int[] PAWN_MOVE_OFFSETS = {7, 8, 9};

    @Override
    public List<Move> generatePossibleMoves(final Board board) {
        final List<Move> moveList = new ArrayList<>();

        final int i = this.getTeam() == Team.WHITE ? -1 : 1;

        for(final int offset : PAWN_MOVE_OFFSETS) {
            int proposedPosition = this.piecePosition + offset * i;
            if(Utility.isValidPosition(proposedPosition)) {
                if(offset == 8 && board.getSquare(proposedPosition).isEmpty()) {
                    moveList.add(new Move(this, proposedPosition, Move.moveType.PAWN));
                    proposedPosition += offset * i;
                    if(this.isFirstMove() && board.getSquare(proposedPosition).isEmpty()) {
                        moveList.add(new Move(this, proposedPosition, Move.moveType.PAWN_JUMP));
                    }
                } else if(offset == 7 || offset == 9) {

                    if(isFileEdgeCase(this.piecePosition, offset*i)) {
                        continue;
                    }

                    int enpassantOffset = offset == 7 ? 1 : -1;

                    if(this.getTeam() == Team.BLACK) enpassantOffset *= -1;

                    //EnPassant stuff
                    if(board.peekLastMove() != null && board.peekLastMove().getType() == Move.moveType.PAWN_JUMP) {
                        if(board.peekLastMove().getEndPosition() == this.piecePosition + enpassantOffset) {
                            moveList.add(new Move(this, proposedPosition, Move.moveType.ENPASSENT));
                        }
                    }

                    if(!board.getSquare(proposedPosition).isEmpty() &&
                            board.getSquare(proposedPosition).getPiece().getTeam() != this.getTeam()) {
                        moveList.add(new Move(this, proposedPosition, board.getSquare(proposedPosition).getPiece(), Move.moveType.PAWN_ATTACK));
                    }
                }
            }
        }

        return moveList;
    }

    private static boolean isFileEdgeCase(final int position, final int offset) {
        final boolean isFirstFileEdgeCase = Utility.IS_FIRST_FILE[position] && (offset == 7 || offset == -9);
        final boolean isEighthFileEdgeCase = Utility.IS_EIGHTH_FILE[position] && (offset == 9 || offset == -7);
        return isFirstFileEdgeCase || isEighthFileEdgeCase;
    }
}
