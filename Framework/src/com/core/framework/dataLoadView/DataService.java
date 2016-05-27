package com.core.framework.dataLoadView;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by kait on 7/10/13.
 */
public class DataService {

    private static final int DELAY_REMOVE_PRODUCER = 1;

    private Handler mThandler;


    private final Map<String, IProducer> mProducers = new HashMap<String, IProducer>();


    public boolean cancelGetTask(DataRequest request) {
        IProducer producer = mProducers.get(request.getHashKey());

        if (producer != null) {
            return producer.cancel(true);
        }
        return false;
    }

    private static DataService inst ;
    public static DataService getInstance() {
        if(inst==null)inst = new DataService();
        return inst;
    }

    private DataService() {
        // timely remove producers from cache
        HandlerThread thread = new HandlerThread("__timer__");
        thread.start();

    }

    public void submit(DataRequest request) {
        String hashKey = request.getHashKey();
        IProducer producer = null;

        if (null == producer || request.isRenew()) {
        	if(request.getRequester()!=null&&request.getRequester().METHOD_POST.equals(request.getRequester().getMethod())){
        		producer = new HttpPostProducer();
        	}else {
        		producer = new HttpGetProducer();
			}
            producer.submit(request);

        } else {
            producer.submit(request);
        }
    }

}
