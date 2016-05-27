package com.core.framework.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.core.framework.app.MyApplication;
import com.core.framework.auth.SSLCustomSocketFactory;
import com.core.framework.callbacks.CommCallBack;
import com.core.framework.develop.LogUtil;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA. User: chenjishi Date: 11-4-11 Time: 上午10:20 To
 * change this template use File | Settings | File Templates.
 */
public class NetworkWorker {
	public String ACCESS_TOKEN = "";// 用户token-登录之后的

	private static final int CONNECTION_TIMEOUT = 25 * 1000;
	private static final int SO_SOCKET_TIMEOUT = 50 * 1000;// qjb 缩短客户端出现
															// “网络异常小章鱼”的时间

	public static final int NATIVE_ERROR = 600;
	public static final int UNKNOWN_HOST = 601;
	public static final int SOCKET_TIMEOUT = 602;
	public static String stateWay = "status";

	public static ConnectivityManager sConnectivityManager;

	private ExecutorService threadPool;
	private NetStatusListener mNetStatusListener;

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	private static NetworkWorker inst = new NetworkWorker();

	public static NetworkWorker getInstance() {
		return inst;
	}

	private NetworkWorker() {
		// fixed thread pool
		threadPool = Executors.newFixedThreadPool(5, new ThreadFactory() {
			int i = 0;

			@Override
			public Thread newThread(Runnable r) {
				Thread mThread = new Thread(r, "NetworkWorker " + i++);
				mThread.setPriority(Thread.MIN_PRIORITY);
				// mThread.setPriority(5);
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				return mThread;
			}
		});
	}

	public DefaultHttpClient getHttpClient() {
		return new DefaultHttpClient();
	}

