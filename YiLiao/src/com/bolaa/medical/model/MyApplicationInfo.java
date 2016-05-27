package com.bolaa.medical.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Dell-1
 * Date: 13-7-1
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class MyApplicationInfo implements Serializable {
    private static final long serialVersionUID = -3166225077376326722L;
    public String name;
    public String packageName;
    public String version;
    public String userId;

    public MyApplicationInfo() {}

    public MyApplicationInfo(JSONObject object) throws JSONException {
        name = object.optString("name");
        packageName = object.optString("packageName");
        version = object.optString("version");
        userId = object.optString("userId");

    }
}
