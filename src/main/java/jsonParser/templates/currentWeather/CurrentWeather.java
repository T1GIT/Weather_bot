package jsonParser.templates.currentWeather;

import jsonParser.templates.currentWeather.inner.Main;
import jsonParser.templates.currentWeather.inner.Weather;
import jsonParser.templates.currentWeather.inner.Wind;

import java.util.TimeZone;

public class CurrentWeather {
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private long dt;
    private long timezone;
    private String name;

    public String[] getWeathers() {
        String[] result = new String[weather.length];
        String sign;
        String state;
        for (int i = 0; i < result.length; i++) {
            sign = weather[i].getSign();
            state = weather[i].getDescription();
            result[i] = sign + state.substring(0, 1).toUpperCase() + state.substring(1) + sign;
        }
        return result;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public long getDate() {
        return dt * 1000;
    }

    public TimeZone getTimezone() {
        return TimeZone.getTimeZone(TimeZone.getAvailableIDs()[0]);
    }

    public String getCity() {
        return name;
    }
}

