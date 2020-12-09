import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegramBot.TelegramBot;


public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main (String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            log.error(e.toString());
        } catch (Exception e) {
            log.fatal(e.toString());
        }
    }
}
