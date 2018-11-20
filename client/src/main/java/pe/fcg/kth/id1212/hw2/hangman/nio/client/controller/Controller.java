package pe.fcg.kth.id1212.hw2.hangman.nio.client.controller;

import pe.fcg.kth.id1212.hw2.hangman.nio.client.net.RawMessageHandler;
import pe.fcg.kth.id1212.hw2.hangman.nio.client.net.ServerEndpoint;
import pe.fcg.kth.id1212.hw2.hangman.nio.common.controller.Message;

import java.io.IOException;

public class Controller {
    private final ServerEndpoint serverEndpoint = new ServerEndpoint();

    public void connect(String host, int port, RawMessageHandler rawMessageHandler) throws IOException {
        serverEndpoint.connect(host, port, rawMessageHandler, new Message(Message.Type.STARTNEW).toString());
    }

    public void disconnect() throws IOException {
        serverEndpoint.disconnect();
    }

    public void makeGuess(String guess) {
        Message message = new Message(Message.Type.GUESS, guess);
        sendMessage(message);
    }

    public void startNewGame() {
        Message message = new Message(Message.Type.STARTNEW);
        sendMessage(message);
    }

    private void sendMessage(Message message) {
        serverEndpoint.enqueueOutgoingMessage(message.toString());
    }
}
