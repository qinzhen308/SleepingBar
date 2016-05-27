package com.core.framework.net;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.core.framework.develop.LogUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-10-9
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class ListJsonParser<T> implements com.core.framework.net.IJsonParser {

    public void parse(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            List<T> results = doParse(jo);
            onParse(results, jo);
        } catch (JSONException e) {
            LogUtil.w(e);
        }
    }

    public void onError(int status, String message){}

    protected abstract List<T> doParse(JSONObject jo) throws JSONException;

    public abstract void onParse(List<T> results, JSONObject jo);
}
