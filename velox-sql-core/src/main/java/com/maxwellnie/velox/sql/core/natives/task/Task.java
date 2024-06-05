package com.maxwellnie.velox.sql.core.natives.task;

/**
 * @author Maxwell Nie
 */
public class Task implements Runnable{
    private final Object lock = new Object();
    private Runnable task;

    public Task(Runnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            task.run();
        }
    }
    public Object getLock(){
        return lock;
    }
}
