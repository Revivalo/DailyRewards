package dev.revivalo.dailyrewards.data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadedQueue {
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final Thread workerThread;
    private volatile boolean running = true;

    public ThreadedQueue() {
        this.workerThread = new Thread(() -> {
            while (running || !taskQueue.isEmpty()) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.workerThread.start();
    }

    public void enqueue(Runnable task) {
        taskQueue.offer(task);
    }

    public void shutdown() {
        running = false;
        workerThread.interrupt();
    }
}