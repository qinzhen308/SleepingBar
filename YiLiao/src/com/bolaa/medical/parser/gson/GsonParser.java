package com.bolaa.medical.parser.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.james.mime4j.codec.EncoderUtil;
import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.core.framework.develop.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tencent.bugly.proguard.ba;

public class GsonParser {
	private static GsonParser instance;
	
	public GsonParser() {
		// TODO Auto-generated constructor stub
		
	}
	
	public static GsonParser getInstance(){
		if(instance==null){
			synchronized (GsonParser.class) {
				if(instance==null){
					instance=new GsonParser();
				}
			}
		}
		return instance;
	}
	
	public BaseObjectList parseToObj4List(String json,Class clazz){
		BaseObjectList obj=null;
		try {
			if(json==null)return null;
			
			Gson gson=new Gson();
			
//		Response response=gson.fromJson(json2, Response.class);
			Type type=getType(BaseObjectList.class, clazz);
			obj = gson.fromJson(json, type);
			HApplication.getInstance().saveToken(obj.token);
			if(obj!=null&&obj.status==BaseObject.STATUS_EXPIRE){
				AppStatic.getInstance().clearLoginStatus();
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			LogUtil.w(e);
			try {
				return checkResponseStateForList(json);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				LogUtil.w(e1);
			}	catch (GsonParseException e2) {
				// TODO Auto-generated catch block
				LogUtil.w(e2);
			}
			
		}
		return obj;
	}
	
	
	public BaseObject parseToObj(String json,Class clazz){
		
		BaseObject obj=null;
		try {
			if(json==null)return null;
			
			Gson gson=new Gson();
//			Response response=gson.fromJson(json2, Response.class);
			Type type=getType(BaseObject.class, clazz);
			obj=gson.fromJson(json, type);
			HApplication.getInstance().saveToken(obj.token);
			if(obj!=null&&obj.status==BaseObject.STATUS_EXPIRE){
				AppStatic.getInstance().clearLoginStatus();
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			LogUtil.w(e);
			try {
				return checkResponseState(json);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				LogUtil.w(e1);
			}	catch (GsonParseException e2) {
				// TODO Auto-generated catch block
				LogUtil.w(e2);
			}
			
		}
		return obj;

	}
	
	/*public BeanWraper parseToWrapper(String json,Class clazz){
		
		BeanWraper wraper=null;
		try {
			if(json==null)return null;
			
			JSONObject obj=new JSONObject(json);
			int state=obj.optInt("status");
			String token=obj.optString("token");
			HApplication.getInstance().saveToken(token);
			if(state==BaseObject.STATUS_OK){
				Gson gson=new Gson();
				Type type=getType(BeanWraper.class, clazz);
				String content=obj.optString("data");
				wraper=gson.fromJson(content, type);
			}
			
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			LogUtil.w(e);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wraper;

	}*/
	
	public Object parseToWrapper(String json,Class clazz){
		
		BaseObject obj=null;
		try {
			if(json==null)return null;
			
			Gson gson=new Gson();
//			Response response=gson.fromJson(json2, Response.class);
			Type type=getType(BaseObject.class, clazz);
			obj=gson.fromJson(json, type);
			HApplication.getInstance().saveToken(obj.token);
			if(obj!=null){
				if(obj.status==BaseObject.STATUS_EXPIRE){
					AppStatic.getInstance().clearLoginStatus();
				}
				return obj.data;
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			LogUtil.w(e);
			try {
				//目前没有作用
				checkResponseState(json);
				return null;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				LogUtil.w(e1);
			}	catch (GsonParseException e2) {
				// TODO Auto-generated catch block
				LogUtil.w(e2);
			}
			
		}
		return null;

	}
	
	
	
	
	private ParameterizedType getType(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
	
	private BaseObject<Object> checkResponseState(String json)throws JSONException,GsonParseException{
			JSONObject jsonObject=new JSONObject(json);
			int state=jsonObject.optInt("status");
			if(state==BaseObject.STATUS_EXPIRE){
				AppStatic.getInstance().clearLoginStatus();
			}
			String msg=jsonObject.optString("msg");
			String token=jsonObject.optString("token");
			HApplication.getInstance().saveToken(token);
			BaseObject baseObject=new BaseObject();
			baseObject.msg=msg;
			baseObject.status=state;
			if(baseObject!=null){
				return baseObject;
			}else {
				throw new GsonParseException(msg);
			}
	}
	
	private BaseObjectList<Object> checkResponseStateForList(String json)throws JSONException,GsonParseException{
		JSONObject jsonObject=new JSONObject(json);
		int state=jsonObject.optInt("status");
		if(state==BaseObject.STATUS_EXPIRE){
			AppStatic.getInstance().clearLoginStatus();
		}
		String msg=jsonObject.optString("msg");
		String token=jsonObject.optString("token");
		HApplication.getInstance().saveToken(token);
		BaseObjectList baseObject=new BaseObjectList();
		baseObject.msg=msg;
		baseObject.status=state;
		if(baseObject!=null){
			return baseObject;
		}else {
			throw new GsonParseException(msg);
		}
}

}
