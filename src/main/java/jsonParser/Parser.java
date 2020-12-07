package jsonParser;

import jsonParser.templates.currentWeather.CurrentWeather;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

public abstract class Parser {
    private static final Gson gson = new Gson();
    private static final String templatesFolder = "src\\main\\resources\\html\\";
    private static final String[] months = new String[]{"января", "февраля", "марта", "апреля",
            "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final String[] weekDays = new String[]{"Воскресенье",
            "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    public static String current(String jsonString) {
        try {
            String path = templatesFolder + "currentWeather.html";
            CurrentWeather currentWeather = gson.fromJson(jsonString, CurrentWeather.class);
            String template = new String(Files.readAllBytes(Paths.get(path)));
            String city = currentWeather.getCity();
            if (city.charAt(city.length() - 1) == 'а') {
                city = city.substring(0, city.length() - 1) + "e";
            } else {
                city += "е";
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentWeather.getDate());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String month = months[calendar.get(Calendar.MONTH)];
            String weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            return String.format(template,
                    city,
                    day + " " + month,
                    weekDay,
                    String.join("\n", currentWeather.getWeathers()),
                    currentWeather.getMain().getTemp(),
                    currentWeather.getMain().getFeels_like(),
                    currentWeather.getMain().getPressure(),
                    currentWeather.getWind().getSpeed(),
                    currentWeather.getWind().getDirection()
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("No template for current weather");
        }
    }
}
