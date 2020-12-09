package jsonParser.templates.generalInner;

public class Weather {
    private String main;
    private String description;

    public String getSign() {
        return switch (main) {
            case "Thunderstorm" -> "⛈";
            case "Drizzle" -> "\uD83C\uDF26";
            case "Rain" -> "\uD83C\uDF27";
            case "Snow" -> "\uD83C\uDF28";
            case "Clear" -> "☀";
            case "Clouds" -> "☁";
            case "Mist", "Smoke", "Haze", "Fog", "Sand", "Dust", "Ash", "Squall", "Tornado" -> "\uD83C\uDF2B";
            default -> throw new IllegalStateException("Unexpected value: " + description);
        };
    }

    public String getDescription() { return description; }
}
