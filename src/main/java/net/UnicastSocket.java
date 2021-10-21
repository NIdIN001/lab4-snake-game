package net;

import snakes.Snakes.GameMessage;
import java.io.IOException;
import java.net.*;

public class UnicastSocket implements Runnable {
    private DatagramSocket socket;
    private InetAddress groupAddr;
    private int port;

    private QueueMsg queueMsgToHandle;

    public int getSeq() {
        return MessagesCounter.next();
    }

    private MessagesCounter counter;
    private int stateOrder;

    public UnicastSocket(String ip, int port, QueueMsg queueMsgToHandle) {
        this.queueMsgToHandle = queueMsgToHandle;
        this.port = port;
        stateOrder = 1;
        counter = new MessagesCounter();

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
            //System.out.println("send: " + bytes.length + " bytes to " + groupAddr.getHostName() + ":" + port + "I: " + socket.getLocalPort());

        } catch (IOException e) {
            System.out.println("can't send invite message because что-то пошло не так");
        }
    }

    public void sendUnicast(GameMessage msg, InetSocketAddress to) {
        byte[] bytes = msg.toByteArray();

        try {
            socket.send(new DatagramPacket(bytes, bytes.length, to.getAddress(), to.getPort()));
            //System.out.println("send: " + bytes.length + " bytes to " + to.getHostName() + ":" + to.getPort());
        } catch (IOException e) {
            System.out.println("can't send message because что-то пошло не так");
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[2048];
        System.out.println("listen on " + socket.getLocalPort());

        DatagramPacket dgram = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(dgram); // blocks until a datagram is received

                byte[] buf = new byte[dgram.getLength()];
                System.arraycopy(dgram.getData(), 0, buf, 0, dgram.getLength());

                snakes.Snakes.GameMessage msg = snakes.Snakes.GameMessage.parseFrom(buf);
                queueMsgToHandle.add(new Message(msg, new InetSocketAddress(dgram.getAddress(), dgram.getPort())));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            dgram.setLength(buffer.length); // must reset length field!
        }
    }
}
