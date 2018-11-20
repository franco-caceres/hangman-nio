package pe.fcg.kth.id1212.hw2.hangman.nio.server.controller;

import pe.fcg.kth.id1212.hw2.hangman.nio.common.controller.Message;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.FileWordRetriever;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.GamingSession;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.Guess;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.Result;

import static pe.fcg.kth.id1212.hw2.hangman.nio.common.controller.Message.GUESS_POS;


public class Controller {
    private static final String WORD_FILE_PATH = "words.txt";
    private final FileWordRetriever fileWordRetriever = new FileWordRetriever(WORD_FILE_PATH);

    public Message startNewGame(GamingSession gamingSession) {
        Result result = gamingSession.startNewGame(fileWordRetriever);
        return makeMessage(Message.Type.NEWGAMEINFO, result);
    }

    public Message makeGuess(GamingSession gamingSession, Message info) {
        Guess guess = new Guess(info.getContent()[GUESS_POS]);
        Result result = gamingSession.makeGuess(guess);
        return makeMessage(Message.Type.RESULT, result);
    }

    private Message makeMessage(Message.Type type, Result result) {
        return new Message(type,
                Integer.valueOf(result.getScore()).toString(),
                result.getStatus().toString(),
                Integer.valueOf(result.getRemainingAttempts()).toString(),
                result.getGuessedSoFarAsString(),
                Integer.valueOf(result.getWordLength()).toString());
    }
}
