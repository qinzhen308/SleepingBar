package com.core.framework.dataLoadView;

/**
 * Created by kait on 7/10/13.
 */
public interface ICacher {

    public void cache(String key, String data);

    public String getCache(String key);

    public String getCachedData(String key);

}
