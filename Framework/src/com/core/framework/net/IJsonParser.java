package com.core.framework.net;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-10-9
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
public interface IJsonParser {
    public void parse(String json);
    public void onError(int status, String message);
}
