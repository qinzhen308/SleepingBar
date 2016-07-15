package com.core.framework.app.oSinfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.res.AssetManager;

import com.core.framework.app.AppSetting;
import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-4-11
 * Time: 上午10:41
 * To change this template use File | Settings | File Templates.
 * <p/>
 * DO NOT change the static variables!
 */

//配置参数  主要是app运行的一些固定参数
//
public class AppConfig {


	public static final String APP_TOKEN="6HDm4jAUv4w5W-ycjAvZFo630qHrXJS60yNKT1r";
	
    public static final String APP_ID="wx1f95ed8882ca2418";
    public static final String APP_SECRET="773a8cc792c0038aa278081b90577875";

    // tag
    public static final String CLIENT_TAG = "sleepingbar";
    public static final String PRODUCT_TAG = "sleepingbar";
    public static final String PLATFORM_TAG = "APad";

    // partner
    //public static String PARTNER_ID = "0b2e3f";
    public static String PARTNER_ID = "6x0eae";
    //public static String PARTNER_ID = "b2e1cc";
    public static String SHOW_ZIFEI = "0";
    public static String UMENG = "0";

    // update
    public static final String REMOTE_DATA_TYPE = "json";

    // log
    public static final String LOG_TAG = "haiyuehui_android";

    public static final boolean LOG_ERR_FEED = AppSetting.LOG_ERR_FEED==1;
    public static final boolean LOG_CLOSED = AppSetting.LOG_CLOSED==1;
    public static final boolean LOG_ERR_SAVE = AppSetting.LOG_ERR_SAVE==1;

    // database
    public static String DEFAULT_DATABASE = "haiyuehui.db";

    // analytics
    private static final String ANALYTICS_LOG_URL = "http://api.tuan800.com/mobilelog/applog/mobilelog";
    private static final String NEW_ANALYTICS_LOG_URL = "http://api.tuan800.com/mobilelog/normal/report";

    private static final Map<String, String> uiMap;

    static {
        uiMap = new HashMap<String, String>();
        uiMap.put("com.boju.hiyo.activities.SplashActivity", "launch");
        uiMap.put("com.boju.hiyo.activities.ZhiCategoryActivity", "guang");
    }

    private static final Map<String, String> eventsMap;

    static {
        eventsMap = new HashMap<String, String>();
        eventsMap.put("ui_resume", "r");
        eventsMap.put("ui_pause", "p");
    }

    // pay
    public static String ALIPAY_PLUGIN_NAME = "alipay_plugin.apk";

    // pay-online
    public static String PAY_CREATE_URL_ONLINE = "http://buy.m.zhe800.com/orders/credits/create?";
    public static String PAY_URL_ONLINE = "http://buy.m.zhe800.com/orders/credits/pay?";

    // pay-test
    public static String PAY_CREATE_URL_TEST = "http://buy.m.xiongmaoz.com/orders/credits/create";
    public static String PAY_URL_TEST = "http://buy.m.xiongmaoz.com/orders/credits/pay";

    public static String PAY_CREATE_URL = PAY_CREATE_URL_ONLINE;
    public static String PAY_URL = PAY_URL_ONLINE;

    // polling
    public static final long POLLING_INTERVAL =AppSetting.POLLING_INTERVAL;
    public static final String ALARM_POLLING_NAME = "com.boju.hiyo.polling.PushPolling";

    public static final String INTEGRAL_API_KEY = "A6E03C34D31BB1CD789646FB0C310CCD";

    // network-properties
    // #0 为正式,其他都是测试环境
    public static final int NETWORK_PROPERTIES_TEST_ENVIRONMENT = AppSetting.NETWORK_PROPERTIES_TEST_ENVIRONMENT;


    public static String REMOTE_VERSION_URL="";

    private AppConfig() {}

    public static void init() {
        AssetManager am = MyApplication.getInstance().getAssets();
        try {
            // readConfigXml(am);
            readPartnerXml(am);
        } catch (IOException e) {
            LogUtil.w(e);
        }

    }

    private static void readPartnerXml(AssetManager am) throws IOException {
        InputStream in = null;
        try {
            in = am.open("partner.xml");
            SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            sp.parse(in, new PartnerXmlHandler());
        } catch (IOException e) {
            LogUtil.w(e);
        } catch (Exception e) {
            LogUtil.w(e);
        } finally {
            if (null != in) in.close();
        }
    }

    private static class PartnerXmlHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("partner".equalsIgnoreCase(localName)) {
                PARTNER_ID = StringUtil.getValueOrDefault(attributes.getValue("id"), PARTNER_ID);
                SHOW_ZIFEI = StringUtil.getValueOrDefault(attributes.getValue("zifei"), SHOW_ZIFEI);
                UMENG = StringUtil.getValueOrDefault(attributes.getValue("umeng"), UMENG);
            }
        }
    }

    /**
     * 可能有外部包需要解析config.xml，使用该接口解析
     */
    public interface IExternalSaxParser {
        public void parseTag(String uri, String localName, String name, Attributes attributes);
    }
}