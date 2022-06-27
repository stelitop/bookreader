package bookreader;

import bookreader.javafx.JavaFxApplication;
import org.springframework.boot.SpringApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookreaderApplication {

	public static void main(String[] args) {
		//SpringApplication.run(BookreaderApplication.class, args);
		Application.launch(JavaFxApplication.class, args);
	}
}
