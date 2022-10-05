package bookreader.javafx.controllers;

import bookreader.components.ScanningCamera;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@FxmlView("CameraPreview.fxml")
public class CameraPreviewController {

    @FXML
    private ImageView mainImage;

    private boolean videoOn = false;

    // Dependencies
    private final ScanningCamera scanningCamera;

    @Autowired
    public CameraPreviewController(
        ScanningCamera scanningCamera
    ) {
        this.scanningCamera = scanningCamera;
    }

    public void startVideo() {
        videoOn = true;
        Platform.runLater(() -> {
            while(videoOn) {
                var image = scanningCamera.takePicture();
                mainImage.setImage(SwingFXUtils.toFXImage(image, null));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
