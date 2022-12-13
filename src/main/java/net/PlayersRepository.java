package net;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayersRepository {
    private int playersNumber = 0;

    private final Map<Integer, Player> playersMap = new ConcurrentHashMap<>(); // user id - player
    private final Map<Integer, Long> lastRecvMsg = new ConcurrentHashMap<>();

    public Collection<Player> getPlayers() {
        return playersMap.values();
    }

    public Player getFirstPlayer() {
        return playersMap.values().iterator().next();
    }

    public void setRecvMsgTime(int from, long time) {
        synchronized (lastRecvMsg) {
            lastRecvMsg.put(from, time);
        }
    }

    public Map<Integer, Long> getLastRecvMsg() {
        synchronized (lastRecvMsg) {
            return lastRecvMsg;
        }
    }

    public void addPlayer(Player player, int id) {
        playersMap.put(id, player);
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

    public void clear(){
        playersNumber = 0;
        playersMap.clear();
    }
}
