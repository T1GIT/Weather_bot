package telegramBot.entitys;

import jsonParser.Parser;
import telegramBot.TelegramBot;
import weatherGetter.WeatherGetter;

import java.io.IOException;

public class User {
    private static TelegramBot telegramBot;
    private static WeatherGetter weatherGetter;
    private final String id;
    private String location;

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

    public User(String id) {
        this.id = id;
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
            e.printStackTrace();
        }
    }

    public void sendTomorrow() {

    }

    public void sendWeek() {

    }

    public void sendMonth() {

    }
}
