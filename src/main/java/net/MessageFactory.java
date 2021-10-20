package net;

import com.google.inject.Inject;
import game.Direction;
import snakes.Snakes;

import java.net.InetSocketAddress;

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
                .build();
    }

    public Snakes.GameMessage createJoinMessage(int senderId, int receiverId, String playerName) {
        return Snakes.GameMessage.newBuilder()
                .setMsgSeq(MessagesCounter.next())
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setJoin(Snakes.GameMessage.JoinMsg.newBuilder().setName(playerName).build())
                .build();
    }
}