package net;

import snakes.Snakes;

import java.io.IOException;
import java.net.*;

public class AnnouncementSocketReceiver implements Runnable {
    private final int BufferSize = 2048;

    private final int DestPort = 4446;
    private final String InetAddr = "239.192.0.4";
    private QueueMsg queueMsgToHandle;

    private MulticastSocket socket;

    private boolean isRunning;

    public AnnouncementSocketReceiver(QueueMsg queueMsgToHandle) {
        this.isRunning = true;
        this.queueMsgToHandle = queueMsgToHandle;

        try {
            socket = new MulticastSocket(DestPort); // must bind receive side
            socket.joinGroup(InetAddress.getByName(InetAddr));
        } catch (IOException e) {
        }
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BufferSize];

        DatagramPacket dgram = new DatagramPacket(buffer, buffer.length);
        while (isRunning) {
            try {
                socket.receive(dgram); // blocks until a datagram is received

                byte[] buf = new byte[dgram.getLength()];
                System.arraycopy(dgram.getData(), 0, buf, 0, dgram.getLength());

                Snakes.GameMessage msg = Snakes.GameMessage.parseFrom(buf);
                queueMsgToHandle.add(new Message(msg, new InetSocketAddress(dgram.getAddress(), dgram.getPort())));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            dgram.setLength(buffer.length); // must reset length field!
        }
    }
}
