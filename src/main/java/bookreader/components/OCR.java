package bookreader.components;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.List;

@Component
public class OCR {

    /**
     * Tesseract object used for OCR.
     */
    private ITesseract tesseract;

    @Autowired
    public OCR(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    public String processImage(BufferedImage image) {
        try {
            var rectangles = tesseract.getSegmentedRegions(image, 0);
            for (var r : rectangles) {
                System.out.println(r.toString());
            }
            //tesseract.setLanguage("eng");
            //tesseract.setPageSegMode(0);
            String result = tesseract.doOCR(image);
            tesseract.createDocuments("Test.jpg", "idk", List.of(ITesseract.RenderedFormat.TEXT));

            return result;
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Failed to process";
        }
    }
}
