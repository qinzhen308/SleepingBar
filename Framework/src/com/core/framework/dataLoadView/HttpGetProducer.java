package com.core.framework.dataLoadView;


import com.core.framework.develop.LogUtil;
import com.core.framework.exception.InternalServerException;
import com.core.framework.exception.UserLoginException;
import com.core.framework.net.HttpRequester;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.StringUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by kait on 7/10/13.
 */
public class HttpGetProducer extends AbstractProducer {

    public HttpGetProducer() {
        cacher = new HttpCacher();
    }

    private void handleLastModified(Header lastModified) {
        if (lastModified == null) return;

        ((HttpCacher) cacher).setLastModified(lastModified.getValue());
    }

    private void handleMaxAge(Header cacheControl) {
        if (cacheControl == null) return;

        // 上层设置了cacheTime为-1，表示强制不缓存不处理max-age
        if (((HttpCacher) cacher).getMaxAge() < 0) return;

        String ccValue = cacheControl.getValue();
        if (StringUtil.isNull(ccValue)) return;

        String[] controls = ccValue.split(",");
        if (controls.length == 0) return;

        String[] singleControl;

        try {
            for (String c : controls) {
                if (!c.contains("max-age")) continue;

                singleControl = c.split("=");
                if (singleControl.length > 1) {
                    // max-age 以秒为单位
                    ((HttpCacher) cacher).setMaxAge(Long.parseLong(singleControl[1]) * 1000);
                }
            }
        } catch (Exception e) {
            LogUtil.w(e);
        }
    }


    protected String produce() throws Exception {
        Object[] params = request.getParams();
        if (null == params || params.length == 0 || !(params[0] instanceof String)) {
            throw new IllegalArgumentException("HttpGet need a string param as url.");
        }

        String url = params[0].toString();
        String lastModified = null;
        if (cacher instanceof HttpCacher) {
            lastModified = ((HttpCacher) cacher).getLastModified(url);
        }

        HttpRequester requester = request.getRequester();
        if (requester == null) {
            requester = new HttpRequester();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("If-Modified-Since", lastModified);
            requester.setRequestHeaders(headers);
        } else {
            requester.getRequestHeaders().put("If-Modified-Since", lastModified);
        }

        DefaultHttpClient client =  NetworkWorker.getInstance().getHttpClient();
        try {
            HttpResponse response =  NetworkWorker.getInstance().getResponse(client, url, requester);
            int status = response.getStatusLine().getStatusCode();
            if (200 == status) {
                HttpEntity entity = response.getEntity();
                String result;
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (null != contentEncoding && "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
                    InputStream in = new GZIPInputStream(entity.getContent());
                    result = StringUtil.getFromStream(in);
                } else {
                    result = EntityUtils.toString(entity);
                }

                handleMaxAge(response.getFirstHeader("Cache-Control"));
                handleLastModified(response.getFirstHeader("Last-Modified"));

                return result;
            } else if (304 == status) {
                // 返回的是304，不理会max-age，直接使用应用自身的设置
                handleLastModified(response.getFirstHeader("Last-Modified"));
                return cacher.getCachedData(request.getHashKey());
            } else if (status == HttpStatus.SC_UNAUTHORIZED) {
                throw new UserLoginException(status);
            } else if (status >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new InternalServerException(status);
            } else {
                throw new Exception("Network exception " + status);
            }
        } catch (Exception e) {
            LogUtil.d("http get producer excepton = " + e);
            throw e;
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
    }
}
