package jsonParser.templates.generalInner;


public abstract class InnerAbstract {
    protected long dt;
    protected Weather[] weather;

    public String getWeather() {
        String sign = weather[0].getSign();
        String state = weather[0].getDescription();
        return sign + state.substring(0, 1).toUpperCase() + state.substring(1) + sign;
    }

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

    public long getDate() {
        return dt * 1000;
    }
}
