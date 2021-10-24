package net;

import java.util.Calendar;
import java.util.Map;

public class DisconnectUserMonitor implements Runnable {
    private final PlayersRepository playersRepository;
    private final Map<Integer, Long> times;

    public DisconnectUserMonitor(PlayersRepository players) {
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
                        times.remove(id);
                        playersRepository.removePlayer(id);
                        System.out.println("Disconnect: " + id);
                    }
                }
                /*
                for (Map.Entry<Integer, Long> time : times.entrySet()) {
                    if (curTime - time.getValue() > 1000) {
                        times.remove(time.getKey());
                        playersRepository.removePlayer(time.getKey());
                        System.out.println("Disconnect: " + time.getKey());
                    }
                }

                 */
            }
        }
    }
}
