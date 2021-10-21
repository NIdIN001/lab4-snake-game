package game;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private final int DefaultLength = 2;
    private final int Head = 0;
    private int score;

    private Direction direction;
    private Direction newDirection;

    private SnakeState state;

    private final int playerId;

    /* координата головы - points[0] */
    private List<Point> body;
    private final Field field;

    public Snake(List<Point> beginPos, Direction beginDir, Field field, int id) {
        score = 0;
        body = new ArrayList<>();

        this.playerId = id;
        this.field = field;
        this.direction = beginDir;
        this.newDirection = beginDir;

        this.body = beginPos;

        this.state = SnakeState.ALIVE;
    }

    public SnakeState getState() {
        return state;
    }

    public void setState(SnakeState state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public boolean isHead(Point p) {
        return p.isSame(getHead());
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean containsWithoutHead(Point p) {
        for (int i = 1; i < body.size(); i++) {
            Point point = body.get(i);

            if (point.isSame(p))
                return true;
        }
        return false;
    }

    public boolean contains(Point p) {
        for (Point point : body) {
            if (point.isSame(p))
                return true;
        }
        return false;
    }

    public void setDirection(Direction newDir) {
        this.newDirection = newDir;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Point getHead() {
        return body.get(Head);
    }

    public List<Point> getBody() {
        return body;
    }

    public void move() {
        if (!direction.isOpposite(newDirection))
            direction = newDirection;

        Point exTailPos = body.get(body.size() - 1);

        Point exBodyBos = getHead().copy();
        for (int i = 0; i < body.size(); ++i) {
            if (i != Head) {
                Point tmp = body.get(i).copy();
                body.set(i, exBodyBos.copy());
                exBodyBos = tmp.copy();
            } else {
                switch (direction) {
                    case UP -> {
                        body.get(Head).height--;
                        if (body.get(Head).height == -1)
                            body.get(Head).height = field.getHeight() - 1;
                    }
                    case DOWN -> {
                        body.get(Head).height++;
                        if (body.get(Head).height == field.getHeight())
                            body.get(Head).height = 0;
                    }
                    case LEFT -> {
                        body.get(Head).width--;
                        if (body.get(Head).width == -1)
                            body.get(Head).width = field.getWidth() - 1;
                    }
                    case RIGHT -> {
                        body.get(Head).width++;
                        if (body.get(Head).width == field.getWidth())
                            body.get(Head).width = 0;
                    }
                }
            }
        }

        if (field.isEat(body.get(Head))) {
            body.add(new Point(exTailPos));
            score++;
        }
    }
}
