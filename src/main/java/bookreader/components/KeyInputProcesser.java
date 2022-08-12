package bookreader.components;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Deals with keyboard inputs that relate to the currently selected text
 * and what to do with it.
 */
@Component
public class KeyInputProcesser implements EventHandler<KeyEvent> {

    // Dependencies
    private final TextHighlighter textHighlighter;
    private final TTSSynthesiser ttsSynthesiser;
    private final ScanningCamera scanningCamera;

    @Autowired
    public KeyInputProcesser(
            TextHighlighter textHighlighter,
            TTSSynthesiser ttsSynthesiser,
            ScanningCamera scanningCamera
    ) {
        this.textHighlighter = textHighlighter;
        this.ttsSynthesiser = ttsSynthesiser;
        this.scanningCamera = scanningCamera;
    }

    @Override
    public void handle(KeyEvent event) {
        if (!textHighlighter.isLoaded()) return;

        boolean wordChange = switch (event.getCode()) {
            case LEFT -> textHighlighter.selectPreviousWord();
            case RIGHT -> textHighlighter.selectNextWord();
            case DOWN -> textHighlighter.selectWordBelow();
            case UP -> textHighlighter.selectWordAbove();
            case A -> textHighlighter.selectPreviousSentence();
            case D -> textHighlighter.selectNextSentence();
            default -> false;
        };

        if (!wordChange) return;
        ttsSynthesiser.readRange(textHighlighter.getSelectionStart(), textHighlighter.getSelectionEnd());
    }
}
