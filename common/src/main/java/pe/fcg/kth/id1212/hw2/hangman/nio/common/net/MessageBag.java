package pe.fcg.kth.id1212.hw2.hangman.nio.common.net;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * Aggregates chunks of text and provides coherent and complete messages when available.
 */
public class MessageBag {
    private StringBuilder chunks = new StringBuilder();
    private Queue<String> messages = new ArrayDeque<>();

    public void addChunk(String s) {
        chunks.append(s);
        while(hasAvailableMessage()) {
            getAvailableMessage();
        }
    }

    public boolean hasNext() {
        return !messages.isEmpty();
    }

    public String next() {
        return messages.poll();
    }

    private boolean hasAvailableMessage() {
        String s = chunks.toString();
        String[] parts = s.split(Pattern.quote(NetUtils.NW_MESSAGE_DELIMITER));
        if(parts.length < 2) {
            return false;
        }
        Integer messageLength = Integer.valueOf(parts[0]);
        return parts[1].length() >= messageLength;
    }

    private void getAvailableMessage() {
        String s = chunks.toString();
        String[] parts = s.split(Pattern.quote(NetUtils.NW_MESSAGE_DELIMITER));
        Integer messageLength = Integer.valueOf(parts[0]);
        String message = parts[1].substring(0, messageLength);
        messages.add(message);
        // remove used part from chunks
        s = s.substring(parts[0].length() + NetUtils.NW_MESSAGE_DELIMITER.length() + message.length());
        chunks = new StringBuilder(s);
    }
}
