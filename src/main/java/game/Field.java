package game;

import net.Node;
import net.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Field {
    private Config config;

    private List<Snake> aliveSnakes;

    private List<Point> eatPoints;
    private int maxEatOnMap;

    private final int width;
    private final int height;
    private final Random random = new Random();

    private Node thisNode;

    public Field(Config config, Node node) {
        thisNode = node;
        this.config = config;

        aliveSnakes = new ArrayList<>();
        eatPoints = new ArrayList<>();

        this.width = config.getWidth();
        this.height = config.getHeight();
        this.maxEatOnMap = (config.getFoodStatic() + config.getFoodPerPlayer() * (aliveSnakes.size()));
    }

    public Snake findSnakeById(int id) {
        return aliveSnakes.stream()
                .filter(snake -> snake.getPlayerId() == id)
                .findFirst().orElse(null);
    }


    public List<Snake> getAliveSnakes() {
        return aliveSnakes;
    }

    public List<Point> getEatPoints() {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void spawnNewSnake(String name, int id) {
        aliveSnakes.add(new Snake(findPosForNewSnake(), Direction.RIGHT, this, id));
    }

    public void setDirectionToSnake(int snakeId, Direction dir) {
        var snake = aliveSnakes.stream()
                .filter(s -> s.getPlayerId() == snakeId)
                .findFirst();

        snake.ifPresent(s -> s.setDirection(dir));
    }

    private void spawnEatAt(int x, int y) {
        eatPoints.add(new Point(y, x));
    }

    public void spawnNewEat() {
        if (eatPoints.size() >= maxEatOnMap)
            return;

        Random r = new Random();

        boolean isOk;
        Point eat;
        do {
            isOk = true;
            eat = new Point(r.nextInt(height), r.nextInt(width));

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
                            s.setState(SnakeState.ZOMBIE);
                            deathSnakes.add(s);
                        }
                    }
                }

        for (Snake deathSnake : deathSnakes)
            aliveSnakes.removeIf(snake -> snake.equals(deathSnake));

        for (Snake s : deathSnakes) {
            for (Point p : s.getBody()) {
                if (random.nextInt(100) < (config.getDeadFoodProb() * 100)) {
                    spawnEatAt(p.width, p.height);
                }
            }
        }
        deathSnakes.clear();
    }

    private ArrayList<Point> findPosForNewSnake() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(5, 5));
        points.add(new Point(5, 4));
        return points;
    }

    public void makeTurn() {
        for (Snake s : aliveSnakes)
            s.move();

        checkEat();
        checkSnakes();

        if (thisNode.getRole() == Role.MASTER)
            thisNode.sendChanges();
    }

    public void setEatPoints(List<Point> food) {
        eatPoints = food;
    }

    public Config getConfig() {
        return config;
    }

    public void addSnakes(List<Snake> snakes) {
        maxEatOnMap = (config.getFoodStatic() + config.getFoodPerPlayer() * (snakes.size()));
        aliveSnakes = snakes;
    }
}
