package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

public class GamingSession {
    private int score = 0;
    private Game currentGame = null;

    public Result startNewGame(RandomWordProvider randomWordProvider) {
        if(currentGame != null && currentGame.getStatus() != Game.Status.WIN && currentGame.getStatus() != Game.Status.LOSS) {
            throw new GameException("Failure to start a new game because the current one is still in process.");
        }
        currentGame = new Game();
        Result result = currentGame.start(randomWordProvider.get());
        result.setScore(score);
        return result;
    }

    public Result makeGuess(Guess guess) {
        if(currentGame == null) {
            throw new GameException("Cannot make a guess because a gaming session has not yet been started.");
        }
        Result result = currentGame.makeGuess(guess);
        if(result.getStatus() == Game.Status.WIN) {
            score++;
        } else if(result.getStatus() == Game.Status.LOSS) {
            score--;
        }
        result.setScore(score);
        return result;
    }
}
