package bookreader.components;

import bookreader.exceptions.UnsupportedExtensionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/**
 * Processes a given file and outputs the text contained within as a string.
 */
@Component
public class FileProcesser {

    private final OCR ocr;

    @Autowired
    public FileProcesser(OCR ocr) {
        this.ocr = ocr;
    }

    /**
     * Process a file and outputs the text contents of it. This method
     * checks the extension of the file and redirects it to the correct
     * method for parsing.
     * @param file The file to process.
     * @return A string containing all of the text in the file.
     * @throws UnsupportedExtensionException If the extension is not supported
     * @throws IOException If there's a problem reading from the file
     * @throws FileNotFoundException If the file is not found.
     * @throws NullPointerException If the given file is null.
     */
    public String processFile(File file) throws UnsupportedExtensionException, IOException,
            FileNotFoundException, NullPointerException {
        if (file == null) throw new NullPointerException();
        int lastDot = file.getAbsolutePath().lastIndexOf(".");
        if (lastDot == -1) throw new IOException();
        String extension = file.getAbsolutePath().substring(lastDot + 1).toLowerCase();

        System.out.println(extension);

        switch (extension) {
            case "txt":
                return processTxtFile(file);
            case "png":
            case "jpg":
                return ocr.processImage(file);
            case "pdf":
            default:
                return ocr.processFile(file);
                //throw new UnsupportedExtensionException();
                // TODO change the default case to try processing the file with tesseract.
        }
    }

    /**
     * Reads a txt file and outputs the data within.
     * @param file
     * @return
     * @throws IOException
     */
    private String processTxtFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)
        ) {
            StringBuilder ret = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                ret.append(str).append("\n");
            }
            System.out.println(ret);
            return ret.toString();
        }

//        BufferedReader bf = new BufferedReader(new FileReader(file));
//        StringBuilder ret = new StringBuilder();
//        String line = bf.readLine();
//        while (line != null) {
//            ret.append(line).append("\n");
//            line = bf.readLine();
//        }
//        bf.close();

//        StringBuilder ret = new StringBuilder();
//        Scanner scanner = new Scanner(file, StandardCharsets.UTF_8);
//        while (scanner.hasNextLine()) {
//            ret.append(scanner.nextLine()).append("\n");
//        }
//        scanner.close();
    }
}
