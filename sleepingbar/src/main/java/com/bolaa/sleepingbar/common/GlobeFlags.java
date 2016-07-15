package com.bolaa.sleepingbar.common;

public class GlobeFlags {
	
	public static final String RECOM_UPDATE_TIME="flag_recom_update_time";
	public static final String MODE_STATUS="flag_list_mode";
	
    public static final String CITY_ID = "cityid";

    public static final String CITY_NAME = "cityname";

    public static final String LAT_HISTORY = "lat_history";

    public static final String LNG_HISTORY = "lng_history";
    
    public static final String DELETE_OLD_CACHE = "delete_old_cache";
    
    
    public static final String NEW_USER_CHECK = "new_user_check"; //新用户判断
    public static final String OLD_USER_FLAG = "-1"; //老用户标示
    public static final String UPDATE_USER_SIGN_MOVE_TIP = "update_user_sign_move_tip"; //老用户标示
    public static final String NEW_USER_INTEGRATION_TIP = "new_user_integration_tip"; //全新用户积分用处提示
    public static final String NO_UPDATE_NOTICE_TAG = "no_update_notice_tag"; //是否再显示更新提示
	
	//列表的显示模式：宫格、列表、瀑布流
    public static final int MODE_BIG_PIC_MODE = 0;
    public static final int MODE_LIST_MODE = 1;
    public static final int MODE_WATER_MODE = 2;
    
    
    
    //MainActivity 框架 tab 索引
    public static final int TAB_HOME = 0;
    public static final int TAB_CATEGORY = 1;
    public static final int TAB_SHOPPING_CART = 2;
    public static final int TAB_FISHION = 3;
    public static final int TAB_USER_CENTER = 4;
    public static final int TAB_ERROR = -1;
    
    //intent参数的key
    public static final String FLAG_FISHION_WAP_URL="flag_fishion_wap_url";
    public static final String FLAG_FISHION_PIC_URL="flag_fishion_pic_url";
    public static final String FLAG_PUSH_ID="flag_push_id";
    public static final String FLAG_PUSH_EXTRA="flag_push_extra";



    public static final String FLAG_PUSH_REGISTION_ID="flag_push_registion_id";


}
