package com.core.framework.app.base;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 12-10-15
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 *
 * If you want expand user,you must extends the class
 */
public class BaseUser implements Serializable{
    public  boolean isLogin;
    private boolean isAutoLogin;
    private boolean isActive;//是否激活
    private String id;
    private String name;
    private String password;
    private String accessToken;
    private String phoneNumber;
    private String mEmail;
    private ThirdPartner mPartner;



    private String inviteCode;
    private String image;





    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public  boolean isLogin() {
        return isLogin;
    }

    public  void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        isAutoLogin = autoLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public ThirdPartner getPartner() {
        return mPartner;
    }

    public void setPartner(ThirdPartner mPartner) {
        this.mPartner = mPartner;
    }




}
