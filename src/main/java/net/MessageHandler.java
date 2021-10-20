package net;

import game.Direction;
import snakes.Snakes;
import view.AvailableGamesList;

import java.net.InetSocketAddress;

public class MessageHandler implements Runnable {
    private QueueMsg messages;
    private AvailableGamesList gamesList;
    private Node node;

    public MessageHandler(QueueMsg messages, AvailableGamesList gamesList, Node node) {
        this.node = node;
        this.messages = messages;
        this.gamesList = gamesList;
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
                        if (!gamesList.getArray().contains(task.from))
                            gamesList.add(task.from);
                    }
                    case ACK -> {

                    }
                    case JOIN -> {
                        System.out.println("get join");
                        var msg = message.getJoin();

                        Player p = new Player(msg.getName(), PlayerType.HUMAN, task.from.getPort(), Role.NORMAL, 0, 2);
                        node.addPlayer(p, message.getSenderId());
                        node.getField().spawnNewSnake(msg.getName(), message.getSenderId());
                    }
                    case STATE -> {
                        var stateMessage = message.getState().getState();

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
