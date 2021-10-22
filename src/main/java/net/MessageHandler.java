package net;

import game.Config;
import game.Direction;
import game.Point;
import game.Snake;
import snakes.Snakes;
import view.AvailableGamesList;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler implements Runnable {
    private QueueMsg messages;
    private AvailableGamesList gamesList;
    private Node node;

    public MessageHandler(QueueMsg messages, AvailableGamesList gamesList, Node node) {
        this.node = node;
        this.messages = messages;
        this.gamesList = gamesList;
    }

    private Point convertPointFormat(Snakes.GameState.Coord p) {
        return new Point(p.getY(), p.getX());
    }

    private Direction convertDirectionFormat(Snakes.Direction dir) {
        return switch (dir) {
            case LEFT -> Direction.LEFT;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case RIGHT -> Direction.RIGHT;
        };
    }

    @Override
    public void run() {
        while (true) {
            if (messages.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!messages.isEmpty()) {
                Message task = messages.get().get();
                Snakes.GameMessage message = task.message;
                InetSocketAddress socketAddress = task.from;

                switch (message.getTypeCase()) {
                    case ANNOUNCEMENT -> {
                        var msg = message.getAnnouncement();
                        Config cfg = new Config();
                        cfg.setDeadFoodProb(msg.getConfig().getDeadFoodProb());
                        cfg.setFoodStatic(msg.getConfig().getFoodStatic());
                        cfg.setNodeTimeoutMs(msg.getConfig().getNodeTimeoutMs());
                        cfg.setFoodPerPlayer(msg.getConfig().getFoodStatic());
                        cfg.setPingDelayMs(msg.getConfig().getPingDelayMs());
                        cfg.setHeight(msg.getConfig().getHeight());
                        cfg.setWidth(msg.getConfig().getWidth());
                        cfg.setStateDelayMs(msg.getConfig().getStateDelayMs());

                        GameAnnounce announce = new GameAnnounce(task.from, cfg);

                        if (!gamesList.getMap().containsKey(announce.getFrom()))
                            gamesList.add(announce);
/*
                        for (GameAnnounce a : gamesList.getMap().values()) {
                            if (a.equals(announce)) {
                                return;
                            }
                        }
                        gamesList.add(announce);

 */
                    }
                    case ACK -> {

                    }
                    case JOIN -> {
                        System.out.println("get join");
                        var msg = message.getJoin();

                        Player p = new Player(msg.getName(), PlayerType.HUMAN, task.from.getHostName(), task.from.getPort(), Role.NORMAL, 0, 2);
                        node.addPlayer(p, message.getSenderId());
                        node.getField().spawnNewSnake(msg.getName(), message.getSenderId());
                    }
                    case STATE -> {
                        var stateMessage = message.getState().getState();

                        List<Snakes.GameState.Coord> food = stateMessage.getFoodsList();
                        List<Point> foodPoints = new ArrayList<>();

                        for (Snakes.GameState.Coord p : food) {
                            foodPoints.add(new Point(p.getY(), p.getX()));
                        }

                        node.getField().setEatPoints(foodPoints);

                        List<Snakes.GameState.Snake> snakes = stateMessage.getSnakesList();
                        List<Snake> snakesList = new ArrayList<>();

                        for (Snakes.GameState.Snake s : snakes) {
                            List<Point> body = s.getPointsList()
                                    .stream()
                                    .map(this::convertPointFormat)
                                    .toList();

                            snakesList.add(new Snake(body, convertDirectionFormat(s.getHeadDirection()), node.getField(), s.getPlayerId()));
                        }

                        node.getField().addSnakes(snakesList);
                    }

                    case ROLE_CHANGE -> {
                        var roleMessage = message.getRoleChange();

                    }
                    case PING -> {
                        // System.err.println("PING");
                    }
                    case STEER -> {
                        System.out.println("get steer");
                        var msg = message.getSteer();
                        int id = message.getSenderId();
                        System.out.println("get steer from id:" + id);
                        var snakes = node.getField().getAliveSnakes();
                        System.out.println("ensable snakes: ");
                        for (var s : snakes) {
                            System.out.println(s.getPlayerId());
                        }
                        node.getField().setDirectionToSnake(id, switch (msg.getDirection()) {
                            case UP -> Direction.UP;
                            case DOWN -> Direction.DOWN;
                            case LEFT -> Direction.LEFT;
                            case RIGHT -> Direction.RIGHT;
                        });
                    }


                }
            }
        }
    }
}
