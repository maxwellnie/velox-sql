package com.maxwellnie.velox.sql.core.utils.base;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统时钟工具类，解决System.currentTimeMillis()被高并发调用时会出现速度下降的问题。<br/>
 * 每1ms获取一次当前时间，在下一个1ms内的时间为在上一个1ms的时间。
 * <br/>
 * <p>高并发问题原因如下：</p>
 * <p>System.currentTimeMillis()是通过调用操作系统的系统调用gettimeofday()方法，多线程下会导致调用频繁影响性能。</p>
 *
 * @author Maxwell Nie
 */
public class SystemClock {
    private static final SystemClock INSTANCE = new SystemClock(1);
    /*
     * 1毫秒周期
     */
    private final int period;
    /**
     * 当前时间
     */
    private final AtomicLong NOW;

    public SystemClock(int period) {
        this.period = period;
        this.NOW = new AtomicLong(System.currentTimeMillis());
        scheduleClock();
    }

    public static SystemClock getClock() {
        return INSTANCE;
    }

    public static long now() {
        return INSTANCE.NOW.get();
    }

    private void scheduleClock() {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            // 线程名
            Thread thread = new Thread(r, "velox.sql.system.clock");
            // 守护线程
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> {
            this.NOW.set(System.currentTimeMillis());
        }, this.period, this.period, java.util.concurrent.TimeUnit.MILLISECONDS);
        this.NOW.set(System.currentTimeMillis());
    }

    public long currentTimeMillis() {
        return this.NOW.get();
    }
}
