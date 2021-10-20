package net;

public class AnnounceSender implements Runnable {
    private MessageFactory factory;

    private boolean isWorking = false;
    public NetSocket socket;

    public AnnounceSender(NetSocket socket, MessageFactory factory) {
        this.socket = socket;
        this.factory = factory;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isWorking)
                socket.sendMulticast(factory.getInviteMsg());
        }
    }
}

