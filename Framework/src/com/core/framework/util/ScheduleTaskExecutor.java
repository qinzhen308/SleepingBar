package com.core.framework.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: longtc
 * Date: 12-4-24
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleTaskExecutor {

    private ScheduledExecutorService mService;

    public ScheduleTaskExecutor(int poolSize) {
        mService = Executors.newScheduledThreadPool(poolSize);
    }

    public void submitRunnable(Long interval, Runnable runnable) {

        mService.scheduleAtFixedRate(runnable, interval, interval, TimeUnit.MILLISECONDS);
    }

}
