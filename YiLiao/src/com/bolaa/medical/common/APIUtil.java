package com.bolaa.medical.common;

import java.util.List;

import org.apache.http.NameValuePair;

import android.text.TextUtils;

import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-5-19
 * Time: 下午4:23
 * To change this template use File | SettingsActivity | File Templates.
 */
public class APIUtil {

    private final static int WORK_CONFIG = 0;
    private final static int TEST_CONFIG = 1;
    private static BaseNetwork mNetwork;

    public static BaseNetwork getNetwork() {
        if (mNetwork == null) {
            init();
        }
        return mNetwork;
    }

    public static void init() {

        int netStatus = AppConfig.NETWORK_PROPERTIES_TEST_ENVIRONMENT;
        if (netStatus == TEST_CONFIG) {
            mNetwork = new TestNetwork();
        } else {
            mNetwork = new OfficialNetwork();
        }
    }

    /**
     * Build a complete request URL base on a request parameter list.
     *
     * @param params
     * @return
     */
    public static String parseGetUrl(List<NameValuePair> params, String path) {
        StringBuilder sBuffer = new StringBuilder();
        if (!TextUtils.isEmpty(path)) {
            sBuffer.append(path);
        }

        if (AppUtil.isEmpty(params))
            return sBuffer.toString();

        int cntParams = params.size();
        for (int i = 0; i < cntParams; i++) {
            NameValuePair param = params.get(i);
            if (i == 0) {
                sBuffer.append("?");
            } else {
                sBuffer.append("&");
            }

            //如果是分类列表的KEY,则直接拼上Value即可，服务器返回的值里面已经拼好了的
            if(ParamBuilder.CATEGORY_QUERY.equals(param.getName())){
                sBuffer.append(param.getValue());
            }else{
                sBuffer.append(param.getName()).append("=").append(android.net.Uri.encode(param.getValue()));
            }

        }
        LogUtil.d("url = " + sBuffer.toString());
        return sBuffer.toString();
    }
    
    public static String parseGetUrlHasMethod(List<NameValuePair> params, String path) {
        StringBuilder sBuffer = new StringBuilder();
        if (!TextUtils.isEmpty(path)) {
            sBuffer.append(path);
        }

        if (AppUtil.isEmpty(params))
            return sBuffer.toString();

        int cntParams = params.size();
        for (int i = 0; i < cntParams; i++) {
            NameValuePair param = params.get(i);
            
            sBuffer.append("&");
            //编码
            sBuffer.append(param.getName()).append("=").append(android.net.Uri.encode(param.getValue()));

        }
        LogUtil.d("url = " + sBuffer.toString());
        return sBuffer.toString();
    }

    /**
     * Build a complete request URL base on a request parameter list.
     *
     * @param params
     * @return
     */
    public static String parseBackJsonValue(List<NameValuePair> params) {
        StringBuilder sBuffer = new StringBuilder("{");
        if (AppUtil.isEmpty(params)) return sBuffer.append("}").toString();

        int cntParams = params.size();
        for (int i = 0; i < cntParams; i++) {
            NameValuePair param = params.get(i);
            sBuffer.append('"')
                    .append(param.getName())
                    .append('"').append(":")
                    .append('"').append(param.getValue()).append('"');

            if (i != cntParams - 1) {
                sBuffer.append(",");
            }
        }

        return sBuffer.append("}").toString();
    }

}