package bookreader.components;

import com.github.sarxos.webcam.*;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;

@Component
public class ScanningCamera implements WebcamMotionListener{

    /**
     * Time in ms to wait after a motion was detected. If no motions occur in the
     * timeframe the camera will take a picture of what it sees.
     */
    private static final int MotionlessTimeframe = 3000;

    /**
     * The webcam used. Null means that no webcam has been currently chosen.
     */
    private Webcam webcam;
    /**
     * The motion detector. Null means that motion detection is not currently on.
     */
    private WebcamMotionDetector motionDetector;
    /**
     * Thread started after motion is detected and waits for motion to cease.
     * It waits some time before executing to check for other motion. If new
     * motion is detected then the threat is reset.
     */
    private Thread waitForStopMotionThread;
    /**
     * Listener with a callback after motion has ended. Passed by
     * {@link ScanningCamera#startMotionDetection(int, MotionEndingListener)}
     */
    private MotionEndingListener listener;

    /**
     * Default constructor. Initialises with no webcam (null).
     */
    public ScanningCamera() {
        this.webcam = null;
        this.motionDetector = null;
        this.waitForStopMotionThread = null;
    }

    /**
     * Autodetects a webcam, using the default
     */
    public void autodetectWebcam() {
        this.webcam = Webcam.getDefault();
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
     * Takes a picture with the current camera. If there isn't one, the
     * default one is attempted to be used.
     * @return
     */
    public BufferedImage takePicture() {
        if (this.webcam == null) {
            this.autodetectWebcam();
        }
        if (this.webcam == null) {
            throw new IllegalStateException("No webcam set or could be found.");
        }
        boolean previousState = this.webcam.isOpen();
        if (!previousState) {
            this.webcam.open();
        }
        BufferedImage img = this.webcam.getImage();
        if (!previousState) {
            this.webcam.close();
        }
        return img;
    }

    /**
     * Opens the webcam (if not opened already) and starts tracking for motion.
     * @param interval How much time between frames to check, in milliseconds. Minimum allowed interval is 100ms.
     * @param listener Event listener that triggers after motion has ended
     * @throws IllegalStateException When the scanning camera is not the correct state.
     */
    public void startMotionDetection(int interval, MotionEndingListener listener) throws IllegalStateException {
        if (this.webcam == null) throw new IllegalStateException();
        if (this.motionDetector != null) throw new IllegalStateException();

        this.motionDetector = new WebcamMotionDetector(
                this.webcam,
                WebcamMotionDetectorDefaultAlgorithm.DEFAULT_PIXEL_THREASHOLD + 10,
                WebcamMotionDetectorDefaultAlgorithm.DEFAULT_AREA_THREASHOLD
        );
        this.motionDetector.setInterval(interval);
        this.motionDetector.addMotionListener(this);
        this.listener = listener;
        this.motionDetector.start();
    }

    /**
     * Stops detecting for motion detection if it was turned on before that.
     */
    public void stopMotionDetection() {
        if (this.motionDetector == null) return;
        this.motionDetector.stop();
        this.motionDetector = null;
        if (this.waitForStopMotionThread != null &&
            this.waitForStopMotionThread.getState() == Thread.State.TIMED_WAITING) {
            this.waitForStopMotionThread.interrupt();
        }
    }

    /**
     * Gets the Webcam Motion Detector.
     * @return The webcam motion detector if the it's currently enabled, null if it's not.
     */
    public WebcamMotionDetector getMotionDetector() {
        return this.motionDetector;
    }

    /**
     * Checks whether the webcam is currently looking for motion detection.
     * @return True if it is, false otherwise.
     */
    public boolean isMotionDetectionEnabled() {
        return this.motionDetector != null;
    }

    /**
     * Adds a motion listener that triggers every time the webcam detects motion.
     * @param listener Event listener.
     */
    public void addMotionListener(WebcamMotionListener listener) {
        this.motionDetector.addMotionListener(listener);
    }

    /**
     * Triggered when motion is detected.
     * @param wme event
     */
    @Override
    public void motionDetected(WebcamMotionEvent wme) {
        if (this.waitForStopMotionThread != null && this.waitForStopMotionThread.getState() == Thread.State.TIMED_WAITING) {
            this.waitForStopMotionThread.interrupt();
        }
        this.resetMotionThread();
        this.waitForStopMotionThread.start();
    }

    /**
     * Replaces {@link ScanningCamera#waitForStopMotionThread} with
     * a fresh thread.
     */
    private void resetMotionThread() {
        this.waitForStopMotionThread = new Thread(() -> {
            try {
                Thread.sleep(ScanningCamera.MotionlessTimeframe);
            } catch (InterruptedException e) {
                return;
            }
            //if there has been no motion for a while, snap a frame
            if (!this.webcam.isOpen()) return;
            if (this.listener == null) return;
            BufferedImage frame = this.webcam.getImage();
            this.listener.afterMotion(frame);
        });
    }

    public interface MotionEndingListener {
        void afterMotion(BufferedImage frame);
    }
}
