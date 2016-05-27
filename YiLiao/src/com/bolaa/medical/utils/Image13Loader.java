package com.bolaa.medical.utils;

import com.core.framework.R;
import com.core.framework.app.MyApplication;

public class Image13Loader extends com.core.framework.image.image13.Image13lLoader{
	
	private static Image13Loader inst ;
    public static Image13Loader getInstance() {
        if(inst==null) {
            inst = new Image13Loader();
            inst.initImageLoader(MyApplication.getInstance(), R.drawable.img_list_default, R.drawable.img_list_default,
                    R.drawable.img_list_default, R.drawable.img_list_default);
        }
        return inst;
    }

}
