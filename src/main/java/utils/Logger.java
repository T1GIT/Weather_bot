package utils;

import telegramBot.TelegramBot;
import telegramBot.entities.User;
import weatherGetter.WeatherGetter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Logger {
    private static final boolean DEBUG = true;
    private static final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getLogger("rootLogger");
    private static final org.apache.log4j.Logger telegramLogger = org.apache.log4j.Logger.getLogger(TelegramBot.class);
    private static final org.apache.log4j.Logger weatherLogger = org.apache.log4j.Logger.getLogger(WeatherGetter.class);

    public static void messageLog(User user, String text, String direction) {
        String row = String.format("%-4s | %s | %10s | %s",
                direction, user.getId(), user.getName(), text.replaceAll("[\f\r\n]", " ")
        );
        if (DEBUG) System.out.println(row);
        telegramLogger.info(row);
    }

    public static void requestLog(String url, String answer) {
        String row = String.format("%-150s | %s",
                url, answer.replaceAll("[\f\r\n]", " ")
        );
        if (DEBUG) System.out.println(row);
        weatherLogger.info(row);
    }

    public static void error(Exception exception) {
        if (DEBUG) exception.printStackTrace();
        List<StackTraceElement> list = Arrays.asList(exception.getStackTrace());
        Collections.reverse(list);
        rootLogger.error(exception.getMessage() + " - " + list.toString());
    }

    public static void fatal(Exception exception) {
        if (DEBUG) exception.printStackTrace();
        List<StackTraceElement> list = Arrays.asList(exception.getStackTrace());
        Collections.reverse(list);
        rootLogger.error(exception.getMessage() + " - " + list.toString());
    }
}
