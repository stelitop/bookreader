package bookreader.components;

import com.github.sarxos.webcam.Webcam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
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

    @Test
    void constructorNullValues() {
        sc = new ScanningCamera();
        assertThat(sc.getWebcam()).isNull();
    }

    @Test
    void getWebcam() {
        assertThat(sc.getWebcam()).isEqualTo(webcamMock);
    }

    @Test
    void openWebcamNull() {
        sc = new ScanningCamera();
        assertThrows(NullPointerException.class, () -> sc.open());
    }

    @Test
    void openWebcamNormal() {
        sc.open();
        verify(webcamMock, times(1)).open();
    }

    @Test
    void closeWebcam() {
        sc.open();
        when(webcamMock.isOpen()).thenReturn(true);
        sc.close();
        verify(webcamMock, times(1)).close();
    }

    @Test
    void isOpenTrue() {
        when(webcamMock.isOpen()).thenReturn(true);
        sc.open();
        assertThat(sc.isOpen()).isTrue();
    }

    @Test
    void isOpenFalse() {
        when(webcamMock.isOpen()).thenReturn(false);
        assertThat(sc.isOpen()).isFalse();
    }

    @Test
    void startMotionDetection() {
        //TODO
    }

    @Test
    void stopMotionDetection() {
        //TODO
    }

    @Test
    void isMotionDetectionEnabled() {
        assertThat(sc.isMotionDetectionEnabled()).isFalse();
        sc.startMotionDetection(1000, null);
        assertThat(sc.isMotionDetectionEnabled()).isTrue();
    }
}