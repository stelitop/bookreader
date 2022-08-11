package bookreader.components;

import bookreader.exceptions.UnsupportedExtensionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Locale;

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

        switch (extension) {
            case "txt":
                return processTxtFile(file);
            case "png":
            case "jpg":
            case "pdf":
                return ocr.processFile(file);
            default:
                throw new UnsupportedExtensionException();
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
        BufferedReader bf = new BufferedReader(new FileReader(file));
        StringBuilder ret = new StringBuilder();
        String line = bf.readLine();
        while (line != null) {
            ret.append(line).append("\n");
            line = bf.readLine();
        }
        return ret.toString();
    }
}
