package bookreader.components;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
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
        this.tesseract.setLanguage("bul+eng");
    }

    /**
     * Processes a buffered image and extracts the text in it.
     * @param image Image to process.
     * @return Text from the image.
     */
    public String processImage(BufferedImage image) {
        try {
            var rectangles = tesseract.getSegmentedRegions(image, 0);
            for (var r : rectangles) {
                System.out.println(r.toString());
            }
            String result = tesseract.doOCR(image);

            return filterResults(result);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Failed to process";
        }
    }

    public String processFile(File file) {
        try {
            String result = tesseract.doOCR(file);
            return filterResults(result);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Failed to process";
        }
    }

    /**
     * Filters the results of an OCR read.
     * @param raw Original result.
     * @return Result after filtering errors.
     */
    private String filterResults(String raw) {
        raw = raw.replace("©", "");
        raw = raw.replace("|", "I");
        raw = raw.replace("1", "I");
        raw = raw.replace("[", "");
        raw = raw.replace("]", "");
        raw = raw.replace("{", "");
        raw = raw.replace("<", "");
        raw = raw.replace(">", "");
        raw = raw.replace("}", "");
        raw = raw.replace("\n", " ");
        raw = raw.trim().replaceAll(" +", " ");
        return raw;
    }

    public void test() {
        try {
            System.out.println("Start");
            tesseract.createDocuments("BananaMassacre.png", "output", List.of(ITesseract.RenderedFormat.TEXT));

            System.out.println("End");
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}
