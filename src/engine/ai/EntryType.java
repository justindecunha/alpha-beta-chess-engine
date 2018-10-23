package engine.ai;

/**
 * Type type of score information stored in the transposition table. Denotes if the score is an upper bound, a lower bound, or an
 * exact value for the particular node in question.
 */
public enum EntryType {
    EXACT,
    UPPER,
    LOWER;
}