package bookreader.javafx;

import bookreader.WeatherService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.util.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Component
@FxmlView("main-scene.fxml")
public class MyController {

    @FXML
    private ImageView captureDisplay;

    private WeatherService weatherService;

    @Autowired
    public MyController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public void takePicture(ActionEvent actionEvent) {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        //TODO: Optimise image capturing process by converting directly from a byte stream to an JavaFX Image
        captureDisplay.setImage(SwingFXUtils.toFXImage(image, null));
        webcam.close();
        System.out.println("123");
    }
}
