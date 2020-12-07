package jsonParser.templates.currentWeather.inner;

public class Wind {
    private double speed;
    private double deg;

    public String getSpeed() {
        return (int) speed + " м/с";
    }

    public String getDirection() {
        String dir;
        if (deg <= 22.5 || deg > 337.5) dir = "Северный";
        else if (deg <= 67.5) dir = "Северо-Восточный";
        else if (deg <= 112.5) dir = "Восточный";
        else if (deg <= 157.5) dir = "Юго-Восточный";
        else if (deg <= 202.5) dir = "Южный";
        else if (deg <= 247.5) dir = "Юго-Западный";
        else if (deg <= 292.5) dir = "Западный";
        else dir = "Северо-Западный";
        return dir;
    }
}
