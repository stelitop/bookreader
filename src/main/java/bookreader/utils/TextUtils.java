package bookreader.utils;

import javafx.util.Duration;
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

    private final Set<Character> commonLatingCyrillicChars = Set.of(
            'a', 'c', 'e', 'o', 'p', 'x', 'y',
            'а', 'с', 'е', 'о', 'р', 'х', 'у'
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

    /**
     * Determines the language of a given string. The function checks for the appearance
     * of cyrillic and latin letters and compares it. If the text is very long, only the
     * first 1000 letters are used to compare.
     * @param text Text to check.
     * @return The string "bg" if latin appears more, "en" otherwise.
     */
    public String getLanguage(String text) {
        int cyrillic = 0;
        int latin = 0;

        for (int i = 0; i < 1000 && i < text.length(); i++) {
            char c = text.charAt(i);

            if (commonLatingCyrillicChars.contains(c)) continue;

            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) latin++;
            else if (('а' <= c && c <= 'я') || ('А' <= c && c <= 'Я')) cyrillic++;
        }

        return cyrillic >= latin ? "bg" : "en";
    }

    /**
     * Gets how much time to subtract from a mediaplayer that contains this an
     * mp3 file of this text. Because gTTS creates individual words with a small
     * pause at the end, it should be removed by setting the stop time of the
     * mediaplayer early.
     * @param text Text used for TTS.
     * @return A Duration object that can be directly subtracted from another
     * Duration.
     */
    public Duration getSubtractDuration(String text) {
        char c = text.charAt(text.length() - 1);
        if (sentenceEndingChars.contains(c) || c == ',') return Duration.millis(150);
        return Duration.millis(250);
    }
}
