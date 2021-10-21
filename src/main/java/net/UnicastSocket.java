package net;

import snakes.Snakes.GameMessage;
import java.io.IOException;
import java.net.*;

public class UnicastSocket implements Runnable {
    private final int BufferSize = 2048;

    private DatagramSocket socket;
    private InetAddress groupAddr;
    private int port;

    private QueueMsg queueMsgToHandle;

    public UnicastSocket(String ip, int port, QueueMsg queueMsgToHandle) {
        this.queueMsgToHandle = queueMsgToHandle;
        this.port = port;

        try {
            socket = new DatagramSocket();
            groupAddr = InetAddress.getByName("239.192.0.4");
        } catch (SocketException | UnknownHostException e) {
        }
    }

    public void sendMulticast(GameMessage msg) {
        byte[] bytes = msg.toByteArray();

        try {
            socket.send(new DatagramPacket(bytes, bytes.length, groupAddr, port));
        } catch (IOException e) {
            System.out.println("can't send invite message because что-то пошло не так");
        }
    }

    public void sendUnicast(GameMessage msg, InetSocketAddress to) {
        byte[] bytes = msg.toByteArray();

        try {
            socket.send(new DatagramPacket(bytes, bytes.length, to.getAddress(), to.getPort()));
        } catch (IOException e) {
            System.out.println("can't send message because что-то пошло не так");
        }
    }

    public int getSeq() {
        return MessagesCounter.next();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BufferSize];
        System.out.println("listen on " + socket.getLocalPort());

        DatagramPacket dgram = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(dgram);

                byte[] buf = new byte[dgram.getLength()];
                System.arraycopy(dgram.getData(), 0, buf, 0, dgram.getLength());

                snakes.Snakes.GameMessage msg = snakes.Snakes.GameMessage.parseFrom(buf);
                queueMsgToHandle.add(new Message(msg, new InetSocketAddress(dgram.getAddress(), dgram.getPort())));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            dgram.setLength(buffer.length);
        }
    }
}
