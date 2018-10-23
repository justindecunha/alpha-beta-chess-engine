package engine.ai;

import engine.board.Board;
import engine.board.Move;
import java.util.List;

/**
 * Does a direct evaluation of each board in the list, and orders them based on this evaluation.
 */
public class SortingHat {
    public static List<Move> sort(List<Move> moves, Board board) {
        moves.sort((moveOne, moveTwo) -> {
            int moveOneScore, moveTwoScore;
            board.makeMove(moveOne);
            moveOneScore = Evaluator.evaluate(board);
            board.undoMove();

            board.makeMove(moveTwo);
            moveTwoScore = Evaluator.evaluate(board);
            board.undoMove();

            return moveOneScore - moveTwoScore;
        });

        return moves;
    }
}
