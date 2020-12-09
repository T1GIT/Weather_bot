package jsonParser.templates.currentWeather;

import jsonParser.templates.currentWeather.inner.Coord;
import jsonParser.templates.currentWeather.inner.Main;
import jsonParser.templates.currentWeather.inner.Wind;
import jsonParser.templates.generalInner.InnerAbstract;


public class CurrentOW extends InnerAbstract {
    private Coord coord;
    private Main main;
    private Wind wind;
    private String name;

    public Coord getCoord() {
        return coord;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }
}

