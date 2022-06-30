package bookreader;

import bookreader.javafx.JavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		//SpringApplication.run(Main.class, args);
		Application.launch(JavaFxApplication.class, args);
	}
}
