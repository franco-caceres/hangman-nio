package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

import java.util.List;
import java.util.StringJoiner;

public class Result {
    private Game.Status status;
    private int score;
    private int remainingAttempts;
    private List<WordLetter> guessedSoFar;
    private int wordLength;

    Result(Game.Status status, int remainingAttempts, List<WordLetter> guessedSoFar, int wordLength) {
        this.status = status;
        this.remainingAttempts = remainingAttempts;
        this.guessedSoFar = guessedSoFar;
        this.wordLength = wordLength;
    }

    public Game.Status getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getGuessedSoFarAsString() {
        StringJoiner sj = new StringJoiner(",");
        for(WordLetter wordLetter : guessedSoFar) {
            sj.add(wordLetter.toString());
        }
        return sj.toString();
    }

    public int getWordLength() {
        return wordLength;
    }
}
