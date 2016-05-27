package com.core.framework.app;

/**
 * Created by qz on 2014/8/8.
 */
public class AppSetting {
    public static final int LOG_CLOSED = 0;// 0 展示log日常使用   1线上环境
    public static final int IM_TEST = 0;    //im配置开关 ，;0正式环境，2开发，1测试使用
    public static final int H5_TEST = 0; //h5打开测试页面，0正式环境
    public static final int PROMOTION_CLOSED = 0;//测试用 强制关掉大促 正式环境设置成0
    public static final int DEV_TEST_SWITCH = 0; //测试使用直接改变device ID


    public static final int LOG_ERR_FEED = 1;//1回传日志  0不会传
    public static final int LOG_ERR_SAVE = 0;//1日志本地保存
    public static final long POLLING_INTERVAL = 7200000;
    public static final int SHOW_TIANMAO_ICON = 0;//0 显示 1不显示 注:这个不要修改,恒为0.
    public static final int SALES_COUNT_SHOW_WAN= 1;// 0:不显示万 该多少就多少 ; 1:显示万 销量超过1w就显示x.x万
    public static final int SHOW_COVERED_GUIDE = 1;// 是否显示覆盖安装的新手引导, 1 显示,0 不显示.
    @Deprecated //建议不使用
    public static final int NETWORK_PROPERTIES_TEST_ENVIRONMENT =0;//0 为正式,其他都是测试环境

    public static final int IS_ADD_USER_IDENTITY_INFO_TO_DEAL_LIST = 1; //列表数据请求的时候是否添加身份信息   0：不添加  ,   1：添加

    public static final long REFRESH_INTERVAL=1000*60*5;//列表刷新的间隔

    public static  int RELIC_SWITCH = 0;//1打开    0关闭
    public static  int SHOW_NET_LOC_SWITCH = 0;//1打开   工信部  ， 0关闭
    public static  int SZLM_O_SWITCH = 0;//1打开  数字联盟  ， 0关闭
    public static  int TELECOM_SWITCH = 0;//1打开 电信返积分 ， 0关闭
    public static  int OFFLINE_SWITCH = 0; // 0 关闭   线下预制， 1 打开
    public static  int WIDGET_SWITCH = 0; // 小部件, 0 关闭， 1 打开
    public static  int IM_SWITCH = 1; // im 0 关闭， 1 打开
    public static  int MI_SWITCH = 1; //小米push 0 关闭， 1 打开
    public static  int JIDIAO_SWITCH = 0; //基调 0 关闭， 1 打开
    //@800mi#%add%#     //@800xgpush#%add%#
    public static  int PUSH_SDK_TYPE = 1; //push相关sdk的类型: 1 小米， 2 信鸽
    //@800mi%end%#      //@800xgpush%end%#
    public static  int IFLYTEK_SWITCH = 0; //科大讯飞 0 关闭， 1 打开

}
