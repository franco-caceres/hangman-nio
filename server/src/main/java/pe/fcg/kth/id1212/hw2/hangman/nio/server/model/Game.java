package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

import pe.fcg.kth.id1212.hw2.hangman.nio.server.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

class Game {
    public enum Status {WIN, LOSS, PLAYING, CREATED}

    private Status status = Status.CREATED;
    private String targetWord;
    private List<WordLetter> guessedSoFar;
    private int remainingAttempts;

    Result start(String targetWord) {
        status = Status.PLAYING;
        this.targetWord = targetWord;
        this.guessedSoFar = new LinkedList<>();
        remainingAttempts = targetWord.length();
        return getCurrentState();
    }

    Result makeGuess(Guess guess) {
        if(status != Status.PLAYING) {
            throw new GameException("Game has " + status + " status.");
        }
        if(guess.getContent() == null || guess.getContent().length() == 0) {
            throw new GameException("Empty guess found.");
        }
        boolean isSingleCharacterGuess = guess.getContent().length() == 1;
        if(isSingleCharacterGuess) {
            char guessCharacter = guess.getContent().charAt(0);
            List<Integer> occurrences = StringUtils.getOcurrences(targetWord, guessCharacter);
            if(occurrences.size() > 0) {
                for(Integer occurrence : occurrences) {
                    WordLetter possiblyNewMatch = new WordLetter(guessCharacter, occurrence);
                    boolean alreadyGuessed = false;
                    for(WordLetter wordLetter : guessedSoFar) {
                        if(possiblyNewMatch.equals(wordLetter)) {
                            alreadyGuessed = true;
                            break;
                        }
                    }
                    if(!alreadyGuessed) {
                        guessedSoFar.add(possiblyNewMatch);
                    }
                }
                if(guessedSoFar.size() == targetWord.length()) {
                    status = Status.WIN;
                }
            } else {
                remainingAttempts--;
                if(remainingAttempts == 0) {
                    status = Status.LOSS;
                }
            }
            return getCurrentState();
        } else {
            if(targetWord.toLowerCase().equals(guess.getContent().toLowerCase())) {
                status = Status.WIN;
            }
            return getCurrentState();
        }
    }

    private Result getCurrentState() {
        return new Result(status, remainingAttempts, guessedSoFar, targetWord.length());
    }

    Status getStatus() {
        return status;
    }
}
