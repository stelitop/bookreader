package bookreader.javafx.controllers;

import bookreader.components.*;
import bookreader.javafx.JavaFxApplication;
import bookreader.utils.TextUtils;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.speech.AudioException;
import javax.speech.EngineException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("MainScreen.fxml")
public class MainScreenController implements Initializable {

    /**
     * Main area where the read text is displayed.
     */
    @FXML
    private TextFlow mainTextWrapper;
    /**
     * The menu bar at the top of the screen that displays options.
     */
    @FXML
    private MenuBar menuBar;
    /**
     * Displays an informative message about the status of loading.
     */
    @FXML
    private TextField loadingText;

    // Dependencies
    private final OCR ocr;
    private final FileProcesser fileProcesser;
    private final TTSSynthesiser tts;
    private final LowVisionSettings lowVisionSettings;
    private final TextHighlighter textHighlighter;
    private final ApplicationContext applicationContext;
    private final TextUtils textUtils;
    private final KeyInputProcesser keyInputProcesser;

    /**
     * Dependencies of the constructor.
     */
    @Autowired
    public MainScreenController(
            OCR ocr,
            FileProcesser fileProcesser,
            TTSSynthesiser tts,
            LowVisionSettings lowVisionSettings,
            TextHighlighter textHighlighter,
            ApplicationContext applicationContext,
            TextUtils textUtils,
            KeyInputProcesser keyInputProcesser)
    {
        this.ocr = ocr;
        this.fileProcesser = fileProcesser;
        this.tts = tts;
        this.lowVisionSettings = lowVisionSettings;
        this.textHighlighter = textHighlighter;
        this.applicationContext = applicationContext;
        this.textUtils = textUtils;
        this.keyInputProcesser = keyInputProcesser;
    }

    /**
     * Code called when the controller is initialised
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        new Thread(() -> {try {
//            tts.testGoogle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        }).start();


        //tts.testPythonGTTS();

        // Add an event filter for key presses related to navigating the selected text.
        JavaFxApplication.getPrimaryStage()
                .addEventFilter(KeyEvent.KEY_PRESSED, keyInputProcesser);
    }

    /**
     * Opens the file explorer menu when the button is clicked.
     */
    public void openFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        File selectedFile = fc.showOpenDialog(JavaFxApplication.getPrimaryStage());
        if (selectedFile == null) return;
        loadingText.setText("Зареждане...");
        new Thread(() -> {
            try {
                String output = fileProcesser.processFile(selectedFile);
                //mainTextArea.setText(output);
                Platform.runLater(() -> {
                    transformTextToNodes(output);
                });
                //System.out.println(output);
                //tts.testPythonReadText(output);
//                try {
//                    tts.testFreeTTS(output);
//                } catch (EngineException | AudioException | InterruptedException e) {
//                    e.printStackTrace();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loadingText.setText("");
            });
        }).start();

    }

    /**
     * Transforms a string into words that are then displayed into individual
     * nodes inside the TextFlow object.
     * @param text Text to transform.
     */
    private void transformTextToNodes(String text) {
        List<String> words = textUtils.splitTextToWords(text);
        mainTextWrapper.getChildren().clear(); // clear current words
        var children = mainTextWrapper.getChildren();
        // create lists where to put the words and the empty spaces
        List<StackPane> actualWords = new ArrayList<>();
        List<StackPane> whitespaces = new ArrayList<>();
        // preload fonts
        Font fontWords = lowVisionSettings.getWordFont();
        Font fontSpacing = lowVisionSettings.getSpacingFont();
        int curIndex = 0;
        for (var word : words) {
            // create the text node for the word
            var textNode = new Text(word);
            int finalCurIndex = curIndex;
            textNode.setOnMouseClicked(event -> {
                //textHighlighter.selectSpecificWord(finalCurIndex);
                tts.readSpecificWord(finalCurIndex);
            });
            textNode.setFont(fontWords);
            // wrap the text node in a stack pane so that you can change the background color
            var nodeWrapperText = new StackPane();
            nodeWrapperText.getChildren().add(textNode);
            children.add(nodeWrapperText);
            actualWords.add(nodeWrapperText);

            // separate the intervals and the actual words
            var spaceNode = new Text(" ");
            spaceNode.setFont(fontSpacing);
            var nodeWrapperScape = new StackPane();
            nodeWrapperScape.getChildren().add(spaceNode);
            children.add(nodeWrapperScape);
            whitespaces.add(nodeWrapperScape);
            curIndex++;
        }
        this.textHighlighter.load(actualWords, whitespaces);
        this.tts.loadSounds(words);
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(this.tts::readFromStart).start();
    }

    /**
     * Gets the scene object of the scene.
     * @return Scene.
     */
    private Scene getScene() {
        return menuBar.getScene();
    }

    /**
     * Opens a new window that displays all low vision settings.
     */
    public void openLowVisionSettingsWindow() {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(LowVisionSettingsController.class);
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Настройки за ниско зрение");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * When called, it will refresh the main screen to reflect changes to
     * the low vision settings.
     */
    public void refreshLowVisionSettings() {
        mainTextWrapper.setBackground(Background.fill(
                lowVisionSettings.getCurrentColorsMain().getKey()));

        if (textHighlighter.isLoaded()) {
            textHighlighter.refreshLowVisionSettings();
        }
    }
}
