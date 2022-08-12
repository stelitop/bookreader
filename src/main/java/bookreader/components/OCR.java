package bookreader.components;

import bookreader.utils.TextUtils;
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
    private final ITesseract tesseract;

    // Dependencies
    private final TextUtils textUtils;

    @Autowired
    public OCR(
            Tesseract tesseract,
            TextUtils textUtils
    ) {
        this.tesseract = tesseract;
        this.tesseract.setLanguage("bul+eng");
        this.textUtils = textUtils;
    }

    /**
     * Processes a buffered image and extracts the text in it.
     * @param image Image to process.
     * @return Text from the image.
     */
    public String processImage(BufferedImage image) {
        try {
//            var rectangles = tesseract.getSegmentedRegions(image, 0);
//            for (var r : rectangles) {
//                System.out.println(r.toString());
//            }
            String result = tesseract.doOCR(image);

            return filterResults(result);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Failed to process";
        }
    }

    /**
     * Processes a given file an extracts the text in a single string.
     * @param file File to process.
     * @return The text content of the file, or null if the file couldn't be
     * processed. There's also the possibility of an empty string when the
     * file was successfully processed.
     */
    public String processFile(File file) {
        try {
            this.tesseract.setLanguage("bul+eng");
            String result = tesseract.doOCR(file);
            if (textUtils.getLanguage(result).equals("en")) {
                tesseract.setLanguage("eng");
                result = tesseract.doOCR(file);
            }
            return filterResults(result);
        } catch (TesseractException e) {
            return null;
        }
    }

    /**
     * Filters the results of an OCR read.
     * @param raw Original result.
     * @return Result after filtering errors.
     */
    private String filterResults(String raw) {
        raw = raw.replace("©", "");
        raw = raw.replace("|", "");
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
