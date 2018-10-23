package engine.ai;

import engine.board.Board;
import engine.board.Move;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.List;

/**
 * The negamax variation of minimax algorithm, with alpha beta pruning.
 *
 * Implemented from en.wikipedia.org/wiki/Negamax#Negamax_with_alpha_beta_pruning_and_transposition_tables
 */
class NegamaxAlphaBetaTransposition extends Task<Move> {

    private static final double MAX_WORK = 100.0;
    private double workDone = 0.0;
    HashMap<Long, HashEntry> transpositionTable;
    Board board;
    int maxDepth;

    NegamaxAlphaBetaTransposition(final Board board, final int maxDepth, HashMap<Long, HashEntry> transpositionTable) {
        this.board = board;
        this.maxDepth = maxDepth;
        this.transpositionTable = transpositionTable;
    }

    @Override
    protected Move call() {
        return negamaxRoot(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
    }

    @SuppressWarnings("Duplicates")
    // The driver method selects a particular move based on the best score
    public Move negamaxRoot(int alpha,final int beta) {

        Move bestMove = null;
        final List<Move> legalMoves = SortingHat.sort(board.getLegalMoves(board.getTurn()), board);

        for(int i = 0; i < legalMoves.size(); i++) {
            Move move = legalMoves.get(i);
            board.makeMove(move);
            final int score = -negamax(board, maxDepth - 1, -beta, -alpha);
            board.undoMove();
            workDone += MAX_WORK/legalMoves.size();
            updateProgress(workDone, MAX_WORK);
            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // The main class simply evaluates scores based on alpha-beta pruned mini-max.
    @SuppressWarnings("Duplicates")
    private int negamax(final Board board, final int depth, int alpha, int beta) {
        int alphaOrig = alpha;
        HashEntry hashEntry = transpositionTable.get(board.getZobristHash());
        if(hashEntry != null && hashEntry.getDepth() >= depth) {
            switch(hashEntry.getType()) {
                case EXACT:
                    return hashEntry.getScore();
                case LOWER:
                    alpha = Math.max(alpha, hashEntry.getScore());
                    break;
                case UPPER:
                    beta = Math.min(beta, hashEntry.getScore());
                    break;
            }

            if(alpha >= beta)
                return hashEntry.getScore();
        }

        // Experimenting with end-game evaluation
        /*if(legalMoves.isEmpty()) {
            return Evaluator.evaluateEndGame(board);
        } else */
        if (depth == 0) {
            return Evaluator.evaluate(board);
        }

        final List<Move> legalMoves = board.getLegalMoves(board.getTurn());

        int score = Integer.MIN_VALUE;
        for(int i = 0; i < legalMoves.size(); i++) {
            Move move = legalMoves.get(i);
            board.makeMove(move);
            score = -negamax(board, depth - 1, -beta, -alpha);
            board.undoMove();
            if (score >= beta) return beta;
            if (score > alpha) alpha = score;
        }

        EntryType type;
        if (score <= alphaOrig) {
            type = EntryType.UPPER;
        } else if(score >= beta) {
            type = EntryType.LOWER;
        } else {
            type = EntryType.EXACT;
        }


        transpositionTable.put(board.getZobristHash(), new HashEntry(score, depth, type));
        return alpha;
    }
}
