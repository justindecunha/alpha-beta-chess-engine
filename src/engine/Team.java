package engine;

/**
 * An enumeration of chess teams, white or black
 */
public enum Team {
    WHITE(0),
    BLACK(1);

    private int value;

    private Team(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
