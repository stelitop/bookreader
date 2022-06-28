package bookreader.components;

import bookreader.javafx.MotionDetectionController;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionListener;
import org.springframework.stereotype.Component;

@Component
public class ScanningCamera {
    /**
     * The webcam used. Null means that no webcam has been currently chosen.
     */
    private Webcam webcam;
    /**
     * The motion detector. Null means that motion detection is not currently on.
     */
    private WebcamMotionDetector motionDetector;

    /**
     * Default constructor. Initialises with no webcam (null).
     */
    public ScanningCamera() {
        this.webcam = null;
        this.motionDetector = null;
    }

    /**
     * Gets the webcam.
     * @return The webcam currently used.
     */
    public Webcam getWebcam() {
        return this.webcam;
    }

    /**
     * Sets the webcam.
     * @param webcam The webcam to use, or null for no camera.
     */
    public void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }

    /**
     * Opens the current webcam in blocking mode.
     * @return True if the camera was opened succesfully, false otherwise.
     * @throws WebcamException When something goes wrong with opening the camera.
     * @throws NullPointerException When no webcam has been selected.
     */
    public boolean open() throws WebcamException, NullPointerException {
        if (this.webcam == null) throw new NullPointerException();
        return this.webcam.open();
    }

    /**
     * Closes the webcam.
     */
    public void close() {
        if (this.webcam != null && this.webcam.isOpen()) webcam.close();
    }

    /**
     * Checks whether the webcam is currently opened or not.
     * @return True if it's opened, false otherwise.
     */
    public boolean isOpen() {
        if (this.webcam == null) return false;
        return this.webcam.isOpen();
    }

    /**
     * Opens the webcam (if not opened already) and starts tracking for motion.
     * @param interval How much time between frames to check, in milliseconds.
     * @param listener Event listener that triggers when motion is detected.
     * @throws IllegalStateException When the scanning camera is not the correct state.
     */
    public void startMotionDetection(int interval, WebcamMotionListener listener) throws IllegalStateException {
        if (this.webcam == null) throw new IllegalStateException();
        if (this.motionDetector != null) throw new IllegalStateException();

        this.motionDetector = new WebcamMotionDetector(this.webcam);
        this.motionDetector.setInterval(interval);
        this.motionDetector.addMotionListener(listener);
        this.motionDetector.start();
    }

    /**
     * Stops detecting for motion detection if it was turned on before that.
     */
    public void stopMotionDetection() {
        if (this.motionDetector == null) return;
        this.motionDetector.stop();
        this.motionDetector = null;
    }

    /**
     * Checks whether the webcam is currently looking for motion detection.
     * @return True if it is, false otherwise.
     */
    public boolean isMotionDetectionEnabled() {
        return this.motionDetector != null;
    }
}
