package pe.fcg.kth.id1212.hw2.hangman.nio.client.net;

import pe.fcg.kth.id1212.hw2.hangman.nio.common.net.MessageBag;
import pe.fcg.kth.id1212.hw2.hangman.nio.common.net.NetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class ServerEndpoint implements Runnable {
    private SocketChannel serverSocketChannel;
    private Selector selector;
    private final Queue<ByteBuffer> outgoingMessages = new ArrayDeque<>();
    private boolean connected = false;
    private MessageBag messageBag = new MessageBag();
    private RawMessageHandler listener;
    private String initialMessageOnConnect;

    @Override
    public void run() {
        try {
            while(connected || outgoingMessages.size() > 0) {
                if(outgoingMessages.size() > 0 && connected) {
                    serverSocketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                }
                selector.select();
                for(SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if(!key.isValid()) {
                        continue;
                    }
                    if(key.isConnectable()) {
                        handleConnection();
                    } else if(key.isReadable()) {
                        handleRead();
                    } else if(key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        } catch(Exception e) {
            if(connected) {
                listener.handleLostConnection(e.getMessage());

            }
        }
    }

    public void connect(String host, int port, RawMessageHandler rawMessageHandler, String initialMessageOnConnect) throws IOException {
        listener = rawMessageHandler;
        this.initialMessageOnConnect = initialMessageOnConnect;
        serverSocketChannel = SocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.connect(new InetSocketAddress(host, port));
        connected = true;
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        new Thread(this).start();
    }

    public void disconnect() throws IOException {
        serverSocketChannel.close();
        serverSocketChannel.keyFor(selector).cancel();
        serverSocketChannel = null;
        connected = false;
    }

    public void enqueueOutgoingMessage(String message) {
        synchronized (outgoingMessages) {
            outgoingMessages.add(NetUtils.getPackagedMessage(message));
        }
        selector.wakeup();
    }

    private void handleConnection() throws IOException {
        serverSocketChannel.finishConnect();
        enqueueOutgoingMessage(initialMessageOnConnect);
    }

    private void handleRead() throws IOException {
        String chunk = NetUtils.receiveMessage(serverSocketChannel);
        messageBag.addChunk(chunk);
        while(messageBag.hasNext()) {
            String message = messageBag.next();
            listener.handleIncoming(message);
        }
        selector.wakeup();
    }

    private void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buffer;
        synchronized(outgoingMessages) {
            while((buffer = outgoingMessages.peek()) != null) {
                serverSocketChannel.write(buffer);
                if(buffer.hasRemaining()) {
                    return;
                }
                outgoingMessages.remove();
            }
        }
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }
}
