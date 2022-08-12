package bookreader.components;

import bookreader.utils.TextUtils;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Takes care of highlighting the currently selected text on the screen and
 * also moving between texts.
 */
@Component
public class TextHighlighter {

//    /**
//     * Pointer towards the last selected word.
//     *
//     * If multiple words are selected, this usually points to the end of the
//     * sequence. If the words are not consecutive, unexpected behaviour might
//     * occur, but in general the last word selected by the user should be
//     * selected.
//     *
//     * If the pointer is -1, then no words are currently selected.
//     */
//    private int pointer = -1;

    /**
     * Set containing all characters that can be used to end a sentence,
     * recognised by the algorithms.
     */
    private final Set<Character> sentenceEndingChars;

    /**
     * The start index of the sequence of words currently selected. If -1,
     * then no words are selected. If equal to seqEnd, then only one
     * word is selected.
     */
    private int seqStart = -1;

    /**
     * The end index of the sequence of words currently selected. If -1,
     * then no words are selected. If equal to seqStart, then only one
     * word is selected.
     */
    private int seqEnd = -1;

    /**
     * List directly connected to the scene. It contains all words. Every stack
     * pane contains a single text node with the word.
     */
    private List<StackPane> words = null;
    /**
     * List directly connected to the scene. It contains all whitespaces between
     * each word. Every stack pane contains a single text node with an interval.
     */
    private List<StackPane> whitespaceNodes = null;

    // Dependencies
    private final LowVisionSettings lowVisionSettings;
    private final TextUtils textUtils;

    /**
     * Insert dependencies through constructor.
     */
    @Autowired
    public TextHighlighter(
            LowVisionSettings lowVisionSettings,
            TextUtils textUtils) {
        this.lowVisionSettings = lowVisionSettings;
        this.textUtils = textUtils;
        this.sentenceEndingChars = textUtils.getSentenceEndingChars();
    }

    /**
     * Loads the words from a list to the text highlighter. If not load,
     * most other methods will throw a null pointer exception.
     * @param wordsAsNodes Words in an observable list.
     * @param whitespaceNodes List of whitespace nodes. These appear between every
     *                        two words. The size of the list should be the size of
     */
    public void load(@NotNull List<StackPane> wordsAsNodes, @NotNull List<StackPane> whitespaceNodes) {
        this.words = wordsAsNodes;
        this.whitespaceNodes = whitespaceNodes;
    }

    /**
     * Checks whether the text highlighter is loaded. In reality this
     * checks whether the current list of words is null or not.
     * @return True if it is, false otherwise
     */
    public boolean isLoaded() {
        return this.words != null;
    }

    public boolean isNothingSelected() {
        return this.seqStart == -1;
    }

    /**
     * Changes the style of all currently selected words to look as if
     * they're not selected.
     */
    private void unselectCurrent() {
        if (isNothingSelected()) return;
        for (int i = seqStart; i <= seqEnd; i++) {
            Text x = (Text)words.get(i).getChildren().get(0);
            //System.out.println("Unselected - " + i);
            x.setFill(lowVisionSettings.getCurrentColorsMain().getValue());
            words.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsMain().getKey()));

