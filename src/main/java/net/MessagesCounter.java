package net;

public class MessagesCounter {
    private static int number = 0;
    public static int next() {
        return number++;
    }
}
