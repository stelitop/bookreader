package bookreader;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class MotionChecker {

    /**
     * The last frame recorded by the Motion Checker.
     */
    private BufferedImage lastFrame;

    /**
     * Defautl constructor.
     */
    public MotionChecker() {
        this.lastFrame = null;
    }

    /**
     * Adds a new frame to the motion checker which is compared to the last frame to check for any significant differences.
     * @param nextFrame The next frame from the camera.
     * @return Whether there has been motion since the last frame.
     */
    public boolean addNextFrame(BufferedImage nextFrame) {

        if (this.lastFrame == null) {
            this.lastFrame = nextFrame;
            return false;
        }
        this.lastFrame = nextFrame;
        return false;
    }
}
