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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
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
    private Media cameraSound;

    @Autowired
    public MotionDetectionController(ScanningCamera scanningCamera) {
        this.scanningCamera = scanningCamera;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scanningCamera.setWebcam(Webcam.getDefault());

        String musicFile = "CameraShutterSoundEffect.mp3";
        this.cameraSound = new Media(new File(musicFile).toURI().toString());
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

        scanningCamera.startMotionDetection(100, new ScanningCamera.MotionEndingListener() {
            @Override
            public void afterMotion(BufferedImage frame) {
                Platform.runLater(() -> {
                    feedbackLabel.setText("Motion at: " + LocalTime.now().toString());
                    MediaPlayer mp = new MediaPlayer(cameraSound);
                    mp.setVolume(0.20);
                    mp.play();
                    imageView.setImage( SwingFXUtils.toFXImage(frame, null) );
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
