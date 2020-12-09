package jsonParser.templates.oneCall.inner;

import jsonParser.templates.generalInner.InnerAbstract;

public abstract class InnerOneCallAbstract extends InnerAbstract {
    private double wind_speed;
    private int wind_deg;
    private int pressure;

    public double getPressure() {
        return pressure * 0.7501;
    }

    public double getWindSpeed() {
        return wind_speed;
    }

    public int getWindDeg() {
        return wind_deg;
    }
}
