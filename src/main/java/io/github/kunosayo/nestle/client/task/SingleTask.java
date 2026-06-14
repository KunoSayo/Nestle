package io.github.kunosayo.nestle.client.task;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleTask {

    public static SingleTask INSTANCE = new SingleTask();

    private final AtomicBoolean threadRunning = new AtomicBoolean(false);

    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public void submitTask(Runnable task) {
        tasks.add(task);
        if (!threadRunning.compareAndExchangeRelease(false, true)) {
            Thread.ofPlatform().name("Nestle fetch").start(() ->  {
                while (true) {
                    try {
                        while (!tasks.isEmpty()) {
                            var r = tasks.poll();
                            if (r != null) {
                                r.run();
                            }

                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {

                            }
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                    threadRunning.compareAndExchangeAcquire(true, false);


                    // 1. THIS: found empty
                    // 2. OTHER: add task
                    // 3. OTHER: skip start new virtual thread (running true)
                    // 4. THIS: set false
                    // 5. THIS: tasks is not empty


                    // if tasks is empty, no task add during the value true
                    if (tasks.isEmpty()) {
                        break;
                    }
                    if (threadRunning.compareAndExchangeRelease(false, true)) {
                        break;
                    }
                }
            });
        }
    }

    public void clearTasks() {
        this.tasks.clear();
    }
}
