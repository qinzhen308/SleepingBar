package com.core.framework.dataLoadView;

import java.util.concurrent.Future;

import android.os.Handler;

import com.core.framework.develop.LogUtil;
import com.core.framework.util.StringUtil;

/**
 * Created by kait on 7/11/13.
 */

public abstract class AbstractProducer implements IProducer {

    protected ICacher cacher;
    protected DataRequest request;
    private Future mFuture;

    @Override
    public void submit(final DataRequest request) {
        this.request = request;

        // 设置缓存时间
        if (request.getCacheTime() > 0 && null != cacher) {
            if (cacher instanceof HttpCacher)
                ((HttpCacher) cacher).setMaxAge(request.getCacheTime());
        }

        // 如果设置了DisConsumer，预先显示数据

        DataService ds =  DataService.getInstance();
        if (null != request.getDisConsumer() && null != cacher) {
            String result = cacher.getCachedData(request.getHashKey());
            callDisConsumer(request.getDisConsumer(), result, request.getHandler());
        }
        //qjb 采用生产者消费者模式进行并发开发，其中实现Future的主要目的就是为了实现其取消动作
        // 但是用Runable接口实现Task，调用Future的cancel方法是无效的，最好将Runable改成FutureTask，以便实现cancel  个人见解
        mFuture = ThreadManager.getInstance().submitUIThread(new Runnable() {
            @Override
            public void run() {
                IConsumer consumer = request.getConsumer();
                Handler handler = request.getHandler();
                String result = cacher.getCache(request.getHashKey());
                //没缓存数据
                if (result == null || request.isRenew() || request.getDisConsumer() != null) {
                    try {
                        result = produce();
                        if (consumer != null) {//pre load data
                            callConsumer(consumer, result, handler);
                        }
                        if (!StringUtil.isNull(result)) {
                            cacher.cache(request.getHashKey(), result);
                        }
                    } catch (Exception e) {
                        LogUtil.e(e);
                        callConsumerError(consumer, e.getMessage(), e, handler);
                    }
                } else {
                    callConsumer(consumer, result, handler);
                }
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    private void callDisConsumer(final IDisConsumer consumer, final String result, Handler handler) {
        if (null == handler)
            consumer.onCachedDataLoaded(result);
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    consumer.onCachedDataLoaded(result);
                }
            });
    }

    private void callConsumer(final IConsumer consumer, final String result, Handler handler) {
        if (null == handler)
            consumer.onDataResponse(result);
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    consumer.onDataResponse(result);
                }
            });
    }

    private void callConsumerError(final IConsumer consumer, final String message, final Throwable throwable, Handler handler) {
        if (null == handler)
            consumer.onDataError(message, throwable);
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    consumer.onDataError(message, throwable);
                }
            });
    }

    protected abstract String produce() throws Exception;
}
