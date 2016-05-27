package com.core.framework.net;

import org.apache.http.cookie.Cookie;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 12-10-15
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCookie implements Cookie {

    /**
     * general cookie name
     * @return
     */
    protected abstract String getCookieName();

    /**
     * general cookie value
     * @return
     */
    protected abstract String getCookieValue();

    /**
     * general cookie domain
     * @return
     */
    protected abstract String getCookieDomain();

    @Override
    public String getName() {
        return getCookieName();
    }

    @Override
    public String getValue() {
        return getCookieValue();
    }

    @Override
    public String getDomain() {
        return getCookieDomain();
    }

    @Override
    public String getComment() {
        return null;  
    }

    @Override
    public String getCommentURL() {
        return null;  
    }

    @Override
    public Date getExpiryDate() {
        return null;  
    }

    @Override
    public boolean isPersistent() {
        return false;  
    }

    @Override
    public String getPath() {
        return null;  
    }

    @Override
    public int[] getPorts() {
        return new int[0];  
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public boolean isExpired(Date date) {
        return false;
    }
    
}