	public DefaultHttpClient getHttpClientx() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SO_SOCKET_TIMEOUT);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// schemeRegistry.register(new Scheme("https",
		// SSLCustomSocketFactory.getSocketFactoryByBKS("tuan800_1.bks","BKS"),
		// 443));
		// schemeRegistry.register(new Scheme("https",
		// SSLCustomSocketFactory.getSocketFactoryByBKS("tuan800_2.bks","BKS"),
		// 443));
		// schemeRegistry.register(new Scheme("https",
		// SSLCustomSocketFactory.getSocketFactoryByCer("tuan8002.cer"), 443));
		// schemeRegistry.register(new Scheme("https",
		// SSLCustomSocketFactory.getSocketFactoryByCer("sso2.cer"), 443));
		schemeRegistry.register(new Scheme("https", SSLCustomSocketFactory
				.getSocketFactoryDef(), 443));

		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);

		return new DefaultHttpClient(cm, params);
	}

	private synchronized void closeHttpClientConnection(DefaultHttpClient client) {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}

	/**
	 * Simple asynchronous get with response, must be used in a looped
	 * thread(like ui thread)
	 * 
	 * @param url
	 * @param callback
	 */

	public void get(final String url, final ICallback callback,
			final Object... params) {

		if (callback == null)
			throw new IllegalArgumentException("callback must not be empty");

		final Handler handler = new Handler(Looper.getMainLooper());

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				String result = null;
				int status = NATIVE_ERROR;
				DefaultHttpClient client = getHttpClient();

				try {
					HttpGet m = new HttpGet(url);

					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);

					final HttpResponse response = client.execute(m);
					HttpEntity entity = response.getEntity();
					status = response.getStatusLine().getStatusCode();

					Header contentEncoding = response
							.getFirstHeader("Content-Encoding");

					if (null != contentEncoding
							&& "gzip".equalsIgnoreCase(contentEncoding
									.getValue())) {
						InputStream in = new GZIPInputStream(entity
								.getContent());
						result = StringUtil.getFromStream(in);
					} else {
						result = EntityUtils.toString(entity);
					}
				} catch (final Exception e) {
					LogUtil.w(e);

					result = e.getMessage();
					if (e instanceof UnknownHostException) {
						status = UNKNOWN_HOST;
					} else if (e instanceof InterruptedIOException) {
						status = SOCKET_TIMEOUT;
					}
				} finally {
					closeHttpClientConnection(client);
					callbackForUI(handler, callback, status, result);
				}
			}
		});
	}

	// 回调在工作线程
	public void getCallbackInBg(final String url, final ICallback callback,
			final Object... params) {

		if (callback == null)
			throw new IllegalArgumentException("callback must not be empty");

		final Handler handler = new Handler(Looper.getMainLooper());

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				String result = null;
				int status = NATIVE_ERROR;
				DefaultHttpClient client = getHttpClient();

				try {
					HttpGet m = new HttpGet(url);
					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);

					final HttpResponse response = client.execute(m);
					HttpEntity entity = response.getEntity();
					status = response.getStatusLine().getStatusCode();

					Header contentEncoding = response
							.getFirstHeader("Content-Encoding");

					if (null != contentEncoding
							&& "gzip".equalsIgnoreCase(contentEncoding
									.getValue())) {
						InputStream in = new GZIPInputStream(entity
								.getContent());
						result = StringUtil.getFromStream(in);
					} else {
						result = EntityUtils.toString(entity);
					}
				} catch (final Exception e) {
					LogUtil.w(e);

					result = e.getMessage();
					if (e instanceof UnknownHostException) {
						status = UNKNOWN_HOST;
					} else if (e instanceof InterruptedIOException) {
						status = SOCKET_TIMEOUT;
					}
				} finally {
					closeHttpClientConnection(client);
					callback.onResponse(status, result);
				}
			}
		});
	}

	/**
	 * Simple asynchronous get without response
	 * 
	 * @param url
	 */
	public void get(final String url, final Object... params) {

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				DefaultHttpClient client = getHttpClient();
				try {
					HttpGet m = new HttpGet(url);
					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);
					client.execute(m);
				} catch (Exception e) {
					LogUtil.w(e);
				} finally {
					closeHttpClientConnection(client);
				}
			}
		});
	}

	/**
	 * Synchronous get string result from url
	 * 
	 * @param url
	 * @return
	 */
	public String getSync(String url, final Object... params) throws Exception {

		String result;
		DefaultHttpClient client = getHttpClient();

		try {
			HttpResponse response = getResponse(client, url, params);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity entity = response.getEntity();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");

				if (null != contentEncoding
						&& "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
					InputStream in = new GZIPInputStream(entity.getContent());
					result = StringUtil.getFromStream(in);
				} else {
					result = EntityUtils.toString(entity);
				}
				return result;
			} else {
				throw new NetworkException(status, "status");
			}
		} finally {
			closeHttpClientConnection(client);
		}

	}

	public String getSync2(String url, final Object... params) throws Exception {

		String result;
		DefaultHttpClient client = getHttpClient();

		try {
			HttpResponse response = getResponse(client, url, params);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity entity = response.getEntity();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");

				if (null != contentEncoding
						&& "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
					InputStream in = new GZIPInputStream(entity.getContent());
					result = StringUtil.getFromStream(in);
				} else {
					result = EntityUtils.toString(entity, HTTP.UTF_8);// 在这里转换
				}
				return result;
			} else {
				throw new NetworkException(status, "status");
			}
		} finally {
			closeHttpClientConnection(client);
		}

	}

	/**
	 * Synchronous get response with last modified header
	 * 
	 * @param url
	 * @return
	 * @throws java.io.IOException
	 */
	public HttpResponse getResponse(DefaultHttpClient client, String url,
			final Object... params) throws Exception {
		HttpGet m = new HttpGet(url);
		HttpRequester requester = generalRequester(params);
		requester.handlerHttpHeader(client, m);
		return client.execute(m);
	}

	public HttpResponse getPostResponse(DefaultHttpClient client, String url,
			final Object... params) throws Exception {
		HttpPost m = new HttpPost(url);
		HttpRequester requester = generalRequester(params);
		requester.handlerHttpHeader(client, m);
		return client.execute(m);
	}

	/**
	 * Asynchronous post with response callback, must be used in a looped
	 * thread(like ui thread)
	 * 
	 * @param url
	 * @param callback
	 * @param params
	 */
	public void post(final String url, final ICallback callback,
			final Object... params) {
		if (callback == null)
			throw new IllegalArgumentException("callback must not be empty");

		final Handler handler = new Handler();

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				String result = null;
				int status = NATIVE_ERROR;
				DefaultHttpClient client = getHttpClient();
				try {
					HttpPost m = new HttpPost(url);
					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);

					final HttpResponse response = client.execute(m);

					if (requester.isSaveHeaders()) {
						requester.setResponseHeaders(response.getAllHeaders());
					}

					HttpEntity entity = response.getEntity();
					status = response.getStatusLine().getStatusCode();
					result = EntityUtils.toString(entity);
				} catch (final Exception e) {
					LogUtil.w(e);

					result = e.getMessage();
					if (e instanceof UnknownHostException) {
						status = UNKNOWN_HOST;
					} else if (e instanceof InterruptedIOException) {
						status = SOCKET_TIMEOUT;
					}
				} finally {
					closeHttpClientConnection(client);
					callbackForUI(handler, callback, status, result);
				}
			}
		});
	}

	public void postCallbackInBg(final String url, final ICallback callback,
			final Object... params) {
		if (callback == null)
			throw new IllegalArgumentException("callback must not be empty");

		final Handler handler = new Handler();

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				String result = null;
				int status = NATIVE_ERROR;
				DefaultHttpClient client = getHttpClient();
				try {
					HttpPost m = new HttpPost(url);
					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);

					final HttpResponse response = client.execute(m);

					if (requester.isSaveHeaders()) {
						requester.setResponseHeaders(response.getAllHeaders());
					}

					HttpEntity entity = response.getEntity();
					status = response.getStatusLine().getStatusCode();
					result = EntityUtils.toString(entity);
				} catch (final Exception e) {
					LogUtil.w(e);

					result = e.getMessage();
					if (e instanceof UnknownHostException) {
						status = UNKNOWN_HOST;
					} else if (e instanceof InterruptedIOException) {
						status = SOCKET_TIMEOUT;
					}
				} finally {
					closeHttpClientConnection(client);
					callback.onResponse(status, result);
				}
			}
		});
	}

	/**
	 * Simple asynchronous post without response
	 * 
	 * @param url
	 * @param params
	 */
	public void post(final String url, final Object... params) {

		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				DefaultHttpClient client = getHttpClient();
				try {
					HttpPost m = new HttpPost(url);
					HttpRequester requester = generalRequester(params);
					requester.handlerHttpHeader(client, m);
					client.execute(m);
				} catch (Exception e) {
					LogUtil.w(e);
				} finally {
					closeHttpClientConnection(client);
				}
			}
		});
	}

	/**
	 * Synchronous post
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String postSync(String url, Object... params) {

		String result = null;
		DefaultHttpClient client = getHttpClient();
		try {
			HttpPost m = new HttpPost(url);
			HttpRequester requester = generalRequester(params);
			// requester.setCookie(CookieTable.getInstance().getCookieByDomain(ParamBuilder.DOMAIN));
			requester.handlerHttpHeader(client, m);
			HttpResponse response = client.execute(m);
			result = EntityUtils.toString(response.getEntity());

			if (requester.isSaveHeaders()) {
				requester.setResponseHeaders(response.getAllHeaders());
			}
		} catch (Exception e) {
			LogUtil.w(e);
		} finally {
			closeHttpClientConnection(client);
		}

		return result;
	}

	public HttpResponse getResponse2(DefaultHttpClient client, String url,
			boolean isPost, final Object... params) throws Exception {

		HttpRequestBase m = null;
		if (isPost) {
			m = new HttpPost(url);
		} else {
			m = new HttpGet(url);
		}
		HttpRequester requester = generalRequester(params);
		requester.handlerHttpHeader(client, m);
		return client.execute(m);
	}

	public HttpRequester generalRequester(Object... params) throws Exception {

		if (params == null || params.length == 0) {
			return new HttpRequester();
		}

		if (params.length > 0 && !(params[0] instanceof HttpRequester)) {
			throw new IllegalArgumentException(
					"Http request need a HttpRequester param in the first");
		}

		LogUtil.d("传入的参数：" + ((HttpRequester) params[0]).mParams.toString());
		return (HttpRequester) params[0];
	}

	private void callbackForUI(Handler handler, final ICallback callback,
			final int code, final String msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mNetStatusListener != null) {
					try {
						mNetStatusListener
								.listenerNetStatus(new JSONObject(msg)
										.getString(stateWay));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				callback.onResponse(code, msg);
			}
		});
	}

	/**
	 * Check current net connection type.E,g wifi, cmnet ,cmwap etc.
	 * 
	 * @return
	 */
	public static String getNetMode() {
		String netMode = "";
		if (sConnectivityManager == null) {
			sConnectivityManager = (ConnectivityManager) MyApplication
					.getInstance().getSystemService("connectivity");
		}
		final NetworkInfo mobNetInfo = sConnectivityManager
				.getActiveNetworkInfo();

		if (mobNetInfo != null && mobNetInfo.isAvailable()) {
			int netType = mobNetInfo.getType();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				netMode = mobNetInfo.getTypeName();
			} else if (netType == ConnectivityManager.TYPE_MOBILE) {
				netMode = mobNetInfo.getExtraInfo();
			}
		}

		return netMode;
	}

	/**
	 * Asynchronous http get/post callback
	 */
	public interface ICallback {
		public void onResponse(int status, String result);
	}

	public class NetworkException extends Exception {

		private static final long serialVersionUID = -2841294936395077461L;

		public int status = NATIVE_ERROR;

		public NetworkException() {
			super();
		}

		public NetworkException(int status) {
			super();
			this.status = status;
		}

		public NetworkException(String message) {
			super(message);
		}

		public NetworkException(int status, String message) {
			super(message);
			this.status = status;
		}

		public NetworkException(String message, Throwable cause) {
			super(message, cause);
		}

		public NetworkException(Throwable cause) {
			super(cause);
		}

		@Override
		public String getMessage() {
			return "status：" + status + " " + super.getMessage();
		}
	}

	public static String postFile(String path, Map<String, String> params,
			File file, CommCallBack faceCommCallBackPro) throws Exception {
		return uploadFile(path, params, file, faceCommCallBackPro);
	}

	/**
	 * 上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
																			// 随机生成
	private static final String CHARSET = "utf-8"; // 设置编码

	public static String uploadFile(String RequestURL,
			Map<String, String> param, File file,
			CommCallBack faceCommCallBackPro) throws Exception {

		String result = null;
		URL url = new URL(RequestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(TIME_OUT);
		conn.setConnectTimeout(TIME_OUT);
		conn.setDoInput(true); // 允许输入流
		conn.setDoOutput(true); // 允许输出流
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST"); // 请求方式
		conn.setRequestProperty("Charset", CHARSET); // 设置编码
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
				+ BOUNDARY);
		// conn.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");

		/**
		 * 当文件不为空，把文件包装并且上传
		 */
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		StringBuffer sb = null;
		String params = "";

		/***
		 * 以下是用于上传参数
		 */
		if (param != null && param.size() > 0) {
			Iterator<String> it = param.keySet().iterator();
			while (it.hasNext()) {
				sb = null;
				sb = new StringBuffer();
				String key = it.next();
				String value = param.get(key);
				sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"")
						.append(key).append("\"").append(LINE_END)
						.append(LINE_END);
				sb.append(value).append(LINE_END);
				params = sb.toString();

				dos.write(params.getBytes());
				// dos.flush();
			}
		}

		sb = null;
		params = null;
		sb = new StringBuffer();
		/**
		 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
		 * 比如:abc.png
		 */
		sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
		sb.append("Content-Disposition:form-data; name=\"" + file.getName()
				+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
		sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的
															// ，用于服务器端辨别文件的类型的
		sb.append(LINE_END);
		params = sb.toString();
		sb = null;
		dos.write(params.getBytes());

		InputStream is = new FileInputStream(file);
		byte[] bytes = new byte[1024];
		int len = 0;
		int allLen = 0;
		long filebytes = file.length();
		while ((len = is.read(bytes)) != -1) {
			dos.write(bytes, 0, len);
			faceCommCallBackPro.callBack((int) (allLen * 100d / filebytes));
		}
		is.close();
		dos.write(LINE_END.getBytes());
		byte[] end_data = (PREFIX + PREFIX + LINE_END).getBytes();
		dos.write(end_data);
		dos.flush();
		faceCommCallBackPro.callBack(99);
		int res = conn.getResponseCode();
		if (res == 200) {
			InputStream input = conn.getInputStream();
			StringBuffer sb1 = new StringBuffer();
			int ss;
			while ((ss = input.read()) != -1) {
				sb1.append((char) ss);
			}
			result = sb1.toString();
		} else {
			throw new Exception("!200");
		}

		faceCommCallBackPro.callBack(100);
		return result;
	}

	public void setNetStatusListener(NetStatusListener netStatusListener) {
		mNetStatusListener = netStatusListener;
	}

	public interface NetStatusListener {

		public void listenerNetStatus(String status);
	}
}