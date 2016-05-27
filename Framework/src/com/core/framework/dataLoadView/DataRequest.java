package com.core.framework.dataLoadView;

import android.os.Handler;
import android.os.Looper;

import com.core.framework.net.HttpRequester;

/**
 * Created by kait on 7/10/13.
 */
public class DataRequest {

    public static final long DEFAULT_IN_MEMORY_TIME = 180000;
    public static final long PRE_LOAD_IN_MEMORY_TIME = 300000;

    private Object[] params;
    private IConsumer consumer;
    private IDisConsumer disConsumer;
    private HttpRequester httpRequester;

    private Handler handler;
    private boolean renew;
    private long cacheTime;
    private long keepInMemoryTime = DEFAULT_IN_MEMORY_TIME;
    
    

    protected DataRequest() {}

    public static DataRequest create() {
        return new DataRequest();
    }

    public Object[] getParams() {
        return params;
    }
    
    public HttpRequester getRequester() {
        return httpRequester;
    }

    public IConsumer getConsumer() {
        return consumer;
    }

    public IDisConsumer getDisConsumer() {
        return disConsumer;
    }

    public Handler getHandler() {
        return handler;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public long getKeepInMemoryTime() {
        return keepInMemoryTime;
    }

    public boolean isRenew() {
        return renew;
    }

    public void submit() {

        // Need handler to deal with multiple thread
        if (null != consumer && null == handler) {
            if (null != Looper.getMainLooper() && Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
                handler = new Handler(Looper.getMainLooper());
            } else if (null != Looper.myLooper()) {
                handler = new Handler(Looper.myLooper());
            } else {
                throw new RuntimeException("DataRequest need a handler thread, please start looper first.");
            }
        }

        if (null == consumer) {
            // pre load data
            keepInMemoryTime = PRE_LOAD_IN_MEMORY_TIME;
        }

         DataService.getInstance().submit(this);
    }

    /**
     * 设置请求参数
     * @param params
     * @return
     */
    public DataRequest setParams(Object... params) {
        this.params = params;
        return this;
    }

    public DataRequest setRequester(HttpRequester requester) {
        this.httpRequester = requester;
        return this;
    }

    /**
     * 设置数据消费者
     * @param consumer
     * @return
     */
    public DataRequest setConsumer(IConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public DataRequest setDisConsumer(IDisConsumer disConsumer) {
        this.disConsumer = disConsumer;
        return this;
    }

    /**
     * 设置数据消费者方法执行的线程handler
     * @param handler
     * @return
     */
    public DataRequest setHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * 设置缓存时间，以毫秒为单位
     * @return
     */
    public DataRequest setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    /**
     * 设置数据在内存中缓存的时间，默认为3分钟
     * @param keepInMemoryTime
     * @return
     */
    public DataRequest setKeepInMemoryTime(long keepInMemoryTime) {
        this.keepInMemoryTime = keepInMemoryTime;
        return this;
    }

    /**
     * 强制刷新,更新缓存
     */
    public void renew() {
        DataRequest newReq = new DataRequest();
        newReq.consumer = consumer;
        newReq.params = params;
        newReq.handler = handler;
        newReq.httpRequester = httpRequester;
        newReq.cacheTime = cacheTime;
        newReq.renew = true;
        newReq.keepInMemoryTime = keepInMemoryTime;
        newReq.submit();
    }

    public String getHashKey() {
        StringBuilder sb = new StringBuilder();
        if (null != params && params.length > 0) {
            sb.append("@");
            for (Object p: params)
                sb.append(p.toString());
        }

        return sb.toString();
    }

}
