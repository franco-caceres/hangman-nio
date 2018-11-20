package pe.fcg.kth.id1212.hw2.hangman.nio.server.net;

import pe.fcg.kth.id1212.hw2.hangman.nio.server.controller.Controller;
import pe.fcg.kth.id1212.hw2.hangman.nio.server.model.GameException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Server {
    Controller controller = new Controller();
    private Set<ClientEndpoint> readyForWrite = Collections.synchronizedSet(new HashSet<>());
    private Selector selector;

    public static void main(String[] args) {
        if(args.length == 1) {
            Server server = new Server();
            server.start(Integer.parseInt(args[0]));
        } else {
            System.out.println("Port argument missing.");
        }
    }

    private void start(int port) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("[" + new Date().toString() + "] Server listening on port " + port + "...");
            while(true) {
                if(readyForWrite.size() > 0) {
                    setWriteInterests();
                }
                selector.select();
                for(SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if(!key.isValid()) {
                        continue;
                    }
                    if(key.isAcceptable()) {
                        handleAccept(key);
                    } else if(key.isReadable()) {
                        handleRead(key);
                    } else if(key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        } catch(IOException e) {
            System.err.println("[" + new Date().toString() + "] Failure to establish server socket (port=" + port + ").");
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        System.out.println("[" + new Date().toString() + "] New client connection.");
        ServerSocketChannel listeningSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientSocketChannel = listeningSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        ClientEndpoint clientEndpoint = new ClientEndpoint(this, clientSocketChannel);
        clientSocketChannel.register(selector, SelectionKey.OP_READ, clientEndpoint);
    }

    private void handleRead(SelectionKey key) throws IOException {
        ClientEndpoint clientEndpoint = (ClientEndpoint) key.attachment();
        try {
            clientEndpoint.handleRead();
        } catch(Exception e) {
            if(e instanceof GameException) {
                System.out.println("[" + new Date().toString() + "] Invalid game action: " + e.getMessage() + " Closing connection.");
            }
            handleDisconnect(key);
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        ClientEndpoint clientEndpoint = (ClientEndpoint) key.attachment();
        try {
            clientEndpoint.handleWrite();
            key.interestOps(SelectionKey.OP_READ);
        } catch(Exception e) {
            if(e instanceof GameException) {
                System.out.println("[" + new Date().toString() + "] Invalid game action: " + e.getMessage() + " Closing connection.");
            }
            handleDisconnect(key);
        }
    }

    void setReadyForWrite(ClientEndpoint clientEndpoint) {
        readyForWrite.add(clientEndpoint);
        selector.wakeup();
    }

    private void setWriteInterests() {
        for(SelectionKey key : selector.keys()) {
            if(key.channel() instanceof SocketChannel && key.isValid()) {
                ClientEndpoint endpoint = (ClientEndpoint) key.attachment();
                if(readyForWrite.contains(endpoint)) {
                    key.interestOps(SelectionKey.OP_WRITE);
                    readyForWrite.remove(endpoint);
                }
            }
        }
    }

    private void handleDisconnect(SelectionKey key) throws IOException {
        ClientEndpoint clientEndpoint = (ClientEndpoint) key.attachment();
        clientEndpoint.disconnect();
        key.cancel();
    }
}
