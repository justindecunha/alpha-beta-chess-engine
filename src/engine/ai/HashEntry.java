package engine.ai;

/**
 * A class that houses the information stored in each entry of the transposition table
 */
public class HashEntry {

    private final int score;
    private final int depth;
    private final EntryType type;

    public HashEntry(int score, int depth, EntryType type) {
        this.score = score;
        this.depth = depth;
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public EntryType getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }
}