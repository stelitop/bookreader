package bookreader.javafx.controllers;

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

    @Autowired
    public MotionDetectionController(ScanningCamera scanningCamera) {
        this.scanningCamera = scanningCamera;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scanningCamera.setWebcam(Webcam.getDefault());
    }

    /**
     * Opens the default camera and starts taking frames periodically, feeding them into the motionchecker.
     */
    public void startWatching() {
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
     */
    public void stopWatching() {
//        webcam.close();
//        imageView.setImage(null);
        scanningCamera.stopMotionDetection();
    }
}
