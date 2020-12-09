package jsonParser.templates.currentWeather.inner;

public class Main {
    private double temp;
    private double feels_like;
    private int pressure;

    public int getTemp() {
        return (int) temp;
    }

    public int getFeelsLike() {
        return (int) feels_like;
    }

    public double getPressure() {
        return pressure * 0.7501;
    }
}
