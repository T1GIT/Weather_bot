package telegramBot.entitys;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Subscribers extends Thread {
    private static final Calendar day = Calendar.getInstance();
    private static final int[] time = new int[]{9, 0, 0};
    private static final long period = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
    private final HashSet<User> subscribers;
    private final Timer timer;

    public Subscribers() {
        subscribers = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, time[0]);
        day.set(Calendar.MINUTE, time[1]);
        day.set(Calendar.SECOND, time[2]);
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
                for (User user: subscribers) {
                    user.sendCurrent();
                }
            }
        }, day.getTime(), period);
    }
}