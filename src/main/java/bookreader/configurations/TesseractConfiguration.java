package bookreader.configurations;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfiguration {

    /**
     * Configuration for the tesseract object to be available as a spring bean.
     * @return A new tesseract instance.
     */
    @Bean
    public Tesseract tesseract() {
        return new Tesseract();
    }
}
