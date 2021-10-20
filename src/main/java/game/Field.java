package game;

import net.Node;
import net.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Field {
    private Config config;

    private HashMap<String, Integer> nameId = new HashMap<>();
    private ArrayList<Snake> aliveSnakes;
    private Snake mySnake;
    private int nextId = 0;

    private ArrayList<Point> eatPoints;

    private int width;
    private int height;

    protected Node thisNode;

    public Field(Config config, Node node) {
        thisNode = node;
        this.config = config;

        aliveSnakes = new ArrayList<>();
        eatPoints = new ArrayList<>();

        this.width = config.getWidth();
        this.height = config.getHeight();

        spawnNewEat();
    }

    public Snake findSnakeById(int id) {
        return aliveSnakes.stream()
                .filter(snake -> snake.getPlayerId() == id)
                .findFirst().orElseThrow();
    }


    public ArrayList<Snake> getAliveSnakes() {
        return aliveSnakes;
    }

    public ArrayList<Point> getEatPoints() {
        return eatPoints;
    }

    public boolean isEat(Point point) {
        for (Point p : eatPoints) {
            if (p.isSame(point)) {
                return true;
            }
        }
        return false;
    }

    public Snake getMySnake() {
        return mySnake;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void spawnNewSnake(String name, int id) {
        nameId.put(name, nextId);
        aliveSnakes.add(new Snake(findPosForNewSnake(), Direction.RIGHT, this, id));
    }

    public void spawnMySnake(int id) {
        mySnake = new Snake(findPosForNewSnake(), Direction.RIGHT, this, id);
        aliveSnakes.add(mySnake);
    }

    public void setDirectionToMySnake(Direction dir) {
        mySnake.setDirection(dir);
    }

    public void setDirectionToSnake(int snakeId, Direction dir) {
        var snake = aliveSnakes.stream()
                .filter(s -> s.getPlayerId() == snakeId)
                .findFirst();

        snake.ifPresent(s -> s.setDirection(dir));
    }

    public void spawnNewEat() {
        Random r = new Random();

        boolean isOk;
        Point eat;
        do {
            isOk = true;
            eat = new Point(r.nextInt(height - 1), r.nextInt(width - 1));

            for (Snake snake : aliveSnakes) {
                if (snake.contains(eat)) {
                    isOk = false;
                    break;
                }
            }

            for (Point p : eatPoints) {
                if (p.isSame(eat)) {
                    isOk = false;
                    break;
                }
            }
        } while (!isOk);

        eatPoints.add(eat);
    }

    private void checkEat() {
        for (Snake snake : aliveSnakes) {
            boolean isEat = eatPoints.removeIf(eat -> snake.getHead().isSame(eat));
            if (isEat)
                spawnNewEat();
        }
    }

    private void checkSnakes() {
        ArrayList<Point> heads = new ArrayList<>();
        ArrayList<Snake> deathSnakes = new ArrayList<>();

        for (Snake s : aliveSnakes) {
            heads.add(s.getHead());
        }

        for (Point head : heads)
            for (Snake snake : aliveSnakes)
                if (snake.containsWithoutHead(head)) {

                    System.out.println("KILL");
                    for (Snake s : aliveSnakes) {
                        if (s.getHead().isSame(head)) {
                            s.setState(SnakeState.DEATH);
                            deathSnakes.add(s);
                        }
                    }
                }

        for (Snake deathSnake : deathSnakes)
            aliveSnakes.removeIf(snake -> snake.equals(deathSnake));

        deathSnakes.clear();
    }

    private Point[] findPosForNewSnake() {
        Point[] points = new Point[2];
        points[0] = new Point(5, 5);
        points[1] = new Point(5, 4);
        return points;
    }

    public void makeTurn() {
        for (Snake s : aliveSnakes)
            s.move();

        if (thisNode.getRole() == Role.MASTER)
            thisNode.sendChanges();

        checkEat();
        checkSnakes();
    }

    public Config getConfig() {
        return config;
    }
}
