package net;

import com.google.inject.Inject;
import game.Config;
import game.Direction;
import game.Field;
import snakes.Snakes;
import view.AvailableGamesList;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

public class Node {
    private final int Port = 4446;
    private final String InetAddr = "239.192.0.4";

    private boolean isPlaying;

    private Role role;

    private int nodeId;
    private int remoteServerId;
    private InetSocketAddress remoteServer;

    private Config config;

    private UnicastSocket senderSocket;
    private AnnouncementSocketReceiver receiverSocket;

    private QueueMsg msgToHandle;
    private MessageHandler messageHandler;
    private DisconnectUserMonitor disconnectUserMonitor;
    private Pinger pinger;

    private Thread pingerThread;
    private Thread disconnectMonitorThread;
    private Thread receiverThread;
    private Thread msgHandlerThread;
    private Thread announceThread;
    private Thread unicastThreadListen;
    private AnnounceSender announceSender;

    private Field field;

    private AvailableGamesList availableGames;
    private MessageFactory factory;

    private final PlayersRepository playersRepository = new PlayersRepository();

    @Inject
    public Node(Config cfg) {
        remoteServerId = 0;
        config = cfg;
        isPlaying = false;
        role = Role.NORMAL;

        nodeId = Math.abs(ThreadLocalRandom.current().nextInt());
        System.out.println(nodeId);

        factory = new MessageFactory(this);
        availableGames = new AvailableGamesList();
        msgToHandle = new QueueMsg();

        receiverThread = new Thread(new AnnouncementSocketReceiver(msgToHandle));
        receiverThread.setDaemon(true);
        receiverThread.start();

        messageHandler = new MessageHandler(msgToHandle, availableGames, this);
        msgHandlerThread = new Thread(messageHandler);
        msgHandlerThread.setDaemon(true);
        msgHandlerThread.start();

        senderSocket = new UnicastSocket("239.192.0.4", 4446, msgToHandle);

        announceSender = new AnnounceSender(senderSocket, factory);
        announceThread = new Thread(announceSender);
        announceThread.setDaemon(true);
        announceThread.start();

        unicastThreadListen = new Thread(senderSocket);
        unicastThreadListen.setDaemon(true);
        unicastThreadListen.start();

        disconnectUserMonitor = new DisconnectUserMonitor(playersRepository, this);

        disconnectMonitorThread = new Thread(disconnectUserMonitor);
        disconnectMonitorThread.setDaemon(true);
        disconnectMonitorThread.start();

        pinger = new Pinger(playersRepository, senderSocket, factory, this);
        pingerThread = new Thread(pinger);
        pingerThread.setDaemon(true);
        pingerThread.start();
    }

    public int getNodeId() {
        return nodeId;
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

    public void setInGame(boolean inGame) {
        isPlaying = inGame;
    }

    public boolean isInGame() {
        return isPlaying;
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

    public void connect(String name, InetSocketAddress to, int remoteId) {
        remoteServer = new InetSocketAddress(to.getAddress(), to.getPort());
        senderSocket.sendUnicast(factory.createJoinMessage(name, remoteId), to);
    }

    public void addPlayer(Player player, int id) {
        playersRepository.addPlayer(player, id);
    }

    public void sendChanges() {
        for (Player p : playersRepository.getPlayers()) {
            senderSocket.sendUnicast(factory.createGameStateMessage(field, p.getId()), new InetSocketAddress(p.getIpAddr(), p.getPort()));
        }
    }

    public void sendDirection(Direction dir) {
        senderSocket.sendUnicast(factory.createSteerMessage(dir, remoteServerId), remoteServer);
    }

    public void sendAck(long seq, InetSocketAddress to, int remoteId) {
        senderSocket.sendUnicast(factory.createAck(seq, remoteId), to);
    }

    public PlayersRepository getPlayersRepository() {
        return playersRepository;
    }

    public int getRemoteServerId() {
        return remoteServerId;
    }

    public void setRemoteServerId(int remoteServerId) {
        this.remoteServerId = remoteServerId;
    }

    public void sendRoleChangeMsg(Snakes.NodeRole role, InetSocketAddress to, int remoteId) {
        senderSocket.sendUnicast(factory.createChangeRoleMessage(role, remoteId), to);
    }
}
