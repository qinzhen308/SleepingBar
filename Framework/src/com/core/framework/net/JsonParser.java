package com.core.framework.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.core.framework.develop.LogUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-5-10
 * Time: 下午2:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class JsonParser<T> implements IJsonParser {

    public void parse(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            T result = doParse(jo);
            onParse(result, jo);
        } catch (JSONException e) {
            LogUtil.w(e);
        }
    }

    public void onError(int status, String message){}

    protected abstract T doParse(JSONObject jo) throws JSONException;

    public abstract void onParse(T result, JSONObject jo);
}
