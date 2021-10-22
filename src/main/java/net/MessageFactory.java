package net;

import game.Direction;
import game.Field;
import game.Point;
import snakes.Snakes;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MessageFactory {
    private MessagesCounter counter = new MessagesCounter();
    private Node node;

    public MessageFactory(Node node) {
        this.node = node;
    }

    private Snakes.GameConfig createConfigMsg() {
        return Snakes.GameConfig.newBuilder()
                .setWidth(node.getConfig().getWidth())
                .setHeight(node.getConfig().getHeight())
                .setFoodStatic(node.getConfig().getFoodStatic())
                .setFoodPerPlayer(node.getConfig().getFoodPerPlayer())
                .setStateDelayMs(node.getConfig().getStateDelayMs())
                .setDeadFoodProb(node.getConfig().getDeadFoodProb())
                .setPingDelayMs(node.getConfig().getPingDelayMs())
                .setNodeTimeoutMs(node.getConfig().getNodeTimeoutMs())
                .build();
    }

    public Snakes.GameMessage getJoinMsg(String name, InetSocketAddress to) {
        Snakes.GameMessage.JoinMsg joinMsg = Snakes.GameMessage.JoinMsg.newBuilder()
                .setName(name)
                .setPlayerType(Snakes.PlayerType.HUMAN)
                .setOnlyView(false)
                .build();

        return Snakes.GameMessage.newBuilder()
                .setMsgSeq(MessagesCounter.next())
                .setJoin(joinMsg)
                .build();
    }


    public Snakes.GameMessage getInviteMsg() {
        Snakes.GameConfig configMsg = createConfigMsg();

        Snakes.GamePlayers players = Snakes.GamePlayers.newBuilder()
                .build();

        Snakes.GameMessage.AnnouncementMsg announceMsg = Snakes.GameMessage.AnnouncementMsg.newBuilder()
                .setCanJoin(true)
                .setConfig(configMsg)
                .setPlayers(players)
                .build();

        return Snakes.GameMessage.newBuilder()
                .setMsgSeq(MessagesCounter.next())
                .setAnnouncement(announceMsg)
                .build();
    }

    public Snakes.GameMessage createSteerMessage(Direction dir) {
        Snakes.GameMessage.SteerMsg msg = Snakes.GameMessage.SteerMsg.newBuilder()
                .setDirection(switch (dir) {
                    case UP -> Snakes.Direction.UP;
                    case DOWN -> Snakes.Direction.DOWN;
                    case LEFT -> Snakes.Direction.LEFT;
                    case RIGHT -> Snakes.Direction.RIGHT;
                })
                .build();

        return Snakes.GameMessage.newBuilder()
                .setSteer(msg)
                .setMsgSeq(MessagesCounter.next())
                .setSenderId(node.getNodeId())
                .build();
    }

    public Snakes.GameMessage createJoinMessage(int receiverId, String playerName) {
        return Snakes.GameMessage.newBuilder()
                .setMsgSeq(MessagesCounter.next())
                .setSenderId(node.getNodeId())
                .setReceiverId(receiverId)
                .setJoin(Snakes.GameMessage.JoinMsg.newBuilder().setName(playerName).build())
                .build();
    }

    public Snakes.GameMessage createGameStateMessage(Field field) {
        var snakes = field.getAliveSnakes().stream().map(
                snake -> Snakes.GameState.Snake.newBuilder()
                        .setState(switch (snake.getState()) {
                            case ALIVE -> Snakes.GameState.Snake.SnakeState.ALIVE;
                            case ZOMBIE -> Snakes.GameState.Snake.SnakeState.ZOMBIE;
                        })
                        .setPlayerId(snake.getPlayerId())
                        .addAllPoints(snake.getBody().stream().map(snakeNode -> Snakes.GameState.Coord.newBuilder()
                                .setX(snakeNode.width)
                                .setY(snakeNode.height)
                                .build())
                                .collect(Collectors.toList()))
                        .setHeadDirection(switch (snake.getDirection()) {
                            case UP -> Snakes.Direction.UP;
                            case DOWN -> Snakes.Direction.DOWN;
                            case LEFT -> Snakes.Direction.LEFT;
                            case RIGHT -> Snakes.Direction.RIGHT;
                        })
                        .build()
        ).collect(Collectors.toList());

        ArrayList<Snakes.GameState.Coord> foodCoordinates = new ArrayList<>();//field.getEatPoints();
        for (Point p : field.getEatPoints()) {
            foodCoordinates.add(coord(p.width, p.height));
        }

        var gamePlayers = Snakes.GamePlayers.newBuilder()
                .addAllPlayers(node.getPlayersRepository().getPlayers().stream()
                        .map(player ->
                                Snakes.GamePlayer.newBuilder()
                                        .setName(player.getName())
                                        .setId(player.getId())
                                        .setIpAddress(player.getIpAddr())
                                        .setPort(player.getPort())
                                        .setScore(player.getScore())
                                        .setRole(switch (player.getRole()) {
                                            case MASTER -> Snakes.NodeRole.MASTER;
                                            case DEPUTY -> Snakes.NodeRole.DEPUTY;
                                            case NORMAL -> Snakes.NodeRole.NORMAL;
                                            case VIEWER -> Snakes.NodeRole.VIEWER;
                                        })
                                        .setType(Snakes.PlayerType.HUMAN)
                                        .build()).collect(Collectors.toList()))
                .build();

        //fixme save remote config
        return Snakes.GameMessage.newBuilder()
                .setSenderId(node.getNodeId())
                .setReceiverId(1000)
                .setMsgSeq(MessagesCounter.next())
                .setState(Snakes.GameMessage.StateMsg.newBuilder()
                        .setState(Snakes.GameState.newBuilder()
                                .addAllSnakes(snakes)
                                .addAllFoods(foodCoordinates)
                                .setPlayers(gamePlayers)
                                .setConfig(createConfigMsg())
                                .setStateOrder(MessagesCounter.next()))
                        .build())
                .build();
    }

    private Snakes.GameState.Coord coord(int x, int y) {
        return Snakes.GameState.Coord.newBuilder().setX(x).setY(y).build();
    }
}
