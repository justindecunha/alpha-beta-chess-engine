package engine.ai;

import engine.board.Board;
import engine.board.Move;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.HashMap;

/**
 * A class that handles threading for the negamax algorithm. The main workload of the program is placed on a separate
 * thread to maintain responsiveness of the gui.
 */
public class AIThinkTank extends Service<Move> {
    private HashMap<Long, HashEntry> transpositionTable;
    private final Board board; // The board to operate on
    private int depth; // The maximum depth the search should reach in the game tree.


    public AIThinkTank(Board board) {
        transpositionTable = new HashMap<>(2^25);
        this.board = board;
    }

    public void start(final int depth) {
        this.depth = depth;
        this.start();
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        this.reset(); // Resets the state of the service back to READY upon completion.
    }

    @Override
    protected Task<Move> createTask() {
        return new NegamaxAlphaBetaTransposition(this.board, this.depth, transpositionTable);
    }
}
