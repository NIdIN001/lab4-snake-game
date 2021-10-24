package net;

import snakes.Snakes;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Map;

public class DisconnectUserMonitor implements Runnable {
    private final PlayersRepository playersRepository;
    private final Map<Integer, Long> times;
    private final Node node;

    public DisconnectUserMonitor(PlayersRepository players, Node node) {
        this.node = node;
        this.playersRepository = players;
        times = playersRepository.getLastRecvMsg();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long curTime = Calendar.getInstance().getTime().getTime();

            synchronized (times) {
                for (Integer id : times.keySet()) {
                    if (curTime - times.get(id) > 1000) {
                        Player deathPlayer = playersRepository.findById(id);
                        times.remove(id);
                        playersRepository.removePlayer(id);

                        if (node.getRole() == Role.MASTER && deathPlayer.getRole() == Role.DEPUTY) {
                            if (playersRepository.getPlayersNumber() == 0)
                                return;
                            else
                                node.sendRoleChangeMsg(Snakes.NodeRole.DEPUTY,
                                        new InetSocketAddress(playersRepository.getFirstPlayer().getIpAddr(), playersRepository.getFirstPlayer().getPort()),
                                        playersRepository.getFirstPlayer().getId());
                        }

                        if (node.getRole() == Role.DEPUTY && deathPlayer.getRole() == Role.MASTER) {
                            node.setRole(Role.MASTER);
                        }

                        System.out.println("Disconnect: " + id);
                    }
                }
            }
        }
    }
}
