package bookreader;

import bookreader.javafx.JavaFxApplication;
import javafx.application.Application;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.io.File;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		//SpringApplication.run(Main.class, args);
		Application.launch(JavaFxApplication.class, args);

//		File imageFile = new File("Test.png");
//		ITesseract instance = new Tesseract();
//		try {
//			String result = instance.doOCR(imageFile);
//			System.out.println(result);
//		} catch (TesseractException e) {
//			e.printStackTrace();
//		}
	}
}
