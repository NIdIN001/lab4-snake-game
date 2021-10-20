package net;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayersRepository {
    private int playersNumber = 0;
    private int serversNumber = 0;

    private final Map<Integer, Player> playersMap = new HashMap<>(); // user id - player
    //private final Map<Integer, Long> lastReceivedMessageTimeMillis = new HashMap<>(); // id - last received message time
    //private final Map<Integer, Long> lastSentMessageTimeMillis = new HashMap<>(); // id - last sent message time

    //private int currentServerId = -1;
/*
    public List<Player> getPlayersCopy() {
        synchronized (playersMap) {
            return List.copyOf(playersMap.values());
        }
    }

    public Map<Integer, Player> getPlayersMap() {
        return playersMap;
    }

 */

    public Collection<Player> getPlayers() {
        synchronized (playersMap) {
            return playersMap.values();
        }
    }
/*
    public int getPlayersNumber() {
        return playersNumber;
    }

    public void setPlayersNumber(int number) {
        this.playersNumber = number;
    }

 */

    public void addPlayer(Player player, int id) {
        synchronized (playersMap) {
            playersMap.put(id, player);
        }
    }
/*
    public void putPlayer(Player player) {
        synchronized (playersMap) {
            playersMap.put(player.getId(), player);
        }
    }

    public void changePlayerId(int currentId, int newId) {
        synchronized (playersMap) {
            Player currentPlayer = playersMap.remove(currentId);
            currentPlayer.setId(newId);
            playersMap.put(newId, currentPlayer);
        }
    }

    public void updateLastReceivedMessageTimeMillis(int id, long millis, boolean isNew) {
        synchronized (lastReceivedMessageTimeMillis) {
            lastReceivedMessageTimeMillis.put(id, millis);
        }
    }

    public void updateLastSentMessageTimeMillis(int id, long millis, boolean isNew) {
        synchronized (lastSentMessageTimeMillis) {
            lastSentMessageTimeMillis.replace(id, millis);
        }
    }

    public void updateAllSentMessageTimes(long millis) {
        synchronized (lastSentMessageTimeMillis) {
            for (Map.Entry<Integer, Long> entry : lastSentMessageTimeMillis.entrySet()) {
                entry.setValue(millis);
            }
        }
    }

    public Map<Integer, Long> getLastReceivedMessageTimeMillis() {
        return lastReceivedMessageTimeMillis;
    }

    public Map<Integer, Long> getLastSentMessageTimeMillis() {
        return lastSentMessageTimeMillis;
    }

    private int nextPlayerId() {
        return playersNumber++;
    }

    private int nextServerId() {
        return serversNumber++;
    }

    public int getCurrentServerId() {
        return currentServerId;
    }

 */

    public void removePlayer(int id) {
        playersMap.remove(id);
        //lastReceivedMessageTimeMillis.remove(id);
    }

    public Player findById(int id) {
        return playersMap.get(id);
    }
}
