package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

public class WordLetter {
    private char letter;
    private int position;

    WordLetter(char letter, int position) {
        this.letter = letter;
        this.position = position;
    }

    public char getLetter() {
        return letter;
    }

    public int getPosition() {
        return position;
    }

    boolean equals(WordLetter obj) {
        return this.letter == obj.letter && this.position == obj.position;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(letter);
        sb.append(",");
        sb.append(position);
        return sb.toString();
    }
}
