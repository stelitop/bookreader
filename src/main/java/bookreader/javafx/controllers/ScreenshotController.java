package bookreader.javafx.controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
@FxmlView("Screenshot.fxml")
public class ScreenshotController {

    @FXML
    private ImageView captureDisplay;

    public void takePicture(ActionEvent actionEvent) {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        //TODO: Optimise image capturing process by converting directly from a byte stream to an JavaFX Image
        captureDisplay.setImage(SwingFXUtils.toFXImage(image, null));
        webcam.close();
    }
}
