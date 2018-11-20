package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

public class GameException extends RuntimeException {
    public GameException(String errorMessage) {
        super(errorMessage);
    }
}
