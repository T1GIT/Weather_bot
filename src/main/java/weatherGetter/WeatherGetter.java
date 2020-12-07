package weatherGetter;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherGetter {
    private final Logger log = Logger.getLogger(WeatherGetter.class);
    private final String apiKey = "939a1ccf2efcee64d5f92bd8d3811fce";
    private final String units = "metric";
    private final String lang = "ru";
    private final String root = "https://api.openweathermap.org/data/2.5/weather";

    private String request(String url) throws IOException {
        StringBuilder response = new StringBuilder();
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();
        String answer = response.toString();
        log.info(url + " | " + answer.replaceAll("\n\f\n", " "));
        return answer;
    }

    public String getCurrent(String location) throws IOException {
        String url = String.format(
                "%s?q=%s&appid=%s&units=%s&lang=%s",
                root,
                location,
                apiKey,
                units,
                lang);
        return request(url);
    }
}
