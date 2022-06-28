package bookreader;

import org.springframework.stereotype.Service;

//Service from an example on the internet.
//https://www.vojtechruzicka.com/javafx-spring-boot/
@Service
public class WeatherService {
    public String getWeatherForecast() {
        return "It's gonna snow a lot. Brace yourselves, the winter is coming.";
    }
}
