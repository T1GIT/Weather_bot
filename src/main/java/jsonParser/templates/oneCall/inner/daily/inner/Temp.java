package jsonParser.templates.oneCall.inner.daily.inner;

public class Temp {
    private double min;
    private double max;
    private double morn;
    private double day;
    private double eve;
    private double night;

    public int getMin() {
        return (int) min;
    }

    public int getMax() {
        return (int) max;
    }

    public int getMorn() {
        return (int) morn;
    }

    public int getDay() {
        return (int) day;
    }

    public int getEve() {
        return (int) eve;
    }

    public int getNight() {
        return (int) night;
    }
}
