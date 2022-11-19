package bookreader.components;

import bookreader.utils.ImageUtils;
import bookreader.utils.TextUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
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
    private final ImageUtils imageUtils;

    @Autowired
    public OCR(
            Tesseract tesseract,
            TextUtils textUtils,
            ImageUtils imageUtils
    ) {
        this.tesseract = tesseract;
        this.tesseract.setLanguage("bul+eng");
        this.tesseract.setDatapath("./tessdata");

        this.textUtils = textUtils;
        this.imageUtils = imageUtils;
    }

    public String processImage(BufferedImage image) {
        try {
            Mat img = imageUtils.bufferedImage2Mat(image);
            return processImage(img);
        } catch (IOException e) {
            return "";
        }
    }

    public String processImage(File file) throws IOException {
        Mat mat = Imgcodecs.imread(file.getAbsolutePath());
        return processImage(mat);
    }
    /**
     * Processes a buffered image and extracts the text in it.
     * @param img Image to process.
     * @return Text from the image.
     */
    public String processImage(Mat img) throws IOException {

//        try {
//            ImageIO.write(imageUtils.openCVMatToBufferedImage(img),
//                    "jpg", new File("data/step0.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
        //Imgcodecs.imwrite("data/step1.jpg", img);

        //Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1.5, 1.5)));
        //Imgcodecs.imwrite("data/step2.jpg", img);

        //Imgproc.medianBlur(img, img, 3);
        //Imgcodecs.imwrite("data/step3.jpg", img);

        BufferedImage image = imageUtils.openCVMatToBufferedImage(img);
//
//        try {
//            ImageIO.write(imageUtils.openCVMatToBufferedImage(img),
//                    "jpg", new File("data/step3.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
//            var rectangles = tesseract.getSegmentedRegions(image, 0);
//            for (var r : rectangles) {
//                System.out.println(r.toString());
//            }
            String result = tesseract.doOCR(image);

            System.out.println("Tesseract result:");
            System.out.println(result);

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
