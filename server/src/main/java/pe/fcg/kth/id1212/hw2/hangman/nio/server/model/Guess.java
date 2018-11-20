package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

public class Guess {
    private String content;

    public Guess(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
