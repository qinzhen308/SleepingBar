package com.core.framework.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.james.mime4j.util.CharsetUtil;

import com.core.framework.app.MyApplication;
import com.core.framework.app.devInfo.DeviceInfo;
import com.core.framework.app.oSinfo.AppConfig;

import android.text.TextUtils;

/**
 * Created with IntelliJ IDEA. User: kait Date: 12-11-13 Time: 下午2:41 To change
 * this template use File | Settings | File Templates.
 */
public class HttpRequester {

	private boolean mSaveHeaders;

	private Header[] mResponseHeaders;

	private List<AbstractCookie> mCookies;

	private String mStringBody;

	public Map<String, Object> mParams;

	private Map<String, String> mRequestHeaders;

	private String method = METHOD_GET;
	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";

	public HttpRequester() {
		mParams = new HashMap<String, Object>();
		mParams.put("access_token", NetworkWorker.getInstance().ACCESS_TOKEN);
		mParams.put("app_token", "6HDm4jAUv4w5W-ycjAvZFo630qHrXJS60yNKT1r");
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setSaveHeaders(boolean save) {
		mSaveHeaders = save;
	}

	public boolean isSaveHeaders() {
		return mSaveHeaders;
	}

	public void setStringBody(String stringBody) {
		mStringBody = stringBody;
	}

	public void setParams(Map<String, Object> params) {
		mParams = params;
	}

	public Map<String, Object> getParams() {
		if (mParams == null) {
			mParams = new HashMap<String, Object>();
		}

		return mParams;
	}

	public Header[] getResponseHeaders() {
		return mResponseHeaders;
	}

	public void setResponseHeaders(Header[] resHeaders) {
		mResponseHeaders = resHeaders;
	}

	public HttpRequester addRequestHeader(String key, String value) {
		if (mRequestHeaders == null) {
			mRequestHeaders = new HashMap<String, String>();
		}

		mRequestHeaders.put(key, value);

		return this;
	}

	public void setRequestHeaders(Map<String, String> headers) {
		if (mRequestHeaders == null) {
			mRequestHeaders = headers;
		} else {
			mRequestHeaders.putAll(headers);
		}
	}

	public Map<String, String> getRequestHeaders() {
		if (mRequestHeaders == null) {
			mRequestHeaders = new HashMap<String, String>();
		}

		return mRequestHeaders;
	}

	public void setCookie(AbstractCookie cookie) {
		if (mCookies == null) {
			mCookies = new ArrayList<AbstractCookie>();
		}
		if (mCookies.contains(cookie)) {
			mCookies.remove(cookie);
		}
		mCookies.add(cookie);
	}

	public void setCookies(List<AbstractCookie> cookies) {
		if (mCookies != null) {
			mCookies.addAll(cookies);
		} else {
			mCookies = cookies;
		}
	}

	/**
	 * 构建HTTP请求的header信息，如果是POST请求，则添加消息body
	 * 
	 * @param client
	 * @param request
	 */
	public final void handlerHttpHeader(DefaultHttpClient client, HttpUriRequest request) throws Exception {
		if (request == null)
			return;

		if (mCookies != null) {
			CookieStore cookie = new BasicCookieStore();
			for (AbstractCookie ac : mCookies) {
				cookie.addCookie(ac);
			}
			client.setCookieStore(cookie);
		}

		String method = request.getMethod();
		if (mRequestHeaders != null) {
			for (String key : mRequestHeaders.keySet()) {
				request.addHeader(key, mRequestHeaders.get(key));
			}
		}

		addUserAgent(request);
		addPlatformMsg(request);
		// 所有请求都增加上 out计数 和 90天filter数
		addOutFilter(request);

		if ("GET".equals(method)) {
			addGzipForGet(request);
		} else if ("POST".equals(method)) {
			addMultipartEntityToHttpPost((HttpPost) request, mParams);
			addStringEntityToHttpPost((HttpPost) request, mStringBody);
		}
	}

	private void addGzipForGet(HttpUriRequest request) {
		request.addHeader("Accept-Encoding", "gzip");
	}

	private void addPlatformMsg(HttpUriRequest request) {
		request.addHeader("X-Tuan800-Platform", "Android");
	}

	private void addUserAgent(HttpUriRequest request) {
		StringBuilder sb = new StringBuilder("tbbz|");
		sb.append(AppConfig.CLIENT_TAG).append("|").append(DeviceInfo.getDeviceId()).append("|").append("Android")
				.append("|").append(MyApplication.getInstance().getVersionName()).append("|")
				.append(AppConfig.PARTNER_ID);
		request.addHeader("User-Agent", sb.toString());
	}

	private void addOutFilter(HttpUriRequest request) {
		// 添加header
	}

	private void addStringEntityToHttpPost(HttpPost m, String body) throws UnsupportedEncodingException {
		if (TextUtils.isEmpty(body))
			return;
		m.setEntity(new StringEntity(body, HTTP.UTF_8));
	}

	private void addMultipartEntityToHttpPost(HttpPost m, Map<String, Object> params)
			throws UnsupportedEncodingException {
		if (params == null || params.isEmpty())
			return;

		MultipartEntity mpEntity = new MultipartEntity();
		List<NameValuePair> lstNVPs = new ArrayList<NameValuePair>();
		StringBody stringPar;
		FileBody filePar = null;

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() instanceof String) {
				lstNVPs.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
				stringPar = new StringBody((String) entry.getValue(), CharsetUtil.getCharset(HTTP.UTF_8));
				mpEntity.addPart(entry.getKey(), stringPar);
			} else if (entry.getValue() instanceof File) {
				filePar = new FileBody((File) entry.getValue());
				mpEntity.addPart(entry.getKey(), filePar);
			}else if(entry.getValue() instanceof byte[]){
				ByteArrayBody byteArrayBody=new ByteArrayBody((byte[]) entry.getValue(),entry.getKey());
				mpEntity.addPart(entry.getKey(), byteArrayBody);
			}
		}

		// modified by tyl for the server's content that is file
		if (filePar == null) {
			m.setEntity(new UrlEncodedFormEntity(lstNVPs, HTTP.UTF_8));
		} else {
			m.setEntity(mpEntity);
		}
	}

}
