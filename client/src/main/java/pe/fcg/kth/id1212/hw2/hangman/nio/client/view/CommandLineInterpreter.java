package pe.fcg.kth.id1212.hw2.hangman.nio.client.view;

import pe.fcg.kth.id1212.hw2.hangman.nio.client.controller.Controller;
import pe.fcg.kth.id1212.hw2.hangman.nio.client.controller.GameStateMessage;
import pe.fcg.kth.id1212.hw2.hangman.nio.client.net.RawMessageHandler;
import pe.fcg.kth.id1212.hw2.hangman.nio.common.controller.Message;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.StringJoiner;

public class CommandLineInterpreter implements Runnable, RawMessageHandler {
    private final String PROMPT = "> ";
    private final Scanner in = new Scanner(System.in);
    private final PrintStream out = new PrintStream(System.out);
    private final Controller controller = new Controller();
    private boolean isWorking = false;
    private Object lastMessage;

    public void start() {
        if(isWorking) {
            return;
        }
        isWorking = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            showInitialScreen();
            while(isWorking) {
                Command command = getNextCommand();
                switch (command.getType()) {
                    case CONNECT:
                        String host = command.getArgs()[Command.HOST_POS];
                        int port = Integer.valueOf(command.getArgs()[Command.PORT_POS]);
                        RawMessageHandler rawMessageHandler = this;
                        controller.connect(host, port, rawMessageHandler);
                        break;
                    case QUIT:
                        disconnect();
                        break;
                    case GUESS:
                        String guess = command.getArgs()[Command.GUESS_POS];
                        controller.makeGuess(guess);
                        break;
                    case START:
                        controller.startNewGame();
                        break;
                    case UNKNOWN:
                        print(lastMessage);
                        break;
                }
            }
        } catch(Exception e) {
            printWithoutPrompt("Failure: " + e.getMessage());
        }
    }

    synchronized private void print(Message message) {
        clearConsole();
        GameStateMessage gameState = new GameStateMessage(message);
        out.print(getAvailableCommands(gameState));
        out.println();
        out.println();
        out.println(getScreenInformation(gameState));
        out.println();
        out.print(PROMPT);
        lastMessage = message;
    }

    synchronized private void print(String message) {
        clearConsole();
        System.out.println(message);
        out.print(PROMPT);
        lastMessage = message;
    }

    synchronized private void print(Object message) {
        if(message instanceof  Message) {
            print((Message) message);
        } else if(message instanceof String) {
            print((String) message);
        }
    }

    private String getAvailableCommands(GameStateMessage gameState) {
        String guess = "guess <guess>\t\ttype in a single character or a complete word to make a guess";
        String start = "start\t\t\tget another word to guess";
        String quit = "quit\t\t\texit the application";
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Available commands:");
        switch(gameState.getStatus()) {
            case WIN:
            case LOSS:
                sj.add(start);
                sj.add(quit);
                break;
            case PLAYING:
                sj.add(guess);
                sj.add(quit);
                break;
        }
        return sj.toString();
    }

    private String getScreenInformation(GameStateMessage gameState) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Score: " + gameState.getScore());
        switch(gameState.getStatus()) {
            case WIN:
                sj.add("You won :)");
                break;
            case LOSS:
                sj.add("You lost :(");
                break;
            case PLAYING:
                sj.add("Remaining attempts: " + gameState.getRemainingAttempts());
                sj.add("Guessed so far: " + gameState.getGuessedSoFar());
                break;
        }
        return sj.toString();
    }

    synchronized private void printWithoutPrompt(String message) {
        clearConsole();
        System.out.println(message);
    }

    private void showInitialScreen() {
        String connectionInstructions =
                "Commands:\n" +
                        "connect <host> <port>\tconnect to a server to start a game\n" +
                        "quit\t\t\texit the application\n";
        print(connectionInstructions);
    }

    private void clearConsole() {
        // from https://stackoverflow.com/questions/2979383/java-clear-the-console
        // for Windows-based terminals
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Command getNextCommand() {
        String userInput = in.nextLine();
        return new Command(userInput);
    }

    private void disconnect() throws IOException {
        isWorking = false;
        controller.disconnect();
    }

    @Override
    public void handleIncoming(String rawMessage) {
        print(new Message(rawMessage));
    }

    @Override
    public void handleLostConnection(String message) {
        printWithoutPrompt(message);
        try {
            disconnect();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
