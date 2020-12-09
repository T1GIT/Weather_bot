package weatherGetter;

import com.google.gson.Gson;
import exceptions.CityNotFoundException;
import exceptions.InvalidApiKeyException;
import jsonParser.templates.currentWeather.CurrentOW;
import jsonParser.templates.currentWeather.inner.Coord;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import telegramBot.entities.Location;
import utils.Logger;
import utils.Paths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherGetter {
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
            Logger.error(e);
        }
    }

    private String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    private String addMetrics(String url) {
        return url + String.format("&units=%s&lang=%s",
                units, lang);
    }

    private String addKey(String url) {
        return url + String.format("&appid=%s",
                apiKeys.get(lastKeyNum));
    }

    private void refreshKey() {
        lastKeyNum = (lastKeyNum + 1) % apiKeys.size();
    }

    private String request(String rowUrl) throws IOException {
        String answer;
        String url = addKey(addMetrics(rowUrl.replaceAll(" ", "+")));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200 -> answer = EntityUtils.toString(response.getEntity());
                    case 404 -> throw new CityNotFoundException();
                    case 429 -> {
                        refreshKey();
                        return request(rowUrl);
                    }
                    case 401 -> throw new InvalidApiKeyException();
                    default -> throw new IOException();
                }
            }
        }
        Logger.requestLog(url, answer);
        return answer;
    }

    public Location getLocationByCity(String cityName) throws IOException {
        String url = String.format("%s?q=%s",
                currentRoot, encode(cityName));
        CurrentOW currentOW = gson.fromJson(request(url), CurrentOW.class);
        Coord coord = currentOW.getCoord();
        return new Location(cityName, coord.getLat(), coord.getLon());
    }

    public String getCurrent(Location location) throws IOException {
        String url = String.format("%s?q=%s",
                currentRoot, encode(location.getCity()));
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
}
