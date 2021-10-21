package net;

import view.DependencyInjector;

public class AnnounceSender implements Runnable {
    private MessageFactory factory;

    private boolean isWorking = false;
    public UnicastSocket socket;

    public AnnounceSender(UnicastSocket socket, MessageFactory factory) {
        this.socket = socket;
        this.factory = factory;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public void run() {
        Node node = DependencyInjector.INSTANCE.injector.getInstance(Node.class);

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isWorking & node.getRole() == Role.MASTER)
                socket.sendMulticast(factory.getInviteMsg());
        }
    }
}
