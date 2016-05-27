package com.core.framework.update;
import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import com.core.framework.app.MyApplication;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.FileDownload;
import com.core.framework.store.file.FileHelper;
import com.core.framework.update.RemoteStableVersion.Partner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

/**
 * Created by IntelliJ IDEA.
 * User: Kait
 * Date: 11-10-20
 * Time: 上午10:00
 * To change this template use File | Settings | File Templates.
 */
public class UpdateService {


    private static UpdateService inst ;
    public static UpdateService getInstance() {
        if(inst==null)inst = new UpdateService();
        return inst;
    }


    private static boolean mDownloading = false;

    private static final String INSTALL_APK_NAME = AppConfig.CLIENT_TAG + ".apk";

    /**
     * 版本检查，至少连网一次
     *
     * @param callback 更新确认监听器
     */
    public void checkVersion(final ConfirmUpdateVersionCallBack callback) {
        if (mDownloading) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (VersionManager.versionCodeHasUpdate()) {
                    Partner partner = VersionManager.getRemoteVersion().getRemoteVersionInfo();
                    callback.onConfirmUpdate(partner);
                }
            }
        }).start();
    }

    public Partner checkVersionSync() {
        if (VersionManager.versionCodeHasUpdate()) {
            return VersionManager.getRemoteVersion().getRemoteVersionInfo();
        }

        return null;
    }

    public Partner checkVersionSync(String resultJson) {
        if (VersionManager.versionCodeHasUpdate(resultJson)) {
            return VersionManager.getRemoteVersion().getRemoteVersionInfo();
        }

        return null;
    }
//
//    public UpdateUtil.ZheUpdateEntity checkVersionSync(String resultJson) {
//        if (VersionManager.versionCodeHasUpdate(resultJson)) {
//            return VersionManager.getRemoteVersion().getRemoteVersionInfo();
//        }
//
//        return null;
//    }

    /**
     * 比较指定某个版本号和服务器版本号。
     *
     * @param localVersionCode
     * @param resultJson
     * @return
     */
    public Partner checkVersionSync(int localVersionCode, String resultJson) {
        if (VersionManager.versionCodeHasUpdate(localVersionCode, resultJson)) {
            return VersionManager.getRemoteVersion().getRemoteVersionInfo();
        }

        return null;
    }

    public Partner checkVersionSync(JSONObject sofJson) {
        if (VersionManager.versionCodeHasUpdate(sofJson)) {
            return VersionManager.getRemoteVersion().getRemoteVersionInfo();
        }

        return null;
    }

    private boolean isNewSettingDownning = false;

    public boolean isNewSettingDownning() {
        return isNewSettingDownning;
    }

    /**
     * 准备下载动作，notification
     */
    @SuppressWarnings("unchecked")
    public void preDownload(Context context,final UpdateBuilder builder) {
        final Notification mNotification = new Notification(builder.iconId, builder.downloadStr, System.currentTimeMillis());
//        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = getInstallIntent(FileHelper.getAppFilesPath() + File.separator + INSTALL_APK_NAME);
        mNotification.contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentView = new RemoteViews(context.getPackageName(), builder.notifyLayout);

        mNotification.contentView.setProgressBar(builder.progressBarId, 100, 0, false);
        mNotification.contentView.setImageViewResource(builder.imageViewId, builder.imageId);
        mNotificationManager.notify(builder.progressBarId, mNotification);
        LogUtil.d("crate nofification id " + builder.progressBarId);

        isNewSettingDownning = true;

        startUpdate(new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == FileDownload.DOWNLOAD_BEGIN || message.what == FileDownload.DOWNLOAD_PROGRESS) {
                    int progress = message.arg1;
                    mNotification.contentView.setProgressBar(builder.progressBarId, 100, progress, false);
                } else if (message.what == FileDownload.DOWNLOAD_SUCCESS) {
                    isNewSettingDownning = false;
                    mNotification.contentView.setProgressBar(builder.progressBarId, 100, 100, false);
                    mNotification.contentView.setTextViewText(builder.notificationTvId, builder.successMsg);
                } else if (message.what == FileDownload.DOWNLOAD_FAILED) {
                    isNewSettingDownning = false;
                    mNotification.contentView.setTextViewText(builder.notificationTvId, builder.failMsg);
                }
                mNotificationManager.notify(builder.progressBarId, mNotification);
                if (message.what == FileDownload.DOWNLOAD_SUCCESS) {
                    mNotificationManager.cancel(builder.progressBarId);
                }
            }
        }, builder);
    }

    /**
     * 开始下载apk,并处理notification的进度条
     *
     * @param progressHandler
     */
    private static void startUpdate(final Handler progressHandler, final UpdateBuilder builder) {
        if (builder == null) {
            LogUtil.d("the partner's apk not find");
            return;
        }

        FileDownload fd = new FileDownload(MyApplication.getInstance());
        final String localFilePath = FileHelper.getAppFilesPath() + File.separator + builder.installFileName;

        fd.download(builder.partner.downloadUrl, builder.installFileName, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (progressHandler != null) {
                    Message pm = new Message();
                    pm.what = msg.what;
                    pm.arg1 = msg.arg1;
                    progressHandler.sendMessage(pm);
                }

                if (msg.what == FileDownload.DOWNLOAD_SUCCESS) {
                    install(localFilePath);
                }
            }
        });
    }

    private static void install(String fileName) {
        File file = new File(fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String mimeType = FileHelper.getMIMEType(file);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, mimeType);
        MyApplication.getInstance().startActivity(intent);
    }

    private Intent getInstallIntent(String fileName) {
        File file = new File(fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String mimeType = FileHelper.getMIMEType(file);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, mimeType);

        return intent;
    }

    /**
     * Interface to notify the result of the download of a apps
     * <p/>
     * It can be extended to report progress or even to support
     * decisions on whether download the file or not
     */
    public static interface DownloadAppsCallback {
        /**
         * This method is called when the file loaded successfully
         *
         * @param partnerList The downloaded apps
         */
        void onSuccess(final ArrayList<Partner> partnerList);

        /**
         * This method is called when the load of the file failed.
         */
        void onFailure();
    }

    /**
     * Interface to notify the a new version
     */
    public interface ConfirmUpdateVersionCallBack {

        /**
         * This method use for tip to the user, e.g AlertDialog or list, need run UI Thread
         */
        public void onConfirmUpdate(Partner partner);
    }
}