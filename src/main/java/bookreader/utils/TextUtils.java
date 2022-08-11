package bookreader.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class TextUtils {

    /**
     * Set containing all characters that can be used to end a sentence,
     * recognised by the algorithms.
     */
    private final Set<Character> sentenceEndingChars = Set.of(
            '.', '?', '!'
    );

    /**
     * Splits a text into individual words. Words are considered separate
     * if they're separated by an empty space or a new line. Empty words
     * are erased.
     * @param text Text to split.
     * @return List of strings containing all separate words.
     */
    public List<String> splitTextToWords(String text) {
        return Arrays.stream(text.split("[ \n]")).filter(x -> !x.isEmpty()).toList();
    }

    /**
     * Splits the given text into individual sentences. Sentences are separated by
     * '.', '?' or '!', followed by an empty space (interval or new line).
     * @param text Text to split.
     * @return A list containing all sentences.
     */
    public List<String> splitTextToSentences(String text) {
        List<String> ret = new ArrayList<>();
        StringBuilder curSentence = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            curSentence.append(text.charAt(i));
            if (sentenceEndingChars.contains(text.charAt(i)) &&
                    (i == text.length() - 1 || text.charAt(i+1) == ' ' || text.charAt(i+1) == '\n')) {
                ret.add(curSentence.toString());
                curSentence.setLength(0);
                i++;
            }
        }
        if (curSentence.length() != 0) ret.add(curSentence.toString());
        return ret;
    }

    /**
     * Gets a set containing all characters recognised as sentence
     * separators.
     * @return A set containing the matching characters.
     */
    public Set<Character> getSentenceEndingChars() {
        return this.sentenceEndingChars;
    }
}
