package view;

import net.GameAnnounce;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class AvailableGamesList {
    private HashMap<InetSocketAddress, GameAnnounce> games = new HashMap<>();

    public void add(GameAnnounce game) {
        games.put(game.getFrom(), game);
    }

    public void remove(GameAnnounce game) {
        games.remove(game.getFrom());
    }

    public void reset() {
        games.clear();
    }

    public HashMap<InetSocketAddress, GameAnnounce> getMap() {
        return games;
    }
}
