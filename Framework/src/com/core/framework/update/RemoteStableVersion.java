package com.core.framework.update;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.util.StringUtil;

import android.util.Xml;

/**
 * Created by IntelliJ IDEA.
 * User: Kait
 * Date: 11-10-20
 * Time: 上午10:00
 * To change this template use File | Settings | File Templates.
 */
public class RemoteStableVersion {

    private Partner mPartner;

    public Partner getRemoteVersionInfo() {
        return mPartner;
    }

    public boolean loadRemoteFile(String url) {
        HttpURLConnection conn = null;
        StringBuffer updateUrl = new StringBuffer(url);
        boolean result = false;
        if ("json".equalsIgnoreCase(AppConfig.REMOTE_DATA_TYPE)) {
            updateUrl.append("product=").append(AppConfig.CLIENT_TAG)
                    .append("&platform=android")
                    .append("&trackid=").append(AppConfig.PARTNER_ID);
        }

        try {
            URL u = new URL(updateUrl.toString());
            conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();

                if ("json".equalsIgnoreCase(AppConfig.REMOTE_DATA_TYPE)) {
                    parseVersionFileByJSONStr(StringUtil.getFromStream(in));
                } else {
                    parseVersionFileByXml(in);
                }

                in.close();
                result = true;
            }
        } catch (Exception e) {
            LogUtil.w(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    void parseVersionFileByJSONObject(JSONObject object) throws Exception {
        mPartner = new Partner();
//        mPartner.id = AppConfig.PARTNER_ID;
        mPartner.appName = AppConfig.CLIENT_TAG;
        mPartner.downloadUrl = object.optString("url");
        mPartner.remoteVersionCode = object.optInt("version");
        mPartner.remoteMinVersionCode = object.optInt("min_version");
        mPartner.description = object.optString("description");
        mPartner.mustUpdate = object.optBoolean("must-update");
        mPartner.minSystemVersion = object.optString("min_system_version");
//        mPartner.isBindDownload = object.optBoolean("bind_download");
    }

    void parseVersionFileByJSONStr(String result) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
//        JSONObject softObject = jsonObject.getJSONObject("soft");

        parseVersionFileByJSONObject(jsonObject);
    }

    private void parseVersionFileByXml(InputStream inStream) throws Exception {
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(inStream, HTTP.UTF_8);

        int event = pullParser.getEventType();
        mPartner = new Partner();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:
                    if ("version".equalsIgnoreCase(pullParser.getName())) {
                        mPartner.appName = pullParser.getAttributeValue("", "app-name");
                        mPartner.remoteVersionCode = Integer.parseInt(pullParser.getAttributeValue("", "current-version"));
                    } else if ("client".equalsIgnoreCase(pullParser.getName())) {
//                        mPartner.id = AppConfig.PARTNER_ID;
                        mPartner.downloadUrl = pullParser.getAttributeValue("", "apkurl");
                        mPartner.description = pullParser.getAttributeValue("", "version-description");
                        mPartner.mustUpdate = "true".equalsIgnoreCase(pullParser.getAttributeValue("", "must-update"));
                    }
                    break;

                case XmlPullParser.END_TAG:
                    break;

                default:
                    break;
            }

            event = pullParser.next();
        }
    }

    public class Partner {
//        public String id;
        public String description;
        public String downloadUrl;
        public String appName;
        public int remoteVersionCode;
        public int remoteMinVersionCode;
        public boolean mustUpdate;
        public String minSystemVersion;
//        public boolean isBindDownload;//绑定下载
    }

}