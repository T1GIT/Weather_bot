package telegramBot.entitys;

import jsonParser.Parser;
import telegramBot.TelegramBot;
import weatherGetter.WeatherGetter;

import java.io.IOException;

public class User {
    private static Subscribers subscribers;
    private static TelegramBot telegramBot;
    private static WeatherGetter weatherGetter;
    private final String chatId;
    private String name;
    private String location;

    public static void setWeatherGetter(WeatherGetter weatherGetter) {
        User.weatherGetter = weatherGetter;
    }

    public static void setTelegramBot(TelegramBot telegramBot) {
        User.telegramBot = telegramBot;
    }

    public static void setSubscribers(Subscribers subscribers) {
        User.subscribers = subscribers;
    }

    public String getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public boolean hasLocation() {
        return this.getLocation() != null;
    }

    public void setSubscribed(boolean val) {
        if (val) {
            subscribers.add(this);
        } else {
            subscribers.remove(this);
        }
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
