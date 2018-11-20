package pe.fcg.kth.id1212.hw2.hangman.nio.server.util;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {
    public static String getStringWithNCharacters(int length, char c) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static List<Integer> getOcurrences(String word, char c) {
        List<Integer> occurrences = new LinkedList<>();
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == c) {
                occurrences.add(i);
            }
        }
        return occurrences;
    }

    public static boolean hasCharacter(String word, char c) {
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }
}
