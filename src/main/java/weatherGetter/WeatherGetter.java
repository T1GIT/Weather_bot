package weatherGetter;

import com.google.gson.Gson;
import exceptions.CityNotFoundException;
import jsonParser.templates.currentWeather.CurrentOW;
import jsonParser.templates.currentWeather.inner.Coord;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import telegramBot.entities.Location;
import utils.Paths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherGetter {
    private static final Logger log = Logger.getLogger(WeatherGetter.class);
    private final Gson gson;
    private final ArrayList<String> apiKeys;
    private int lastKeyNum = 0;
    private static final String units = "metric";
    private static final String lang = "ru";
    private static final String currentRoot = "https://api.openweathermap.org/data/2.5/weather";
    private static final String oneCallRoot = "https://api.openweathermap.org/data/2.5/onecall";

    public String getKey() {
        return lastKeyNum + " | " + apiKeys.get(lastKeyNum);
    }

    public WeatherGetter() {
        apiKeys = new ArrayList<>();
        gson = new Gson();
        try {
            Scanner scanner = new Scanner(new FileReader(Paths.getKeys()));
            while (scanner.hasNextLine()) apiKeys.add(scanner.nextLine().strip());
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }

    private String addMetrics(String url) {
        return String.format(url + "&units=%s&lang=%s",
                units, lang);
    }

    private String addKey(String url) {
        return String.format(url + "&appid=%s",
                apiKeys.get(lastKeyNum));
    }

    private void refreshKey() {
        lastKeyNum = (lastKeyNum + 1) % apiKeys.size();
    }

    private String request(String rowUrl) throws IOException {
        String answer;
        String url = addMetrics(addKey(rowUrl)).replaceAll(" ", "+");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
                int code = response.getStatusLine().getStatusCode();
                if (code == 404) throw new CityNotFoundException();
                else if (code == 401 || code == 429) {
                    refreshKey();
                    return request(rowUrl);
                }
                else if (code != 200) throw new IOException();
                else answer = EntityUtils.toString(response.getEntity());
            }
        }
        requestLog(url, answer);
        return answer;
    }

    public Location getLocationByCity(String cityName) throws IOException {
        String url = String.format("%s?q=%s",
                currentRoot, cityName);
        CurrentOW currentOW = gson.fromJson(request(url), CurrentOW.class);
        Coord coord = currentOW.getCoord();
        return new Location(cityName, coord.getLat(), coord.getLon());
    }

    public String getCurrent(Location location) throws IOException {
        String url = String.format("%s?q=%s",
                currentRoot, location.getCity());
        return request(url);
    }

    public String getDaily(Location location) throws IOException {
        String url = String.format("%s?lat=%f&lon=%f&exclude=alerts,minutely,hourly,current",
                oneCallRoot, location.getLat(), location.getLon());
        return request(url);
    }

    public String getHourly(Location location) throws IOException {
        String url = String.format("%s?lat=%f&lon=%f&exclude=alerts,minutely,current,daily",
                oneCallRoot, location.getLat(), location.getLon());
        return request(url);
    }

    private void requestLog(String url, String answer) {
        log.info(String.format("%-150s | %s",
                url, answer.replaceAll("[\f\r\n]", " ")
        ) );
    }
}
