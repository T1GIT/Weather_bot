package telegramBot.entities;

import com.google.gson.Gson;
import jsonParser.Parser;
import jsonParser.templates.currentWeather.CurrentOW;
import jsonParser.templates.oneCall.OneCallOW;
import org.apache.log4j.Logger;
import telegramBot.TelegramBot;
import weatherGetter.WeatherGetter;

import java.io.IOException;


public class User {
    private static final Gson gson = new Gson();
    private static final Logger log = Logger.getLogger(User.class);
    private static TelegramBot telegramBot;
    private static WeatherGetter weatherGetter;
    private final String id;
    private Location location;
    private String name;

    public static void setWeatherGetter(WeatherGetter weatherGetter) {
        User.weatherGetter = weatherGetter;
    }

    public static void setTelegramBot(TelegramBot telegramBot) {
        User.telegramBot = telegramBot;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) { this.name = name; }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean hasLocation() {
        return this.getLocation() != null;
    }

    public void send(String text) {
        telegramBot.sendMsg(this, text);
    }

    public void sendCurrent() {
        try {
            String jsonString = weatherGetter.getCurrent(location);
            CurrentOW currentOW = gson.fromJson(jsonString, CurrentOW.class);
            String outText = Parser.current(currentOW, location.getCity());
            telegramBot.sendMsg(this, outText);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void sendTomorrow() {
        try {
            String jsonString = weatherGetter.getDaily(location);
            OneCallOW oneCallOW = gson.fromJson(jsonString, OneCallOW.class);
            String outText = Parser.tomorrow(oneCallOW.getDaily()[1], location.getCity());
            telegramBot.sendMsg(this, outText);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void sendDaily() {
        try {
            String jsonString = weatherGetter.getDaily(location);
            OneCallOW oneCallOW = gson.fromJson(jsonString, OneCallOW.class);
            String outText = Parser.daily(oneCallOW.getDaily(), location.getCity());
            telegramBot.sendMsg(this, outText);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void sendHourly() {
        try {
            String jsonString = weatherGetter.getHourly(location);
            OneCallOW oneCallOW = gson.fromJson(jsonString, OneCallOW.class);
            String outText = Parser.hourly(oneCallOW.getHourly(), location.getCity());
            telegramBot.sendMsg(this, outText);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}
