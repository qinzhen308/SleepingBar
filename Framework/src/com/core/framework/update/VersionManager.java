package com.core.framework.update;

import com.core.framework.app.MyApplication;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.update.RemoteStableVersion.Partner;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Kait
 * Date: 11-10-20
 * Time: 上午10:00
 * To change this template use File | Settings | File Templates.
 */
public class VersionManager {

    private static RemoteStableVersion mRemoteVersion;

    /**
     * 比较本地客户端版本和服务器上的版本
     *
     * @return 如果服务器上的版本号大于本地客户端版本号，返回true
     */
    public static boolean versionCodeHasUpdate() {
        boolean result = false;

        mRemoteVersion = null;

        if (getRemoteVersion() != null) {
            mRemoteVersion.loadRemoteFile(AppConfig.REMOTE_VERSION_URL);
            Partner partner = mRemoteVersion.getRemoteVersionInfo();
            if (partner != null) {
                result = compareVersion(MyApplication.getInstance().getVersionCode(), partner.remoteVersionCode);
            }
        }

        return result;
    }

    /**
     *比较本地客户端版本和服务器上的版本
     *
     * @param resultJson 与版本相关的json数据
     * @return 如果服务器上的版本号大于本地客户端版本号，返回true
     */
    public static boolean versionCodeHasUpdate(String resultJson) {
        boolean result = false;

        mRemoteVersion = null;

        if (getRemoteVersion() != null) {
            try {
                mRemoteVersion.parseVersionFileByJSONStr(resultJson);
                Partner partner = mRemoteVersion.getRemoteVersionInfo();
                if (partner != null) {
                    result = compareVersion(MyApplication.getInstance().getVersionCode(), partner.remoteVersionCode);
                }
            } catch (Exception e) {
                LogUtil.w(e);
            }
        }

        return result;
    }

    /**
     *比较指定版本和服务器上的版本
     *
     * @param resultJson 与版本相关的json数据
     * @return 如果服务器上的版本号大于本地客户端版本号，返回true
     */
    public static boolean versionCodeHasUpdate(int versionCode, String resultJson) {
        boolean result = false;

        mRemoteVersion = null;

        if (getRemoteVersion() != null) {
            try {
                mRemoteVersion.parseVersionFileByJSONStr(resultJson);
                Partner partner = mRemoteVersion.getRemoteVersionInfo();
                if (partner != null) {
                    result = compareVersion(versionCode, partner.remoteVersionCode);
                }
            } catch (Exception e) {
                LogUtil.w(e);
            }
        }

        return result;
    }

    /**
     *比较本地客户端版本和服务器上的版本
     *
     * @param softJson 与版本相关的json数据
     * @return 如果服务器上的版本号大于本地客户端版本号，返回true
     */
    public static boolean versionCodeHasUpdate(JSONObject softJson) {
        boolean result = false;

        mRemoteVersion = null;

        if (getRemoteVersion() != null) {
            try {
                mRemoteVersion.parseVersionFileByJSONObject(softJson);
                Partner partner = mRemoteVersion.getRemoteVersionInfo();
                if (partner != null) {
                    result = compareVersion(MyApplication.getInstance().getVersionCode(), partner.remoteVersionCode);
                }
            } catch (Exception e) {
                LogUtil.w(e);
            }
        }

        return result;
    }

    public static RemoteStableVersion getRemoteVersion() {
        if (mRemoteVersion == null) {
            mRemoteVersion = new RemoteStableVersion();
        }

        return mRemoteVersion;
    }

    public static boolean compareVersion(int local, int remote) {
        LogUtil.d("local version: " + local + " ~~~~~~~~~~~ remote version: " + remote);

        return local < remote;
    }

}