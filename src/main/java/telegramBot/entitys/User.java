package telegramBot.entitys;

import jsonParser.Parser;
import org.apache.log4j.Logger;
import telegramBot.TelegramBot;
import weatherGetter.WeatherGetter;

import java.io.IOException;


public class User {
    private static TelegramBot telegramBot;
    private static WeatherGetter weatherGetter;
    private final String id;
    private String location;
    private String name;
    private static Logger log = Logger.getLogger(User.class);

    public static void setWeatherGetter(WeatherGetter weatherGetter) {
        User.weatherGetter = weatherGetter;
    }

    public static void setTelegramBot(TelegramBot telegramBot) {
        User.telegramBot = telegramBot;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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
            String jsonString = weatherGetter.getCurrent(getLocation());
            String outText = Parser.current(jsonString);
            telegramBot.sendMsg(this, outText);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void sendTomorrow() {

    }

    public void sendWeek() {

    }

    public void sendMonth() {

    }

    public String getName() {
        return name;
    }
}
