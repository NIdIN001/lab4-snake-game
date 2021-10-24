package net;

import java.util.ArrayDeque;
import java.util.Optional;

public class QueueMsg {
    private final ArrayDeque<Message> messages = new ArrayDeque<>();

    public synchronized void add(Message msg) {
        messages.add(msg);
    }

    public synchronized Optional<Message> get() {
        return Optional.ofNullable(messages.pollFirst());
    }

    public synchronized boolean isEmpty() {
        return messages.isEmpty();
    }
}
