/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.core.framework.image.universalimageloader.core.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.core.framework.develop.DevRunningTime;
import com.core.framework.develop.LogUtil;
import com.core.framework.develop.Su;
import com.core.framework.image.universalimageloader.core.DealUrl;

/**
 * Provides retrieving of {@link java.io.InputStream} of image by URI from network or file system or app resources.<br />
 * {@link java.net.URLConnection} is used to retrieve image stream from network.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see com.tuan800.framework.image.universalimageloader.core.download.HttpClientImageDownloader
 * @since 1.8.0
 */
public class BaseImageDownloader implements ImageDownloader {
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 20 * 1000; // milliseconds
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 80 * 1000; // milliseconds

    /**
     * {@value}
     */
    protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    /**
     * {@value}
     */
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    protected static final int MAX_REDIRECT_COUNT = 10;

    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. "
            + "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseImageDownloader(Context context) {
        this.context = context.getApplicationContext();
        this.connectTimeout = DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.readTimeout = DEFAULT_HTTP_READ_TIMEOUT;
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(DealUrl imageUri, Object extra) throws IOException {


        switch (Scheme.ofUri(imageUri.url)) {
            case HTTP:
            case HTTPS:
                return getStreamFromNetwork(imageUri, extra);
            case FILE:
                return getStreamFromFile(imageUri, extra);
            case CONTENT:
                return getStreamFromContent(imageUri, extra);
            case ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case UNKNOWN:
            default:
                return getStreamFromOtherSource(imageUri, extra);
        }
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI (image is located in the network).
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     * @throws java.io.IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    protected InputStream getStreamFromNetwork(DealUrl imageUriDeal, Object extra) throws IOException {
        HttpURLConnection conn = createConnection(imageUriDeal.url);
//        conn.setRequestProperty("Content-type", imageUri.getHeadInfo());

        if(DevRunningTime.isCacheImage)
        Su.log("http load image" +imageUriDeal.url);

        int redirectCount = 0;

        int code = conn.getResponseCode();
        if (code != 200) {
            LogUtil.w(null,"load image getResponseCode:" + code);
        }

        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount++;
        }

        return new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
    }

    /**
     * Create {@linkplain java.net.HttpURLConnection HTTP connection} for incoming URL
     *
     * @param url URL to connect to
     * @return {@linkplain java.net.HttpURLConnection Connection} for incoming URL. Connection isn't established so it still
     * configurable.
     * @throws java.io.IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    protected HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI (image is located on the local file system or SD card).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     * @throws java.io.IOException if some I/O error occurs reading from file system
     */
    protected InputStream getStreamFromFile(DealUrl imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri.url);
        return new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI (image is accessed using {@link android.content.ContentResolver}).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     * @throws java.io.FileNotFoundException if the provided URI could not be opened
     */
    protected InputStream getStreamFromContent(DealUrl imageUri, Object extra) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse(imageUri.url);
        return res.openInputStream(uri);
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI (image is located in assets of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     * @throws java.io.IOException if some I/O error occurs file reading
     */
    protected InputStream getStreamFromAssets(DealUrl imageUri, Object extra) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri.url);
        return context.getAssets().open(filePath);
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI (image is located in drawable resources of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     */
    protected InputStream getStreamFromDrawable(DealUrl imageUri, Object extra) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri.url);
        int drawableId = Integer.parseInt(drawableIdString);
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * Retrieves {@link java.io.InputStream} of image by URI from other source with unsupported scheme. Should be overriden by
     * successors to implement image downloading from special sources.<br />
     * This method is called only if image URI has unsupported scheme. Throws {@link UnsupportedOperationException} by
     * default.
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link com.tuan800.framework.image.universalimageloader.core.DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link java.io.InputStream} of image
     * @throws java.io.IOException                   if some I/O error occurs
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    protected InputStream getStreamFromOtherSource(DealUrl imageUri, Object extra) throws IOException {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri.url));
    }
}