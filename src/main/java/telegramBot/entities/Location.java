package telegramBot.entities;

public class Location {
    private final double lat;
    private final double lon;
    private final String city;

    public Location(String city, double lat, double lon) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }


    public String getCity() {
        return city;
    }
}
