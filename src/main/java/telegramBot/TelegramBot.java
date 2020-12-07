package telegramBot;

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
import telegramBot.entitys.Subscribers;
import telegramBot.entitys.User;
import utils.Paths;
import weatherGetter.WeatherGetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final WeatherGetter weatherGetter = new WeatherGetter();
    private final Subscribers subscribers = new Subscribers();
    private final HashMap<String, User> users = new HashMap<>();
    private final HashSet<String> adminIdList = new HashSet<>();
    private final char adminChar = '$';

    public TelegramBot() {
        super();
        User.setTelegramBot(this);
        User.setWeatherGetter(weatherGetter);
        subscribers.start();
        try {
            Scanner scanner = new Scanner(new FileReader(Paths.getAdmin()));
            while (scanner.hasNextLine()) adminIdList.add(scanner.nextLine().strip());
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }

    @Override
    public String getBotToken() {
        return "1456615883:AAHv0T4ySbE4x6ZrlpbSDhSFPwQMjwhb4kY";
    }

    @Override
    public String getBotUsername() {
        return "T1WEATHER_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message inMsg = update.getMessage();
            answer(inMsg);
        }
    }

    private User getUserById(String chatId, String name) {
        User user;
        if (users.containsKey(chatId)) {
            user = users.get(chatId);
        } else {
            user = new User(chatId, name);
            users.put(chatId, user);
        }
        return user;
    }

    private void answer(Message inMsg) {
        String chatId = String.valueOf(inMsg.getChatId());
        String name = inMsg.getChat().getFirstName();
        String inText = inMsg.getText();
        User user = getUserById(chatId, name);
        log.info("in  | " + user.getId() + " | " + user.getName() + " | " + inMsg.getText().replaceAll("\n\f\n", " "));
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
                        weatherGetter.getCurrent(inText);
                        user.setLocation(inText);
                        user.send("Твое местоположение известно мне стало");
                    } catch (IOException e) {
                        user.send("Не известен город " + inText + " мне");
                        user.send("Откуда ты, знать я желаю");
                    }
                } else {
                    command(user, inText);
                }
            }
        }
    }

    private void adminCommand(User user, String logName) {
        if (!adminIdList.contains(user.getId())) {
            user.send("Прав администратора ты не имеешь");
        } else {
            try {
                logName = logName.toLowerCase();
                File file = new File(Paths.getLogs(), logName + ".log");
                if (!file.exists()) {
                    user.send("Не существует " + logName + ".log");
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

    private void command(User user, String command) {
        command = command.toLowerCase();
        switch (command) {
            case "сегодня" -> user.sendCurrent();
            case "завтра" -> {
                user.sendTomorrow();
                user.send("Пока что не работает");
            }
            case "неделя" -> {
                user.sendWeek();
                user.send("Пока что не работает");
            }
            case "месяц" -> {
                user.sendMonth();
                user.send("Пока что не работает");
            }
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
                    subscribers.add(user);
                    user.send("Каждый день получать погоду будешь ты");
                }
                subscribers.remove(user);
                user.send("Получать погоду больше не будешь ты");
            }
            case "изменить расположение" -> {
                subscribers.remove(user);
                user.setLocation(null);
                user.send("Куда отправился ты?");
            }
            default ->  user.send("Непонятны мне слова твои");
        }
    }

    public synchronized void sendMsg(User user, String text) {
        try {
            SendMessage outMsg = new SendMessage();
            setButtons(outMsg, subscribers.contains(user), user.hasLocation());
            outMsg.setChatId(user.getId());
            outMsg.setText(text);
            outMsg.setParseMode("HTML");
            log.info("out | " + user.getId() + " | " + user.getName() + " | " + text.replaceAll("[\r\f\n]", " "));
            execute(outMsg);
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }

    private synchronized void setButtons(SendMessage outMsg, boolean isSubscribed, boolean withMenu) {
        if (withMenu) {
            // Configuring
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            KeyboardRow row1 = new KeyboardRow();
            row1.add("Сегодня");
            row1.add("Завтра");
            KeyboardRow row2 = new KeyboardRow();
            row2.add("Неделя");
            row2.add("Месяц");
            KeyboardRow row3 = new KeyboardRow();
            if (isSubscribed) {
                row3.add("Отписаться");
            } else {
                row3.add("Подписаться");
            }
            KeyboardRow row4 = new KeyboardRow();
            row4.add("Изменить расположение");
            List<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
            keyboard.add(row4);
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
}
