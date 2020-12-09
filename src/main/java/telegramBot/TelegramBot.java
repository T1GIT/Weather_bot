package telegramBot;

import exceptions.CityNotFoundException;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.entities.Subscribers;
import telegramBot.entities.User;
import utils.Paths;
import weatherGetter.WeatherGetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String TOKEN = "1456615883:AAHv0T4ySbE4x6ZrlpbSDhSFPwQMjwhb4kY";
    private static final String BOT_USERNAME = "T1WEATHER_bot";
    private static final String BOT_CHAT_ID = "1456615883";
    private static final char adminChar = '$';
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final WeatherGetter weatherGetter;
    private final Subscribers subscribers;
    private final HashMap<String, User> users;
    private final HashSet<String> adminIdList;
    public TelegramBot() {
        super();
        adminIdList = new HashSet<>();
        weatherGetter = new WeatherGetter();
        subscribers = new Subscribers();
        users = new HashMap<>();
        User.setTelegramBot(this);
        User.setWeatherGetter(weatherGetter);
        subscribers.start();
        try {
            Scanner scanner = new Scanner(new FileReader(Paths.getAdmin()));
            while (scanner.hasNextLine()) adminIdList.add(scanner.nextLine().split("\\|")[0].strip());
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotChatId() {
        return BOT_CHAT_ID;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMsg = update.getMessage();
                answer(inMsg);
            }
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

    private User getUser(String chatId, String name) {
        User user;
        if (users.containsKey(chatId)) {
            user = users.get(chatId);
            user.setName(name);
        } else {
            user = new User(chatId, name);
            users.put(chatId, user);
        }
        return user;
    }

    private void answer(Message inMsg) throws IOException {
        String chatId = String.valueOf(inMsg.getChatId());
        String name = inMsg.getChat().getFirstName();
        String inText = inMsg.getText();
        User user = getUser(chatId, name);
        msgLog(user, inText, "from");
        if (inText.charAt(0) == adminChar) {
            adminCommand(user, inText.substring(1));
        } else {
            if (inText.equals("/start")) {
                user.send("Здравствуй, юный подаван (@_@)");
                if (!user.hasLocation()) {
                    user.send("Откуда ты, знать я желаю");
                }
            } else {
                if (!user.hasLocation()) {
                    try {
                        user.setLocation(weatherGetter.getLocationByCity(inText));
                        user.send("Твое местоположение известно мне стало");
                    } catch (CityNotFoundException e) {
                        user.send("Не известен город " + inText + " мне");
                        user.send("Откуда ты, знать я желаю");
                    }
                } else {
                    command(user, inText);
                }
            }
        }
    }

    private void adminCommand(User user, String command) {
        command = command.toLowerCase();
        if (!adminIdList.contains(user.getId())) {
            user.send("Прав администратора ты не имеешь");
        } else {
            if (command.equals("key")) {
                user.send(weatherGetter.getKey());
            } else {
                try {
                    File file = new File(Paths.getLogs(), command + ".log");
                    if (!file.exists()) {
                        user.send("Не существует " + command + ".log");
                    } else {
                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setChatId(user.getId());
                        sendDocument.setDocument(new InputFile(file));
                        execute(sendDocument);
                    }
                } catch (TelegramApiException e) {
                    log.error(e.toString());
                }
            }
        }
    }

    private void command(User user, String command) throws IOException {
        command = command.toLowerCase();
        switch (command) {
            case "сегодня" -> user.sendCurrent();
            case "завтра" -> user.sendTomorrow();
            case "неделя" -> user.sendDaily();
            case "12 часов" -> user.sendHourly();
            case "подписаться" -> {
                if (subscribers.contains(user)) {
                    user.send("Тебя уже знаю я");
                } else {
                    subscribers.add(user);
                    user.send("Каждый день получать погоду будешь ты");
                }
            }
            case "отписаться" -> {
                if (!subscribers.contains(user)) {
                    user.send("Не помню тебя я");
                } else {
                    subscribers.remove(user);
                    user.send("Получать погоду больше не будешь ты");
                }
            }
            case "изменить расположение" -> {
                subscribers.remove(user);
                user.setLocation(null);
                user.send("Куда отправился ты?");
            }
            default ->  {
                try {
                    user.setLocation(weatherGetter.getLocationByCity(command));
                    user.send("Теперь новое у тебя местоположение");
                } catch (CityNotFoundException e) {
                    user.send("Непонятны мне слова твои");
                }
            }
        }
    }

    public synchronized void sendMsg(User user, String outText) {
        try {
            SendMessage outMsg = new SendMessage();
            setButtons(outMsg, subscribers.contains(user), user.hasLocation());
            outMsg.setChatId(user.getId());
            outMsg.setText(outText);
            outMsg.setParseMode("HTML");
            execute(outMsg);
            msgLog(user, outText, "to");
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }

    private synchronized void setButtons(SendMessage outMsg, boolean isSubscribed, boolean withMenu) {
        if (withMenu) {
            KeyboardRow[] rows = new KeyboardRow[4];
            for (int i = 0; i < rows.length; i++) rows[i] = new KeyboardRow();
            rows[0].add("Сегодня"); rows[0].add("Завтра");
            rows[1].add("Неделя");  rows[1].add("12 часов");
            if (isSubscribed)   rows[2].add("Отписаться");
            else                rows[2].add("Подписаться");
            rows[3].add("Изменить расположение");
            List<KeyboardRow> keyboard = new ArrayList<>(Arrays.asList(rows));
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            replyKeyboardMarkup.setKeyboard(keyboard);
            outMsg.enableMarkdown(true);
            outMsg.setReplyMarkup(replyKeyboardMarkup);
        } else {
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            replyKeyboardRemove.setSelective(true);
            replyKeyboardRemove.setRemoveKeyboard(true);
            outMsg.setReplyMarkup(replyKeyboardRemove);
        }
    }

    private void msgLog(User user, String text, String direction) {
        log.info(String.format("%-4s | %s | %10s | %s",
                direction, user.getId(), user.getName(), text.replaceAll("[\f\r\n]", " ")
        ) );
    }
}
