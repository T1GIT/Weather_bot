package jsonParser.templates.oneCall;

import jsonParser.templates.oneCall.inner.daily.Daily;
import jsonParser.templates.oneCall.inner.Hourly;


public class OneCallOW {
    private Hourly[] hourly;
    private Daily[] daily;


    public Hourly[] getHourly() {
        return hourly;
    }

    public Daily[] getDaily() {
        return daily;
    }
}
