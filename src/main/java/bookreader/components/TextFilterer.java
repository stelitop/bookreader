package bookreader.components;

import bookreader.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TextFilterer {

    private final TextUtils textUtils;

    @Autowired
    public TextFilterer(
        TextUtils textUtils
    ) {
        this.textUtils = textUtils;
    }

    /**
     * Filters the results of an OCR read of an image.
     * @param raw Original text.
     * @return Result after filtering errors.
     */
    public String filterTextFromImage(String raw) {
        // multiple spaces are converted into one
        String textLanguage = textUtils.getLanguage(raw);

        raw = raw.trim().replaceAll(" +", " ");
        raw = raw.replaceAll("-\n", "");
        raw = raw.replaceAll("\n", " ");
        if (textLanguage.equals("bg")) {
            raw = replaceEnglishLettersWithCyrillic(raw);
        }
        raw = removeUnusualCharactersFromText(raw);
        return raw;
    }

    /**
     * Replaces lating letters with cyrillic ones that look similar and
     * can be confused by the OCR. This does not necessarily remove all latin
     * letters.
     * @param raw Original text.
     * @return Text after replacing the potential latin letters.
     */
    public String replaceEnglishLettersWithCyrillic(String raw) {
        raw = raw.replaceAll("B", "в");
        raw = raw.replaceAll("H", "н");
        raw = raw.replaceAll("[kK]","к");
        raw = raw.replaceAll("m", "м");
        raw = raw.replaceAll("n", "н");
        raw = raw.replaceAll("[pP]", "р");
        raw = raw.replaceAll("[xX]", "х");
        raw = raw.replaceAll("y", "у");
        return raw;
    }

    /**
     * Checks if a string that should be a word incorrectly contains digits.
     * This could happen as a result of an OCR mistake.
     * @param word Word to check.
     * @return True if the input is a word containing a mix of letters and digits.
     * If the word doesn't contain any digits or the whole word is a number, returns
     * false.
     */
    public boolean doesWordContainDigits(String word) {
        boolean hasDigit = false, hasChar = false;
        for (int i = 0; i < word.length(); i++) {
            if ('0' <= word.charAt(i) && word.charAt(i) <= '9') hasDigit = true;
            else hasChar = true;
        }
        return hasDigit && hasChar;
    }

    /**
     * Takes a whole sentence
     * @param raw
     * @return
     */
    public String replaceDigitsWithCyrillicLetters(String raw) {
        var words = textUtils.splitTextToWords(raw);
        var filtered = words.stream().map(x -> {
            if (!doesWordContainDigits(x)) return x;
            return x.replaceAll("[68]", "в");
        }).toList();
        return textUtils.reconstructTextFromWords(filtered);
    }

    /**
     * Removes unusual characters from a string that should not normally be in
     * the text.
     * They are ©|[]{}<>
     * @param raw Original text.
     * @return Text after the specified characters are removed.
     */
    public String removeUnusualCharactersFromText(String raw) {
        raw = raw.replace("©", "");
        raw = raw.replace("|", "");
        raw = raw.replace("[", "");
        raw = raw.replace("]", "");
        raw = raw.replace("{", "");
        raw = raw.replace("<", "");
        raw = raw.replace(">", "");
        raw = raw.replace("}", "");
        return raw;
    }
}
