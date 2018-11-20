package pe.fcg.kth.id1212.hw2.hangman.nio.server.net;

import pe.fcg.kth.id1212.hw2.hangman.nio.common.controller.Message;
import pe.fcg.kth.id1212.hw2.hangman.nio.common.net.MessageBag;
import pe.fcg.kth.id1212.hw2.hangman.nio.common.net.NetUtils;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.controller.Controller;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.GamingSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

class ClientEndpoint {
    private Server server;
    private SocketChannel clientSocket;
    private Controller controller;
    private GamingSession gamingSession = new GamingSession();
    private MessageBag messageBag = new MessageBag();
    private final Queue<ByteBuffer> outgoingMessages = new ArrayDeque<>();

    ClientEndpoint(Server server, SocketChannel clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.controller = server.controller;
    }

    void handleRead() throws IOException {
        String chunk = NetUtils.receiveMessage(clientSocket);
        messageBag.addChunk(chunk);
        while(messageBag.hasNext()) {
            Message message = new Message(messageBag.next());
            if(isIoExpensive(message)) {
                CompletableFuture.runAsync(() -> {
                    if(System.getProperty("test") != null) {
                        System.out.println("[test] sleeping for 5 seconds to simulate expensive IO operation.");
                        try {
                            Thread.sleep(5000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    enqueueOutgoingMessage(controller.startNewGame(gamingSession));
                });
            } else {
                switch(message.getType()) {
                    case GUESS:
                        enqueueOutgoingMessage(controller.makeGuess(gamingSession, message));
                        break;
                    case CONNECTIONLOST:
                        disconnect();
                        break;
                }
            }
        }
    }

    void handleWrite() throws IOException {
        ByteBuffer buffer;
        synchronized(outgoingMessages) {
            while((buffer = outgoingMessages.peek()) != null) {
                clientSocket.write(buffer);
                if(buffer.hasRemaining()) {
                    throw new IOException("Failure to send message to client.");
                }
                outgoingMessages.remove();
            }
        }
    }

    void disconnect() throws IOException {
        System.out.println("[" + new Date().toString() + "] Client disconnected.");
        clientSocket.close();
    }

    private void enqueueOutgoingMessage(Message message) {
        synchronized (outgoingMessages) {
            outgoingMessages.add(NetUtils.getPackagedMessage(message.toString()));
            server.setReadyForWrite(this);
        }
    }

    private boolean isIoExpensive(Message message) {
        return Message.Type.STARTNEW.equals(message.getType());
    }
}
