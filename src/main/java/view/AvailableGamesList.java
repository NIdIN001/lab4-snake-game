package view;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class AvailableGamesList {
    private ArrayList<InetSocketAddress> games = new ArrayList<>();

    public void add(InetSocketAddress game) {
        games.add(game);
    }

    public void remove(InetSocketAddress game) {
        games.remove(game);
    }

    public void reset() {
        games.clear();
    }

    public ArrayList<InetSocketAddress> getArray() {
        return games;
    }
}
