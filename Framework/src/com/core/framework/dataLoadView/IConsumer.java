package com.core.framework.dataLoadView;

/**
 * Created by kait on 7/10/13.
 */
public interface IConsumer {

    public void onDataResponse(String data);

    public void onDataError(String msg, Throwable throwable);

}
