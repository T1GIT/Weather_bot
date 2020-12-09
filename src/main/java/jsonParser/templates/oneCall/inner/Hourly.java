package jsonParser.templates.oneCall.inner;


public class Hourly extends InnerOneCallAbstract {
    private double temp;
    private double feels_like;

    public int getTemp() {
        return (int) temp;
    }

    public int getFeelsLike() {
        return (int) feels_like;
    }
}
