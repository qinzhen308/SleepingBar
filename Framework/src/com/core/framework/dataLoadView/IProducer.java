package com.core.framework.dataLoadView;

/**
 * Created by kait on 7/10/13.
 */
public interface IProducer {

    public void submit(DataRequest request);

    public boolean cancel(boolean mayInterruptIfRunning);

}
