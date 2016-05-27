package com.core.framework.dataLoadView;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.beans.DataPollingCache;
import com.core.framework.util.StringUtil;

/**
 * Created by kait on 7/10/13.
 */
public class HttpCacher implements ICacher {

    /**
     * 缓存时间，与server的max-age一起使用
     * 默认为0 ，表示不使用缓存，但是不拒绝server的max-age
     * 如果设置为-1，表示禁止使用缓存，server的max-age也不起作用
     */
    private long mMaxAge;

    private String mLastModified;

    @Override
    public void cache(String key, String data) {
        if (key.length() < 10) return;

        long expireTime = mMaxAge < 1 ? mMaxAge : (System.currentTimeMillis() + mMaxAge);
        try {
            long lm = 0;
            if (!StringUtil.isEmpty(mLastModified))
                lm = DateUtils.parseDate(mLastModified).getTime();

            DataPollingCache.getInstance().save(key, data, lm, expireTime);
        } catch (DateParseException e) {
            LogUtil.w(e);
        }
    }

    @Override
    public String getCache(String key) {
        String result = DataPollingCache.getInstance().load(key);
        if (StringUtil.isEmpty(result))
            return null;
        else
            return result;
    }

    @Override
    public String getCachedData(String key) {
        String result = DataPollingCache.getInstance().load(key, true);
        if (StringUtil.isEmpty(result))
            return null;
        else
            return result;
    }

    public String getLastModified(String key) {
        mLastModified = DataPollingCache.getInstance().getLastModifiedGMT(key);
        return mLastModified;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public void setLastModified(String lastModified) {
        this.mLastModified = lastModified;
    }

    public long getMaxAge(String key) {
        this.mMaxAge = DataPollingCache.getInstance().getExpireTime(key);
        return mMaxAge;
    }

    public long getMaxAge() {
        return mMaxAge;
    }

    public void setMaxAge(long maxAge) {
        this.mMaxAge = maxAge;
    }

}
