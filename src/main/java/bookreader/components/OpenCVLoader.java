package bookreader.components;

import nu.pattern.OpenCV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenCVLoader {

    @Autowired
    public OpenCVLoader() {
        OpenCV.loadShared();

    }
}
