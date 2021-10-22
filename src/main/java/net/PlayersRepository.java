package net;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayersRepository {
    private int playersNumber = 0;

    private final Map<Integer, Player> playersMap = new HashMap<>(); // user id - player

    public Collection<Player> getPlayers() {
        synchronized (playersMap) {
            return playersMap.values();
        }
    }

    public void addPlayer(Player player, int id) {
        synchronized (playersMap) {
            playersMap.put(id, player);
        }
        playersNumber++;
    }

    public void removePlayer(int id) {
        if (playersMap.remove(id) != null)
            playersNumber--;
    }

    public Player findById(int id) {
        return playersMap.get(id);
    }

    public int getPlayersNumber() {
        return playersNumber;
    }
}
