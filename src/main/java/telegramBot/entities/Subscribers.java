package telegramBot.entities;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Subscribers extends Thread {
    private static final Calendar day = Calendar.getInstance();
    private static final int[] time = new int[]{9, 0, 0};
    private static final long period = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    private final HashSet<User> subscribers;
    private final Timer timer;

    public Subscribers() {
        System.out.println("INTITIALISING");
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
                System.out.println("SCHEDULE");
                for (User user: subscribers) {
                    System.out.println("SENDING >>>>>>>>>>> " + user.getName());
                    user.sendCurrent();
                }
            }
        }, day.getTime(), period);
    }
}