package com.core.framework.image.image13;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.beans.Image;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-10-17
 * Time: 上午11:24
 * Image pool toolkit
 */
@Deprecated
public class ImagePool {

    private ExecutorService executorService;

    private Map<String, SoftReference<Drawable>> drawableMap;

    private Handler handler;

    public ImagePool() {
        this(5);
    }

    public ImagePool(int taskNum) {
        this(taskNum, new Handler());
    }

    public ImagePool(int taskNum, Handler handler) {
        executorService = Executors.newFixedThreadPool(taskNum);
        drawableMap = new HashMap<String, SoftReference<Drawable>>();
        this.handler = handler;
    }

    private Drawable getDrawableFromUrl(String imgUrl) throws IOException {
        // Check if image available in database
        Bitmap bm = Image.getInstance().get(imgUrl);
        if (null == bm) {
            InputStream in = null;
            try {
                URL url = new URL(imgUrl);
                in = url.openStream();
                bm = BitmapFactory.decodeStream(new FlushedInputStream(in));
                if (null == bm) return null;
                // Save to db
                Image.getInstance().save(imgUrl, bm);
            } catch (Exception e) {
                LogUtil.w(e);
                return null;
            } finally {
                if (null != in) in.close();
            }
        }
        return new BitmapDrawable(bm);
    }

    public void requestImage(final String url, final ICallback callback) {
        requestImage(url, callback, handler);
    }

    public void requestImage(final String url, final ICallback callback, final Handler handler) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        if (null == callback)
            throw new IllegalArgumentException("Callback can not be null.");
        if (null == handler) {
            throw new IllegalArgumentException("Handler can not be null.");
        }
        SoftReference<Drawable> oldRef = drawableMap.get(url);
        if (null != oldRef && null != oldRef.get()) {
            final Drawable d = oldRef.get();
            callback.onImageResponse(d);
        } else {
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        final Drawable d = getDrawableFromUrl(url);
                        if (null == d) return;
                        // Save to soft reference
                        SoftReference<Drawable> sr = new SoftReference<Drawable>(d);
                        drawableMap.put(url, sr);
                        handler.post(new Runnable() {
                            public void run() {
                                callback.onImageResponse(d);
                            }
                        });
                    } catch (Exception e) {
                        LogUtil.w(e);
                    }
                }
            });
        }
    }

    public interface ICallback {
        public void onImageResponse(Drawable d);
    }

    public static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int byteValue = read();
                    if (byteValue < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

}
