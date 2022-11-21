package bookreader.components;

import com.github.sarxos.webcam.Webcam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScanningCameraTest {

    private ScanningCamera sc;
    private Webcam webcamMock;

    @BeforeEach
    void setUp() {
        sc = new ScanningCamera();
        webcamMock = mock(Webcam.class);
        sc.setWebcam(webcamMock);
    }

//    @Test
//    void getWebcam() {
//        assertThat(sc.getWebcam()).isEqualTo(webcamMock);
//    }
//
//    @Test
//    void isMotionDetectionEnabled() {
//        assertThat(sc.isMotionDetectionEnabled()).isFalse();
//        sc.startMotionDetection(1000, null);
//        assertThat(sc.isMotionDetectionEnabled()).isTrue();
//    }
}