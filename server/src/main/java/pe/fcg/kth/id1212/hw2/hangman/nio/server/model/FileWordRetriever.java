package pe.fcg.kth.id1212.hw2.hangman.nio.server.model;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FileWordRetriever implements RandomWordProvider {
    private String filePath;

    public FileWordRetriever(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String get() {
        List<String> words = getWordsFromFile();
        int index = new Random().nextInt(words.size());
        return words.get(index);
    }

    private List<String> getWordsFromFile() {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(filePath)))) {
            List<String> words = new LinkedList<>();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                words.add(line.toLowerCase());
            }
            return words;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }
}
