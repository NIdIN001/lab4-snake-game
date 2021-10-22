package net;

import game.Config;

import java.net.InetSocketAddress;

public class GameAnnounce {
    private InetSocketAddress from;
    private Config cfg;

    public GameAnnounce(InetSocketAddress from, Config cfg) {
        this.cfg = cfg;
        this.from = from;
    }

    public Config getCfg() {
        return cfg;
    }

    public InetSocketAddress getFrom() {
        return from;
    }
}
