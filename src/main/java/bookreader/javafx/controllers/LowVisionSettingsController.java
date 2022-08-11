package bookreader.javafx.controllers;

import bookreader.components.LowVisionSettings;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("LowVisionSettings.fxml")
public class LowVisionSettingsController {

    // dependencies
    private final LowVisionSettings lowVisionSettings;
    private final MainScreenController mainScreenController;

    @Autowired
    public LowVisionSettingsController(
            LowVisionSettings lowVisionSettings,
            MainScreenController mainScreenController) {
        this.lowVisionSettings = lowVisionSettings;
        this.mainScreenController = mainScreenController;
    }

    public void increaseFontSize() {
        lowVisionSettings.increaseFontSize();
        mainScreenController.refreshLowVisionSettings();
    }

    public void decreaseFontSize() {
        lowVisionSettings.decreaseFontSize();
        mainScreenController.refreshLowVisionSettings();
    }

    public void cycleMainColors() {
        lowVisionSettings.cycleMainColors();
        mainScreenController.refreshLowVisionSettings();
    }

    public void cycleSpotlightColors() {
        lowVisionSettings.cycleSpotlightColors();
        mainScreenController.refreshLowVisionSettings();
    }
}
