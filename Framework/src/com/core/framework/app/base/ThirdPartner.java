package com.core.framework.app.base;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 13-2-1
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 * "partner_login_info": {
 # "nick_name": "",
 # "expires_at": "",
 # "access_token": "",
 # "partner_type": 1
 # }
 */

public class ThirdPartner {
    private int mPartnerType;//4,淘宝登陆
    private String mAccessToken;
    private String mNickName;
    private String mExpiresTime;

    public int getPartnerType() {
        return mPartnerType;
    }

    public void setPartnerType(int mPartnerType) {
        this.mPartnerType = mPartnerType;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String mNickName) {
        this.mNickName = mNickName;
    }

    public String getExpiresTime() {
        return mExpiresTime;
    }

    public void setExpiresTime(String mExpiresTime) {
        this.mExpiresTime = mExpiresTime;
    }
}
