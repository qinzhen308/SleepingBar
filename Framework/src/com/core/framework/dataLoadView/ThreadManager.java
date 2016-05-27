package com.core.framework.dataLoadView;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * 线程池管理器
 */

public class ThreadManager {

    public static int imageThread=3;//其实是 *2的  load 和cache

    public static int getImageThread() {
        return imageThread;
    }

//  static ThreadManager instance = null;
    private volatile static ThreadManager instance = null;

    //用这种单例效率是低下的  影响性能
//    public static synchronized ThreadManager getInstance() {
//        if (instance == null)
//            instance = new ThreadManager();
//        return instance;
//    }

    //改进版单例实现：
    public static ThreadManager getInstance(){
        if (instance==null){
            synchronized (ThreadManager.class){
                if (instance==null){
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }

    ExecutorService threadPoolForLoadUI;
    private ExecutorService getThreadPool() {
        if (threadPoolForLoadUI == null) {
            threadPoolForLoadUI = Executors.newFixedThreadPool(4, new ThreadFactory() {//qjb 增加线程池中线程数量到4   可满足4个Fragment同时加载数据
                int i=0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread mThread = new Thread(r," su load thread" + i++);
                    mThread.setPriority(Thread.MAX_PRIORITY);
                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    return mThread;
                }
            });
        }
        return threadPoolForLoadUI;
    }


    public Future submitUIThread(Runnable run) {
        return getThreadPool().submit(run);
    }


    @Deprecated
    public void executeUIThread(Runnable run) {
        getThreadPool().execute(run);
    }

}
