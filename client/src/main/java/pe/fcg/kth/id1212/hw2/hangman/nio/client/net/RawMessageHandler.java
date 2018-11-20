package pe.fcg.kth.id1212.hw2.hangman.nio.client.net;

public interface RawMessageHandler {
    void handleIncoming(String rawMessage);
    void handleLostConnection(String message);
}
