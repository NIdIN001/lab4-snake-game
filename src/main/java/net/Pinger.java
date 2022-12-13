package net;

import java.net.InetSocketAddress;

public class Pinger implements Runnable {
    private final PlayersRepository players;
    private UnicastSocket socket;
    private MessageFactory factory;
    private Node node;

    public Pinger(PlayersRepository playersRepository, UnicastSocket socket, MessageFactory factory, Node node) {
        this.node = node;
        this.factory = factory;
        this.socket = socket;
        players = playersRepository;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (node.getRole() == Role.MASTER) {
                for (Player p : players.getPlayers()) {
                    System.out.println("send ping to: " + p.getIpAddr() + ":" + p.getPort());
                    socket.sendUnicast(factory.createPingMessage(p.getId()), new InetSocketAddress(p.getIpAddr(), p.getPort()));
                }
            }
        }
    }
}
