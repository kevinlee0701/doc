package com.kevinlee.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 时间轮
 *
 * @create 2020-09-21 10:08
 */
public class TimeWheelDemo {
    private static final int SLOT_COUNT = 10; // 槽位数量
    private static final int INTERVAL = 1000; // 时间间隔，毫秒
    private static final int TOTAL_TIME = 10; // 总共模拟的时间，秒
    private static final int[] DELAYS = {3, 6}; // 需要模拟的延迟触发时间，秒

    private List<List<TimerTask>> slots; // 时间轮的槽位

    public TimeWheelDemo() {
        slots = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots.add(new ArrayList<>());
        }
    }

    public void addTask(TimerTask task, int delay) {
        int slotIndex = (delay / INTERVAL) % SLOT_COUNT;
        slots.get(slotIndex).add(task);
    }

    public void start() {
        Timer timer = new Timer();
        for (int i = 0; i < TOTAL_TIME; i++) {
            int slotIndex = i % SLOT_COUNT;
            for (TimerTask task : slots.get(slotIndex)) {
                timer.schedule(task, 0); // 立即执行任务
            }
            try {
                Thread.sleep(INTERVAL); // 等待一个时间间隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timer.cancel();
    }

    public static void main(String[] args) {
        TimeWheelDemo timeWheel = new TimeWheelDemo();
        for (int delay : DELAYS) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Task triggered after " + delay + " seconds.");
                }
            };
            timeWheel.addTask(task, delay);
        }
        timeWheel.start();
    }
}
