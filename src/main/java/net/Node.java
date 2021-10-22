package net;

import com.google.inject.Inject;
import game.Config;
import game.Direction;
import game.Field;
import view.AvailableGamesList;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public class Node {
    private final int Port = 4446;
    private final String InetAddr = "239.192.0.4";

    private Role role;

    public int getNodeId() {
        return nodeId;
    }

    private int nodeId;

    private Config config;

    private boolean isInGame;

    private InetSocketAddress remoteServer;

    private UnicastSocket senderSocket;
    private AnnouncementSocketReceiver receiverSocket;

    private QueueMsg msgToHandle;
    private MessageHandler messageHandler;

    private Thread receiverThread;
    private Thread msgHandlerThread;
    private Thread announceThread;
    private Thread unicastThreadListen;
    private AnnounceSender announceSender;

    private Field field;

    private AvailableGamesList availableGames;
    private MessageFactory factory;

    private PlayersRepository playersRepository = new PlayersRepository();

    @Inject
    public Node(Config cfg) {
        config = cfg;
        isInGame = false;
        role = Role.NORMAL;

        nodeId = Math.abs(ThreadLocalRandom.current().nextInt());
        System.out.println(nodeId);

        factory = new MessageFactory(this);
        availableGames = new AvailableGamesList();
        QueueMsg toHandle = new QueueMsg();

        receiverThread = new Thread(new AnnouncementSocketReceiver(toHandle));
        receiverThread.setDaemon(true);
        receiverThread.start();

        messageHandler = new MessageHandler(toHandle, availableGames, this);
        msgHandlerThread = new Thread(messageHandler);
        msgHandlerThread.setDaemon(true);
        msgHandlerThread.start();

        senderSocket = new UnicastSocket("239.192.0.4", 4446, toHandle);

        announceSender = new AnnounceSender(senderSocket, factory);
        announceThread = new Thread(announceSender);
        announceThread.setDaemon(true);
        announceThread.start();

        unicastThreadListen = new Thread(senderSocket);
        unicastThreadListen.setDaemon(true);
        unicastThreadListen.start();
    }

    public AvailableGamesList getAvailableGames() {
        return availableGames;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void createNewGame() {
        isInGame = true;
    }

    public void connectToRemoteGame(Config cfg) {
        isInGame = true;
        this.config = cfg;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field localField) {
        this.field = localField;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void sendAnnounces(boolean b) {
        announceSender.setWorking(b);
    }

    public void connect(String name, InetSocketAddress to) {
        remoteServer = new InetSocketAddress(to.getAddress(), to.getPort());
        senderSocket.sendUnicast(factory.createJoinMessage(0, name), to);
    }

    public void addPlayer(Player player, int id) {
        playersRepository.addPlayer(player, id);
    }

    public void sendChanges() {
        for (Player p : playersRepository.getPlayers()) {
            senderSocket.sendUnicast(factory.createGameStateMessage(field, p.getId()), new InetSocketAddress(p.getIpAddr(), p.getPort()));
            System.out.println("Send changes to: " + p.getIpAddr() + ":" + p.getPort());
        }
    }

    public void sendDirection(Direction dir) {
        senderSocket.sendUnicast(factory.createSteerMessage(dir), remoteServer);
    }

    public void sendAck() {
        senderSocket.sendUnicast(factory.createAck(), remoteServer);
    }

    public PlayersRepository getPlayersRepository() {
        return playersRepository;
    }
}
