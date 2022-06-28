package bookreader.javafx;

import bookreader.MotionChecker;
import bookreader.components.ScanningCamera;
import bookreader.utils.ImageUtils;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

@Component
@FxmlView("MotionCapture.fxml")
public class MotionDetectionController implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private Label feedbackLabel;

    private final ScanningCamera scanningCamera;
    private final ImageUtils imageUtils;

    private Webcam webcam;

    @Autowired
    public MotionDetectionController(ScanningCamera scanningCamera, ImageUtils imageUtils) {
        this.scanningCamera = scanningCamera;
        this.imageUtils = imageUtils;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webcam = Webcam.getDefault();
    }

    /**
     * Opens the default camera and starts taking frames periodically, feeding them into the motionchecker.
     * @param event JavaFX action event
     */
    public void startWatching(ActionEvent event) {
//        webcam.open();
//
//        Thread cameraSSThread = new Thread(() -> {
//            final int waitTime = 200;
//            while (webcam.isOpen()) {
//                BufferedImage image = webcam.getImage();
//                Platform.runLater(() -> {
//                    imageView.setImage(SwingFXUtils.toFXImage(image, null));
//                });
//                try {
//                    Thread.sleep(waitTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        cameraSSThread.start();
        System.out.println(scanningCamera.isOpen());
        scanningCamera.setWebcam(Webcam.getDefault());
        //scanningCamera.open();
        scanningCamera.startMotionDetection(100, new WebcamMotionListener() {
            @Override
            public void motionDetected(WebcamMotionEvent wme) {
                Platform.runLater(() -> {
                    feedbackLabel.setText("Motion at: " + LocalTime.now().toString());
                });
            }
        });
    }

    /**
     * Closes the camera.
     * @param event JavaFX action event.
     */
    public void stopWatching(ActionEvent event) {
//        webcam.close();
//        imageView.setImage(null);
        scanningCamera.stopMotionDetection();
    }
}
