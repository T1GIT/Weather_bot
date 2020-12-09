package telegramBot.entities;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Subscribers extends Thread {
    private static final long period = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
    private final HashSet<User> subscribers;
    private final Timer timer;

    public Subscribers() {
        subscribers = new HashSet<>();
        timer = new Timer();
    }

    public void add(User user) {
        this.subscribers.add(user);
    }

    public void remove(User user) {
        this.subscribers.remove(user);
    }

    public boolean contains(User user) { return this.subscribers.contains(user); }

    @Override
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (User user: subscribers) user.sendCurrent();
            }
        }, 0, period);
    }
}