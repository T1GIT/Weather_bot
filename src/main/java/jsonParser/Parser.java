package jsonParser;

import jsonParser.templates.currentWeather.CurrentOW;
import jsonParser.templates.currentWeather.inner.Main;
import jsonParser.templates.currentWeather.inner.Wind;
import jsonParser.templates.oneCall.inner.Hourly;
import jsonParser.templates.oneCall.inner.daily.Daily;
import jsonParser.templates.oneCall.inner.daily.inner.Temp;
import utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;

public abstract class Parser {
    private static final String templateDir = utils.Paths.getTemplate();
    private static final String SOURCE = "openweathermap.org";
    private static final String[] MONTHS = new String[]{"января", "февраля", "марта", "апреля",
            "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final String[] WEEK_DAYS = new String[]{
            "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    private static final String[] FEEL_STATES = new String[]{
            "☃", "🥶", "🤒", "🤧", "😟", "😌", "😅", "🥵", "🔥"};
    private static final String[] DIRECTIONS = new String[]{
            "⬇ С", "↙ С-В", "⬅ В", "↖ Ю-В", "⬆ Ю", "↗ Ю-З", "➡ З", "↘ С-З"};
    private static final String HTML_OFFSET = "<b> </b> ";

    private static String indent(int size) {
        return HTML_OFFSET.repeat(size);
    }

    private static String getSignByTemp(int temp) {
        return FEEL_STATES[Math.max(0, Math.min(8, (temp + 35) / 10))];
    }

    private static String getWordByDeg(int deg) {
        return DIRECTIONS[(int) Math.max(0, Math.min(7, Math.round(((deg + 22.5) % 360 - 22.5) / 45)))];
    }

    private static String cityModify(String city) {
        if (city.endsWith("а")) {
            city = city.substring(0, city.length() - 1) + "e";  // москвА -> москвЕ
        } else if (!city.endsWith("o")) {
            city += "е";  // новгород -> новгородЕ
        }
        String[] cityArr = city.split(" ");
        if (city.contains(" ")) {
            if (cityArr[0].length() > 2) {
                if (cityArr[0].endsWith("ый")) {
                    cityArr[0] = cityArr[0].substring(0, cityArr[0].length() - 2) + "ом";  // новЫЙ афоне -> новОМ афоне
                }
            }
        }
        for (int i = 0; i < cityArr.length; i++) {
            cityArr[i] = cityArr[i].substring(0, 1).toUpperCase() + cityArr[i].substring(1).toLowerCase();
        }
        city = String.join(" ", cityArr);
        return city;
    }

    private static Calendar getCalendar(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    private static String getWeekDay(Calendar calendar) {
        return WEEK_DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    private static String getMonth(Calendar calendar) {
        return MONTHS[calendar.get(Calendar.MONTH)];
    }

    private static int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private static String getHour(Calendar calendar) {
        String s = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        return s.length() == 1
                ? 0 + s
                : s;
    }

    private static String getMinute(Calendar calendar) {
        String s = String.valueOf(calendar.get(Calendar.MINUTE));
        return s.length() == 1
                ? 0 + s
                : s;
    }

    private static String attachFooter(String row) {
        try {
            Path footerPath = Paths.get(templateDir, "footer.html");
            row += String.format(new String(Files.readAllBytes(footerPath)), SOURCE);
        } catch (IOException e) {
            Logger.error(e);
        }
        return row;
    }

    public synchronized static String current(CurrentOW currentOW, String cityName) {
        String result = null;
        try {
            String template = new String(Files.readAllBytes(Paths.get(templateDir, "current.html")));
            Calendar cal = getCalendar(currentOW.getDate());
            Main main = currentOW.getMain(); Wind wind = currentOW.getWind();
            result = String.format(template,
                    cityModify(cityName),
                    getDay(cal), getMonth(cal), getWeekDay(cal),
                    indent(5), currentOW.getWeather(),
                    indent(2), main.getTemp(), getSignByTemp(main.getFeelsLike()), main.getFeelsLike(),
                    indent(2), main.getPressure(),
                    indent(2), wind.getSpeed(), getWordByDeg(wind.getDeg())
            );
        } catch (IOException e) {
            Logger.error(e);
        }
        return attachFooter(result);
    }

    public synchronized static String tomorrow(Daily daily, String cityName) {
        final int MARGIN = 3;
        final int PIC_MARGIN = 5;
        String result = null;
        try {
            String template = new String(Files.readAllBytes(Paths.get(templateDir, "tomorrow.html")));
            Calendar cal = getCalendar(daily.getDate());
            Temp temp = daily.getTemp();
            result = String.format(template,
                    cityModify(cityName),
                    getDay(cal), getMonth(cal), getWeekDay(cal),
                    indent(5), daily.getWeather(),
                    indent(10),
                    indent(3), indent(PIC_MARGIN), indent(PIC_MARGIN), indent(PIC_MARGIN),
                    indent(1), temp.getMorn(), indent(MARGIN), temp.getDay(), indent(MARGIN),
                                     temp.getEve(), indent(MARGIN), temp.getNight(),
                    indent(2), daily.getPressure(),
                    indent(2), daily.getWindSpeed() , getWordByDeg(daily.getWindDeg())
            );
        } catch (IOException e) {
            Logger.error(e);
        }
        return attachFooter(result);
    }

    public synchronized static String daily(Daily[] dailies, String cityName) {
        StringBuilder result = null;
        try {
            String header = new String(Files.readAllBytes(Paths.get(templateDir, "daily/header.html")));
            String body = new String(Files.readAllBytes(Paths.get(templateDir, "daily/body.html")));
            result = new StringBuilder(String.format(header, cityName));
            for (Daily daily: dailies) {
                Calendar cal = getCalendar(daily.getDate());
                Temp temp = daily.getTemp();
                result.append(String.format(body,
                        getDay(cal), getMonth(cal), getWeekDay(cal),
                        indent(2), daily.getWeather(),
                        indent(2), temp.getMax(), temp.getMin(),
                        indent(2), daily.getWindSpeed(), getWordByDeg(daily.getWindDeg())
                ));
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        assert result != null;
        return attachFooter(result.toString());
    }

    public synchronized static String hourly(Hourly[] hourlies, String cityName) {
        StringBuilder result = null;
        try {
            String header = new String(Files.readAllBytes(Paths.get(templateDir, "hourly/header.html")));
            String body = new String(Files.readAllBytes(Paths.get(templateDir, "hourly/body.html")));
            Calendar dayCal = getCalendar(hourlies[0].getDate());
            result = new StringBuilder(String.format(header,
                    cityName,
                    getDay(dayCal), getMonth(dayCal), getWeekDay(dayCal)
            ));
            for (Hourly hourly: Arrays.copyOf(hourlies, 12)) {
                Calendar cal = getCalendar(hourly.getDate());
                result.append(String.format(body,
                        indent(7), getHour(cal), getMinute(cal),
                        indent(2), hourly.getWeather(),
                        indent(2), hourly.getTemp(), getSignByTemp(hourly.getFeelsLike()), hourly.getFeelsLike(),
                        indent(2), hourly.getWindSpeed(), getWordByDeg(hourly.getWindDeg())
                ));
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        assert result != null;
        return attachFooter(result.toString());
    }
}
