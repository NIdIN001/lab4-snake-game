package game;

import net.Node;

public class GameState {
    private Config cfg;
    private Field field;

    private boolean isPlaying;
    private Node thisNode;

    public GameState() {
        cfg = new Config();
        isPlaying = false;

        thisNode = new Node(cfg);
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

}
