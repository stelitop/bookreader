package bookreader.components;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LowVisionSettings {

    /**
     * List of available font size options.
     */
    private final int[] fontSizeOptions = new int[]{24, 36, 48, 72, 128};

    /**
     * List of available spacing options. They are multipliers of the actual
     * font size.
     */
    private final double[] spacingOptions = new double[]{1.0, 1.1, 1.25, 1.35, 1.5};

    /**
     * List of available combinations for the background color and the
     * color of the font.
     */
    private final List<Pair<Paint, Paint>> backgroundFontColors = List.of(
            new Pair<>(Paint.valueOf("FFFFFF"), Paint.valueOf("000000")), // white - black
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("FFFFFF")), // black - white
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("00ff00")), // white - green
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("FFFF00")), // white - yellow
            new Pair<>(Paint.valueOf("0000FF"), Paint.valueOf("FFFF00")), // blue - yellow
            new Pair<>(Paint.valueOf("0000FF"), Paint.valueOf("FFFFFF")), // blue - white
            new Pair<>(Paint.valueOf("E79CA5"), Paint.valueOf("000000")), // rose - black
            new Pair<>(Paint.valueOf("DEAD84"), Paint.valueOf("000000")), // amber - black
            new Pair<>(Paint.valueOf("A6CAF0"), Paint.valueOf("000000")), // sky blue - black
            new Pair<>(Paint.valueOf("FFFF00"), Paint.valueOf("FF0000")), // yellow - red
            new Pair<>(Paint.valueOf("0000FF"), Paint.valueOf("FFFF00")), // blue - yellow
            new Pair<>(Paint.valueOf("FFFF00"), Paint.valueOf("0000FF")), // yellow - blue
            new Pair<>(Paint.valueOf("FFFF00"), Paint.valueOf("000000")), // yellow - black
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("AC3FFF")), // black - violet
            new Pair<>(Paint.valueOf("AC3FFF"), Paint.valueOf("000000")), // violet - black
            new Pair<>(Paint.valueOf("FFFFFF"), Paint.valueOf("0000FF")), // white - blue
            new Pair<>(Paint.valueOf("00FF00"), Paint.valueOf("000000")), // green - black
            new Pair<>(Paint.valueOf("FFFFFF"), Paint.valueOf("FF0000")), // white - red
            new Pair<>(Paint.valueOf("FF0000"), Paint.valueOf("FFFFFF")), // red - white
            new Pair<>(Paint.valueOf("00FF00"), Paint.valueOf("FFFFFF")), // green - white
            new Pair<>(Paint.valueOf("FFFFFF"), Paint.valueOf("00FF00")), // white - green
            new Pair<>(Paint.valueOf("0000FF"), Paint.valueOf("000000")), // blue - black
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("0000FF")), // black - blue
            new Pair<>(Paint.valueOf("FF0000"), Paint.valueOf("000000")), // red - black
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("FF0000")), // black - red
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("AC3FFF")), // white - violet
            new Pair<>(Paint.valueOf("AC3FFF"), Paint.valueOf("FF0000")), // violet - white
            new Pair<>(Paint.valueOf("FF9823"), Paint.valueOf("000000")), // orange - black
            new Pair<>(Paint.valueOf("000000"), Paint.valueOf("FF9823")), // black - orange
            new Pair<>(Paint.valueOf("FFFF00"), Paint.valueOf("00FF00")), // yellow - green
            new Pair<>(Paint.valueOf("00FF00"), Paint.valueOf("FFFF00")), // green - yellow
            new Pair<>(Paint.valueOf("FFFFFF"), Paint.valueOf("FF9823")), // white - orange
            new Pair<>(Paint.valueOf("FF9823"), Paint.valueOf("FFFFFF")) // orange - white
    );

    /**
     * The index at which the current font size is.
     */
    private int fontSizeChoice = 2;
    /**
     * The index at which the current spacing is.
     */
    private int spacingChoice = 0;
    /**
     * The index at which the current combination of colors is.
     */
    private int mainColorCombinationChoice = 0;
    /**
     * The index at which the current combination of spotlight colors are.
     * The currently selected words use those colors.
     */
    private int spotlightColorCombinationChoice = 9;

    @Autowired
    public LowVisionSettings() {

    }

    /**
     * Gets the font size of the letters on the main screen.
     * @return Current font size.
     */
    public int getWordFontSize() {
        return fontSizeOptions[fontSizeChoice];
    }

    /**
     * Gets the font size of the spacings on the main screen.
     * @return Current spacing size.
     */
    public int getSpacingFontSize() {
        return (int)((double)fontSizeOptions[fontSizeChoice]*spacingOptions[spacingChoice]);
    }

    /**
     * Changes the current font size to the next bigger option available.
     * @return True if the size has changed, false otherwise aka the max
     * is reached.
     */
    public boolean increaseFontSize() {
        if (fontSizeChoice == fontSizeOptions.length - 1) return false;
        fontSizeChoice++;
        return true;
    }

    /**
     * Changes the current font size to the next smaller option available.
     * @return True if the size has changed, false otherwise aka the min
     * is reached.
     */
    public boolean decreaseFontSize() {
        if (fontSizeChoice == 0) return false;
        fontSizeChoice--;
        return true;
    }

    /**
     * Gets the colors for the background and the font color used generally.
     * @return A pair containing both colors. The key contains the background
     * color and the value - the font color.
     */
    public Pair<Paint, Paint> getCurrentColorsMain() {
        return backgroundFontColors.get(mainColorCombinationChoice);
    }

    /**
     * Cycles to the next available color combination for main colors.
     */
    public void cycleMainColors() {
        mainColorCombinationChoice++;
        if (mainColorCombinationChoice == backgroundFontColors.size()) {
            mainColorCombinationChoice = 0;
        }
    }

    /**
     * Gets the colors for the background and the font color used for
     * spotlighted words.
     * @return A pair containing both colors. The key contains the background
     * color and the value - the font color.
     */
    public Pair<Paint, Paint> getCurrentColorsSpotlight() {
        return backgroundFontColors.get(spotlightColorCombinationChoice);
    }

    /**
     * Cycles to the next available color combination for spotlight colors.
     */
    public void cycleSpotlightColors() {
        spotlightColorCombinationChoice++;
        if (spotlightColorCombinationChoice == backgroundFontColors.size()) {
            spotlightColorCombinationChoice = 0;
        }
    }

    /**
     * Creates a new Font object that contains all font information.
     * @return Font object.
     */
    public Font getWordFont() {
        return new Font("System", getWordFontSize());
    }

    /**
     * Creates a new Font object that contains all the font information for
     * the spacing characters.
     * @return Font object.
     */
    public Font getSpacingFont() {
        return new Font("System", getSpacingFontSize());
    }
}
