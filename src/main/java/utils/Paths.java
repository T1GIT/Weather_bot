package utils;

import java.nio.file.Files;


public abstract class Paths {
    private static final String templatesDir = "src/main/resources/html/";
    private static final String deployTemplatesDir = "app/src/main/resources/html/";
    private static final String admin = "src/main/deploy/administrators.txt";
    private static final String deployAdmin = "app/src/main/deploy/administrators.txt";
    private static final String logDir = "src/main/deploy/logs/";
    private static final String deployLogDir = "app/src/main/deploy/logs/";

    public static String getTemplate() {
        return Files.exists(java.nio.file.Paths.get(templatesDir))
                ? templatesDir
                : deployTemplatesDir;
    }

    public static String getAdmin() {
        return Files.exists(java.nio.file.Paths.get(admin))
                ? admin
                : deployAdmin;
    }

    public static String getLogs() {
        return Files.exists(java.nio.file.Paths.get(logDir))
                ? logDir
                : deployLogDir;
    }

}
