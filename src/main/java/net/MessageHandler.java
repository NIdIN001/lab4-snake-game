package net;

import game.Config;
import game.Direction;
import game.Point;
import game.Snake;
import snakes.Snakes;
import view.AvailableGamesList;

import java.util.ArrayList;
import java.util.Calendar;
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

    private Role convertRoleFormat(Snakes.NodeRole role) {
        return switch (role) {
            case DEPUTY -> Role.DEPUTY;
            case MASTER -> Role.MASTER;
            case NORMAL -> Role.NORMAL;
            case VIEWER -> Role.VIEWER;
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

                switch (message.getTypeCase()) {
                    case ANNOUNCEMENT -> {
                        if (node.isInGame())
                            break;

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
                    }
                    case ACK -> {
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());
                    }
                    case JOIN -> {
                        if (!node.isInGame())
                            break;

                        System.out.println("get join");
                        var msg = message.getJoin();
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());

                        Player p = new Player(msg.getName(), PlayerType.HUMAN, task.from.getHostName(), task.from.getPort(), Role.NORMAL, 0, message.getSenderId());
                        node.addPlayer(p, message.getSenderId());
                        node.getField().spawnNewSnake(msg.getName(), message.getSenderId());

                        if (node.getPlayersRepository().getPlayersNumber() == 2) {
                            //todo дать роль заместителя 2 игроку
                        }

                        node.sendAck(task.message.getMsgSeq(), task.from, task.message.getSenderId());
                    }
                    case STATE -> {
                        if (!node.isInGame())
                            break;

                        var stateMessage = message.getState().getState();
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());

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
                        node.sendAck(task.message.getMsgSeq(), task.from, task.message.getSenderId());
                    }

                    case ROLE_CHANGE -> {
                        if (!node.isInGame())
                            break;

                        var roleMessage = message.getRoleChange();
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());
                        node.setRole(convertRoleFormat(roleMessage.getReceiverRole()));
                        node.sendAck(task.message.getMsgSeq(), task.from, task.message.getSenderId());
                    }

                    case PING -> {
                        System.out.println("Ping from: " + task.from.getHostName() + ":" + task.from.getPort());
                        if (!node.isInGame())
                            break;
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());
                        node.sendAck(task.message.getMsgSeq(), task.from, task.message.getSenderId());
                    }

                    case STEER -> {
                        if (!node.isInGame())
                            break;

                        System.out.println("get steer");

                        var msg = message.getSteer();
                        node.getPlayersRepository().setRecvMsgTime(message.getSenderId(), Calendar.getInstance().getTime().getTime());

                        System.out.println("get steer from id:" + message.getSenderId());
                        System.out.println("ensable snakes: ");
                        for (var s : node.getField().getAliveSnakes()) {
                            System.out.println(s.getPlayerId());
                        }
                        node.getField().setDirectionToSnake(message.getSenderId(), switch (msg.getDirection()) {
                            case UP -> Direction.UP;
                            case DOWN -> Direction.DOWN;
                            case LEFT -> Direction.LEFT;
                            case RIGHT -> Direction.RIGHT;
                        });
                        node.sendAck(task.message.getMsgSeq(), task.from, task.message.getSenderId());
                    }
                    case ERROR -> {
                    }
                    case TYPE_NOT_SET -> {
                    }
                }
            }
        }
    }
}
