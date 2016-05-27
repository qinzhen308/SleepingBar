package com.bolaa.medical.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 13-4-9
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public class City implements Serializable {
    private static final long serialVersionUID = -3166225077376326722L;

    public String longitude;

    public String latitude;

    public String id;

    public String name;

    public String pinyin;

    public City () {}

    public City (String id) {
        this.id = id;
    }

    public City (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public City (JSONObject object) throws JSONException {
        id = object.optString("id");
        name = object.optString("name");
        pinyin = object.optString("pinyin");
        latitude = object.optString("latitude");
        longitude = object.optString("longitude");
    }
}
