package net;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class DisconnectUserMonitor implements Runnable {
    private PlayersRepository playersRepository;

    public DisconnectUserMonitor(PlayersRepository players) {
        this.playersRepository = players;
    }

    @Override
    public void run() {

    }
}
