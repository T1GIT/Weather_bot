package jsonParser.templates.currentWeather.inner;

public class Main {
    private double temp;
    private double feels_like;
    private int pressure;


    public int getTemp() {
        return (int) temp;
    }

    public int getFeels_like() {
        return (int) feels_like;
    }

    public int getPressure() {
        return (int) (pressure * 0.7501);
    }
}