            if (i != seqEnd) {
                whitespaceNodes.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsMain().getKey()));
            }
        }
    }

    /**
     * Changes the style of all currently selected words to look as if
     * they're selected.
     */
    private void selectCurrent() {
        if (isNothingSelected()) return;
        for (int i = seqStart; i <= seqEnd; i++) {
            Text x = (Text)words.get(i).getChildren().get(0);
            //System.out.println("Selected - " + i);
            x.setFill(lowVisionSettings.getCurrentColorsSpotlight().getValue());
            words.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsSpotlight().getKey()));

            if (i != seqEnd) {
                whitespaceNodes.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsSpotlight().getKey()));
            }
        }
    }

    /**
     * Moves the highlighter to the next word. If nothing was selected before,
     * it only selects the first word. If the end is reached, nothing happens.
     * @see TextHighlighter#load(List, List)
     * @return True if the current word has changed, false otherwise.
     */
    public boolean selectNextWord() {
        if (seqEnd >= words.size() - 1) return false;
        if (seqEnd < -1) seqEnd = -1;
        unselectCurrent();
        seqEnd++;
        seqStart = seqEnd;
        selectCurrent();
        return true;
    }

    /**
     * Moves the highlighter to the previous word. If there is no
     * previous word, nothing happens.
     * @see TextHighlighter#load(List, List)
     * @return True if the current word has changed, false otherwise.
     */
    public boolean selectPreviousWord() {
        if (isNothingSelected()) return false;
        if (seqStart <= 0) return false;
        unselectCurrent();
        seqStart--;
        seqEnd = seqStart;
        selectCurrent();
        return true;
    }

    /**
     * General method for selecting the word directly above or below the
     * currently selected word.
     * @param direction +1 for Below, -1 for Above
     * @see TextHighlighter#load(List, List)
     */
    private boolean selectWordAboveOrBelow(int direction) {
        if (isNothingSelected()) return false;

        var cur = words.get(seqStart);
        double neededYlvl = -1;
        int best = -1;
        double bestDiff = Double.MAX_VALUE;

        // find the next word that is below the current word
        for (int next = seqEnd + direction; 0 <= next && next < words.size(); next += direction) {
            // candidate
            var cand = words.get(next);

            if (cand.getBoundsInParent().getMaxY() == cur.getBoundsInParent().getMaxY()) continue;
            // record the Y level of the line under
            if (neededYlvl == -1) neededYlvl = cand.getBoundsInParent().getMaxY();
            // under next line
            if (neededYlvl != cand.getBoundsInParent().getMaxY()) break;

            double curDiff = Math.min(
                    Math.abs(cur.getBoundsInParent().getCenterX() - cand.getBoundsInParent().getMaxX()),
                    Math.abs(cur.getBoundsInParent().getCenterX() - cand.getBoundsInParent().getMinX())
            );
            if (cand.getBoundsInParent().getMinX() <= cur.getBoundsInParent().getCenterX() &&
                    cur.getBoundsInParent().getCenterX() <= cand.getBoundsInParent().getMaxX()) curDiff = 0;

            if (curDiff < bestDiff) {
                bestDiff = curDiff;
                best = next;
            }
        }

        // if no next was found, don't do anything
        if (best == -1) return false;
        unselectCurrent();
        seqStart = seqEnd = best;
        selectCurrent();
        return true;
    }

    /**
     * Selects the word directly below the currently selected word. If there
     * isn't one, nothing happens.
     * @return True if the current word has changed, false otherwise.
     * @see TextHighlighter#load(List, List)
     */
    public boolean selectWordBelow() {
        return selectWordAboveOrBelow(+1);
    }

    /**
     * Selects the word directly above the currently selected word. If there
     * isn't one, nothing happens.
     * @return True if the current word has changed, false otherwise.
     * @see TextHighlighter#load(List, List)
     */
    public boolean selectWordAbove() {
        return selectWordAboveOrBelow(-1);
    }

    /**
     * Selects a specific word from the text.
     * @param index Which word to choose.
     * @return True if the word was successfully chosen i.e. the index was in
     * bounds, false otherwise.
     * @see TextHighlighter#load(List, List)
     */
    public boolean selectSpecificWord(int index) {
        if (index < 0 || index >= words.size()) return false;
        unselectCurrent();
        seqStart = seqEnd = index;
        selectCurrent();
        return true;
    }

    /**
     * Gets the actual last character of a word node. That is to say,
     * the last non-whitespace character in the text.
     * @param x Node object.
     * @return The last non-whitespace character if this is a text node,
     * 0 otherwise. If this is a text node but without any text, 0 is
     * also returned.
     * @see TextHighlighter#load(List, List)
     */
    private char getActualLastLetter(Node x) {
        if (!(x instanceof Text t)) return 0;
        String trimmedText = t.getText().trim();
        if (trimmedText.isEmpty()) return 0;
        return trimmedText.charAt(trimmedText.length() - 1);
    }

    /**
     * Selects the whole sentence containing a given word.
     * @param index Index of the word.
     * @see TextHighlighter#load(List, List)
     */
    private void selectSentenceContaining(int index) {
        unselectCurrent();
        // start from the given word
        seqEnd = index;
        // find the nearest word that ends a sentence
        while (!sentenceEndingChars.contains(getActualLastLetter(words.get(seqEnd).getChildren().get(0)))) {
            if (seqEnd == words.size() - 1) break;
            seqEnd++;
        }
        if (seqEnd == 0) {
            seqStart = 0;
            selectCurrent();
            return;
        }

        // go backwards and find the previous end of a sentence, or the beginning of the text
        seqStart = seqEnd - 1;
        while (!sentenceEndingChars.contains(getActualLastLetter(words.get(seqStart).getChildren().get(0)))) {
            if (seqStart == 0) break;
            seqStart--;
        }

        if (seqStart == 0 && !sentenceEndingChars.contains(getActualLastLetter(words.get(0).getChildren().get(0)))) {
            seqStart--;
        }
        seqStart++;
        selectCurrent();
    }

    /**
     * Selects the next sentence from the text.
     * @return True if a new sentence was successfully selected, false
     * otherwise. False is only returned if we've reached the end of the
     * text.
     * @see TextHighlighter#load(List, List)
     */
    public boolean selectNextSentence() {
        if (seqEnd >= words.size() - 1) return false;
        if (seqEnd < -1) seqEnd = -1;
        selectSentenceContaining(seqEnd + 1);
        return true;
    }

    /**
     * Selects the previous sentence from the text.
     * @return True if a new sentence was successfully selected, false
     * otherwise. False is only returned if we've reached the beginning
     * of the text.
     * @see TextHighlighter#load(List, List)
     */
    public boolean selectPreviousSentence() {
        if (seqStart <= 0) return false;
        selectSentenceContaining(seqStart - 1);
        return true;
    }

    /**
     * When called, it will refresh the main screen to reflect changes to
     * the low vision settings.
     */
    public void refreshLowVisionSettings() {
        Font fontWords = lowVisionSettings.getWordFont();
        Font fontSpacings = lowVisionSettings.getSpacingFont();

        for (int i = 0; i < words.size(); i++) {
            Text x = (Text)words.get(i).getChildren().get(0);
            x.setFont(fontWords);

            if (seqStart <= i && i <= seqEnd) {
                x.setFill(lowVisionSettings.getCurrentColorsSpotlight().getValue());
                words.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsSpotlight().getKey()));

                if (i != seqEnd) {
                    whitespaceNodes.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsSpotlight().getKey()));
                } else {
                    whitespaceNodes.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsMain().getKey()));
                }
            } else {
                x.setFill(lowVisionSettings.getCurrentColorsMain().getValue());
                words.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsMain().getKey()));
                whitespaceNodes.get(i).setBackground(Background.fill(lowVisionSettings.getCurrentColorsMain().getKey()));
            }

            if (i != words.size() - 2) {
                var spacing = (Text)whitespaceNodes.get(i).getChildren().get(0);
                spacing.setFont(fontSpacings);
            }
        }
    }

    /**
     * Gets the currently selected text.
     * @return Currently selected text. If there isn't any, an empty String
     * is returned. That happens when {@link TextHighlighter#isNothingSelected()}
     * is True.
     */
    public String getCurrentSelection() {
        if (isNothingSelected()) return "";
        StringBuilder ret = new StringBuilder();
        for (int i = seqStart; i <= seqEnd; i++) {
            ret.append(((Text)(words.get(i).getChildren().get(0))).getText());
            ret.append(" ");
        }
        return ret.toString().trim();
    }

    /**
     * Gets how many words are currently selected, referred to as the
     * selection size.
     * @return How many words are selected.
     */
    public int getSelectionSize() {
        if (isNothingSelected()) return 0;
        return seqEnd - seqStart + 1;
    }

    /**
     * Clears anything currently selected.
     * @see TextHighlighter#load(List, List) 
     */
    public void clearSelection() {
        unselectCurrent();
        this.seqStart = this.seqEnd = -1;
    }

    /**
     * The start of what's currently selected.
     * @return Index of the first word in the selection, or -1 if nothing
     * is selected.
     */
    public int getSelectionStart() {
        return this.seqStart;
    }

    /**
     * The end of what's currently selected.
     * @return Index of the last word in the selection, or -1 if nothing
     * is selected.
     */
    public int getSelectionEnd() {
        return this.seqEnd;
    }

    /**
     * Gets the word at the given index.
     * @param index Index
     * @return String containing the word at the index, or null if there isn't such a word.
     */
    public String getWordAt(int index) {
        if (index < 0 || index >= this.words.size()) return null;
        return ((Text)this.words.get(index).getChildren().get(0)).getText();
    }
}
