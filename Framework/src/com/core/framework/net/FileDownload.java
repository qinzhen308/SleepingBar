package com.core.framework.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.file.FileHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-5-13
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
public class FileDownload {

    public static final int DOWNLOAD_BEGIN = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_FAILED = 3;

    private Context mContext;
    private static int BUFF_SIZE = 4096;

    public FileDownload(){}

    public FileDownload(Context context){
        mContext = context;
    }

    public void download(final String remoteFile, final String localFile, final Handler progressHandler) {
        // Check local file exists
        if (new File(localFile).exists()) {
            FileHelper.delete(localFile);
        }
        // one thread supported
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doDownload(remoteFile, localFile, progressHandler);
                } catch (IOException err) {
                    LogUtil.w(err);
                }
            }
        }).start();
    }

    private void doDownload(String remoteFile, String localFile, Handler progressHandler) throws IOException{
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream out = null;

        int downloadedSize = 0;
        int readSize = 0;
        int perTen = 0;

        try {
            if(null!=remoteFile && !remoteFile.toLowerCase().startsWith("http")) {
                remoteFile = "http://" + remoteFile;
            }
            URL url = new URL(remoteFile);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            int fileSize = conn.getContentLength();
            if (fileSize == -1) {
                sendProgressMessage(progressHandler, 0, DOWNLOAD_FAILED);
                return;
            }

            in = conn.getInputStream();
            if (mContext == null) {
                out = new FileOutputStream(localFile, false);
            } else {
                out = mContext.openFileOutput(localFile,
                        Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
            }
            byte[] buff = new byte[BUFF_SIZE];
            sendProgressMessage(progressHandler, 0, DOWNLOAD_BEGIN);
            while ((readSize = in.read(buff)) > 0) {
                out.write(buff, 0, readSize);
                downloadedSize += readSize;
                if (perTen == 0 || (downloadedSize * 100 / fileSize) - 2 > perTen) {
                    perTen += 2;
                    sendProgressMessage(progressHandler, (downloadedSize * 100 / fileSize), DOWNLOAD_PROGRESS);
                }
            }
            sendProgressMessage(progressHandler, downloadedSize * 100 / fileSize, DOWNLOAD_SUCCESS);
        } catch (MalformedURLException e) {
            LogUtil.w(e);
        } catch (SocketTimeoutException e) {
            LogUtil.w(e);
            sendProgressMessage(progressHandler, 0, DOWNLOAD_FAILED);
        } catch (IOException e) {
            LogUtil.w(e);
            sendProgressMessage(progressHandler, 0, DOWNLOAD_FAILED);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Send progress message
     * @param handler
     * @param progress      0 - 100
     * @param what          0 - begin download, 1 - in progress, 2 - download finished, 3 - download failed
     */
    private void sendProgressMessage(Handler handler, int progress, int what) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = what;
            msg.arg1 = progress;
            handler.sendMessage(msg);
        }
    }
}
