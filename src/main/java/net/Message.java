package net;

import snakes.Snakes;

import java.net.InetSocketAddress;

public class Message {
    public InetSocketAddress from;
    public Snakes.GameMessage message;

    public Message(Snakes.GameMessage message, InetSocketAddress from) {
        this.message = message;
        this.from = from;
    }
}
