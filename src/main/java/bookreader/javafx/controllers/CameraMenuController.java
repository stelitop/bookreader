package bookreader.javafx.controllers;

import bookreader.components.ScanningCamera;
import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("CameraMenu.fxml")
public class CameraMenuController implements Initializable {

    @FXML
    private ComboBox<Webcam> comboBox;
    @FXML
    private ImageView imageView;

    private final ScanningCamera scanningCamera;

    @Autowired
    public CameraMenuController(ScanningCamera scanningCamera) {
        this.scanningCamera = scanningCamera;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().add(null);
        var webcams = Webcam.getWebcams();
        for (var webcam : webcams) {
            comboBox.getItems().add(webcam);
        }
        comboBox.setValue(null);
    }

    public void optionChanged(ActionEvent event) {
        scanningCamera.setWebcam(comboBox.getValue());
        if (comboBox.getValue() == null) {
            return;
        }
        scanningCamera.open();
        BufferedImage img = scanningCamera.getWebcam().getImage();
        imageView.setImage(SwingFXUtils.toFXImage(img, null));
        scanningCamera.close();
    }
}
