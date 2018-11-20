package pe.fcg.kth.id1212.hw2.hangman.nio.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class NetUtils {
    private static final int MAX_MESSAGE_SIZE = 1024;
    static final String NW_MESSAGE_DELIMITER = "$";

    public static ByteBuffer getPackagedMessage(String message) {
        Integer messageLength = message.length();
        String packagedMessage = messageLength.toString() + NW_MESSAGE_DELIMITER + message;
        return ByteBuffer.wrap(packagedMessage.getBytes());
    }

    public static String receiveMessage(ReadableByteChannel readableByteChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_MESSAGE_SIZE);
        int bytesReadCount = readableByteChannel.read(buffer);
        if(bytesReadCount == -1) {
            throw new IOException("Connection closed.");
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }
}
