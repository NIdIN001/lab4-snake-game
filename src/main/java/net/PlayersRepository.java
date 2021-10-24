package net;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayersRepository {
    private int playersNumber = 0;

    private final Map<Integer, Player> playersMap = new HashMap<>(); // user id - player

    public synchronized Collection<Player> getPlayers() {
        return playersMap.values();
    }

    public synchronized void addPlayer(Player player, int id) {
        playersMap.put(id, player);
        playersNumber++;
    }

    public synchronized void removePlayer(int id) {
        if (playersMap.remove(id) != null)
            playersNumber--;
    }

    public synchronized Player findById(int id) {
        return playersMap.get(id);
    }

    public int getPlayersNumber() {
        return playersNumber;
    }
}
