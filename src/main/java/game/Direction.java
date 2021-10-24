package game;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public boolean isOpposite(Direction dir) {
        switch (this.name()) {
            case "UP" -> {
                if (dir.name().equals("DOWN"))
                    return true;
            }
            case "DOWN" -> {
                if (dir.name().equals("UP"))
                    return true;
            }
            case "LEFT" -> {
                if (dir.name().equals("RIGHT"))
                    return true;
            }
            case "RIGHT" -> {
                if (dir.name().equals("LEFT"))
                    return true;
            }
        }
        return false;
    }
}
