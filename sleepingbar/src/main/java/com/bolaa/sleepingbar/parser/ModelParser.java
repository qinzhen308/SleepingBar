package com.bolaa.sleepingbar.parser;
/*package com.bolaa.sleepingbar.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.core.framework.develop.LogUtil;
import com.bolaa.sleepingbar.model.BeanWraper;
import com.bolaa.sleepingbar.model.City;
import com.bolaa.sleepingbar.utils.AppUtil;

*//**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 13-1-16
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 *//*
public class ModelParser {
    public static final int PARSE_BANNER = 108;
    public static final int PARSE_CITY = 111;

    public static final int PARSE_GOODINFO = 150;

    public static final String TRADES = "trades";
    public static final String ORDERS = "orders";
    public static final String OBJECTS = "objects";
    public static final String SCORE = "score_histories";
    public static final String SELLING_DEAL = "deals";
    public static final String DEALS = "deals";
    public static final String SHOPS = "shops";
    public static final String SCORE_ACCOUNTS = "score_accounts";
    public static final String AUCTION_RECORD = "item";
    public static final String DATA = "data";
    public static final String PRIZE = "prize";


    public static String LIST_COUNT;
    public static String SELLING_DEAL_COUNT;//正在热卖
    public static String TO_SELLING_DEAL_COUNT;//即将开始

    public static <T> T parseAsJSONArray(String response, int parseType) {
        ArrayList<Object> objects = new ArrayList<Object>();
        JSONArray jsonArray = null;
        //将JSONObject对应的str转换成JSONArray对应的str
        try {
            if (!response.startsWith("[")) {
                StringBuilder sb = new StringBuilder();
                sb.append("[").append(response).append("]");
                response = sb.toString();
            }

            jsonArray = new JSONArray(response);
        } catch (Exception e) {
            LogUtil.w(e);
        }

        //对JSONArray 遍历解析
        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Object object = null;
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }

                    switch (parseType) {
                        case PARSE_CITY:
                            object = new City(jsonObject);
                            break;

                        
                        default:
                            break;
                    }
                    //将所有解析到的object对象加入到objects中，
                    if (object != null) {
                        objects.add(object);
                    }
                } catch (JSONException e) {
                    LogUtil.w(e);
                }
            }
        }
        return (T) objects;
    }

    public static <T> T parseAsJSONObject(String response, int parseType, String parseKey) {
        if (AppUtil.isNull(response)) return null;
        ArrayList<Object> objects = new ArrayList<Object>();
        JSONObject jObj;
        JSONArray jsonArray = null;

        try {
            if (!response.startsWith("{")) {
                StringBuilder sb = new StringBuilder();
                sb.append("{").append(response).append("}");
                response = sb.toString();
            }

            jObj = new JSONObject(response);
            if (jObj.has("count")) {
                LIST_COUNT = jObj.optString("count");
            }

            if (jObj.has("sellingCount")) {
                SELLING_DEAL_COUNT = jObj.optString("sellingCount");
            }

            if (jObj.has("toSellCount")) {
                TO_SELLING_DEAL_COUNT = jObj.optString("toSellCount");
            }

            if (jObj.has("meta")) {
                JSONObject object = jObj.optJSONObject("meta");
                if (object.has("version")) {
//                    Tao800Application.exposeVersion = object.optString("version");

                }
            }

            jsonArray = jObj.optJSONArray(parseKey);
        } catch (Exception e) {
            LogUtil.w(e);
        }

        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Object object = null;
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }

                    switch (parseType) {
                      	
                        default:
                            break;
                    }

                    if (object != null) {
                        objects.add(object);
                    }
                } catch (JSONException e) {
                    LogUtil.w(e);
                }
            }
        }
        return (T) objects;
    }

    public static BeanWraper parseAsWrapperArray(String response, int parseType) {
        BeanWraper beanWraper = new BeanWraper(BeanWraper.BEAN_ARRAY_TYPE);

        ArrayList<Object> objects = new ArrayList<Object>();
        JSONArray jsonArray = null;

        try {
            if (!response.startsWith("[")) {
                StringBuilder sb = new StringBuilder();
                sb.append("[").append(response).append("]");
                response = sb.toString();
            }

            jsonArray = new JSONArray(response);
        } catch (Exception e) {
            LogUtil.w(e);
        }

        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Object object = null;
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }

                    switch (parseType) {
                        case PARSE_CITY:
                            object = new City(jsonObject);
                            break;

                        default:
                            break;
                    }

                    if (object != null) {
                        beanWraper.allBeans.add(object);
                    }
                } catch (JSONException e) {
                    LogUtil.w(e);
                }
            }
        }
        return beanWraper;
    }

    public static BeanWraper parseAsWraperObject(String response, int parseType, String parseKey) {
        BeanWraper beanWraper = new BeanWraper(0);
        if (AppUtil.isNull(response)) return beanWraper;
        // ArrayList<Object> objects = new ArrayList<Object>();
        JSONObject jObj;
        JSONArray jsonArray = null;

        try {
            if (!response.startsWith("{")) {
                StringBuilder sb = new StringBuilder();
                sb.append("{").append(response).append("}");
                response = sb.toString();
            }

            jObj = new JSONObject(response);
            if (jObj.has("count")) {
                LIST_COUNT = jObj.optString("count");
            }

            if (jObj.has("sellingCount")) {
                SELLING_DEAL_COUNT = jObj.optString("sellingCount");
            }

            if (jObj.has("toSellCount")) {
                TO_SELLING_DEAL_COUNT = jObj.optString("toSellCount");
            }

            if (jObj.has("status")) {
                beanWraper.userStatus = jObj.optInt("status");
            }

            if (jObj.has("has_next")) {   // 收藏接口不带meta
                beanWraper.hasNext = jObj.optBoolean("has_next") ? 1 : 0;
            }

            if (jObj.has("hasNext")) {   // 支付相关记录列表
                beanWraper.hasNext = jObj.optBoolean("hasNext") ? 1 : 0;
            }



            if (jObj.has("meta")) {
                JSONObject object = jObj.optJSONObject("meta");
                if (object.has("version")) {
//                    Tao800Application.exposeVersion = object.optString("version");
                    beanWraper.exposeVersion = object.optString("version");
                }

                if (object.has("has_next")) {
                    beanWraper.hasNext = object.optBoolean("has_next") ? 1 : 0;
                }

                if (object.has("count")){
                    beanWraper.count = object.optInt("count");
                }
            }

            jsonArray = jObj.optJSONArray(parseKey);
        } catch (Exception e) {
            LogUtil.w(e);
        }

        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Object object = null;
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }

                    switch (parseType) {
                        

                        default:
                            break;
                    }

                    if (object != null) {
                        beanWraper.allBeans.add(object);
                    }
                } catch (JSONException e) {
                    LogUtil.w(e);
                }
            }
        }
        return beanWraper;
    }
}
*/