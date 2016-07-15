package com.bolaa.sleepingbar.update;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.app.AppSetting;
import com.core.framework.app.MyApplication;
import com.core.framework.app.base.BaseUser;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.DB.Database;
import com.core.framework.store.DB.DatabaseManager;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.update.UpdateBuilder;
import com.core.framework.update.UpdateService;
import com.core.framework.update.VersionManager;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-5-1
 * Time: 下午12:45
 * To change this template use File | Settings | File Templates.
 */
public class UpdateUtil extends com.core.framework.update.UpdateUtil{

    private static final int POS_TUAN = 0;
    private static final int POS_ZHE = 1;

    private static final String TUAN800_NAME = "sleepingbar.apk";
    private static final String ZHE800_NAME = "sleepingbar.apk";

    private static Activity mContext;
    private static List<UpdateBuilder> partnerList = new ArrayList<UpdateBuilder>();
    public static boolean isChecking = false;

    public static void checkBackgroundDate(final Activity context, final boolean fromSettings, final boolean needUpdate) {
        isChecking = true;
        mContext = context;

        ParamBuilder pb = new ParamBuilder();
        pb.append("types", "0");

        if (AppSetting.OFFLINE_SWITCH == 0) {
            NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(pb.getParamList(), AppUrls.getInstance().URL_CHECK_UPDATE), new NetworkWorker.ICallback() {
                @Override
                public void onResponse(int status, String result) {
                    if (status != 200) {
                        isChecking = false;
                        return;
                    }

                    String data="";
                    try {
						JSONObject jsonObject=new JSONObject(result);
						if(jsonObject.optInt("status")!=1){
							isChecking = false;
							return;
						}
						data=jsonObject.optString("data");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    
                    try {
                        if (needUpdate) {
                            UpdateUtil.checkZheUpdate(data, fromSettings);
                        } else {
                            isChecking = false;
                        }
                    } catch (JSONException e) {
                        LogUtil.w(e);
                    }
                }
            });
        } else {
            NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(pb.getParamList(), AppUrls.getInstance().URL_CHECK_UPDATE), new NetworkWorker.ICallback() {
                @Override
                public void onResponse(int status, String result) {
                    if (status != 200) {
                        isChecking = false;
                        return;
                    }
                    
                    String data="";

                    try {
                        JSONObject jObj = new JSONObject(result);
                        if(jObj.optInt("status")!=1){
							isChecking = false;
							return;
						}
                        
						data=jObj.optString("data");
//                        if (!jObj.has("partner_update")){
//                            if (!jObj.optBoolean("partner_update")){
//                                if (fromSettings) {
//                                    AppUtil.showTaoToast(mContext, "当前已经是最新版本");
//                                }
//                                isChecking = false;
//                                return;
//                            }
//                        }

                        if (needUpdate) {
                            UpdateUtil.checkZheUpdate(result, fromSettings);
                        } else {
                            isChecking = false;
                        }
                    } catch (JSONException e) {
                        LogUtil.w(e);
                        isChecking = false;
                    }
                }
            });

        }

    }

    private static UpdateBuilder getUpdateBuilder(int pos, ZheUpdateEntity partner) {
        UpdateBuilder builder = new UpdateBuilder();
        switch (pos) {
            case POS_TUAN:
                builder.notifyLayout = R.layout.tuan800_download_notification;
                builder.progressBarId = R.id.tuan_down_pb;
                builder.notificationTvId = R.id.down_tv;
                builder.imageViewId = R.id.down_img;
                builder.imageId = R.drawable.logo;
                builder.iconId = R.drawable.logo;
                builder.downloadStr = mContext.getString(R.string.downloading);
                builder.failMsg = mContext.getString(R.string.update_failure);
                builder.successMsg = mContext.getString(R.string.update_success);
                builder.installFileName = TUAN800_NAME;
                builder.setPartner(partner);
                break;
            case POS_ZHE:
                builder.notifyLayout = R.layout.download_notification;
                builder.progressBarId = R.id.down_pb;
                builder.notificationTvId = R.id.down_tv;
                builder.imageViewId = R.id.down_img;
                builder.imageId = R.drawable.logo;
                builder.iconId = R.drawable.logo;
                builder.downloadStr = mContext.getString(R.string.downloading);
                builder.failMsg = mContext.getString(R.string.update_failure);
                builder.successMsg = mContext.getString(R.string.update_success);
                builder.installFileName = ZHE800_NAME;
                builder.setPartner(partner);
                break;
        }

        return builder;
    }

    public static void checkZheUpdate(final String updateStr, final boolean fromSettings) throws JSONException {

        JSONObject mJSONObject = new JSONObject(updateStr);
        ZheUpdateEntity mZheUpdateEntity = new ZheUpdateEntity();
        mZheUpdateEntity.appName = AppConfig.CLIENT_TAG;
        mZheUpdateEntity.downloadUrl = mJSONObject.optString("download_url");
//        mZheUpdateEntity.remoteVersionCode = Integer.valueOf(mJSONObject.optString("version_sort"));
        mZheUpdateEntity.remoteVersionCode = mJSONObject.optInt("versions_code");
//        mZheUpdateEntity.remoteMinVersionCode = Integer.valueOf(mJSONObject.optString("mix_version"));
        mZheUpdateEntity.remoteMinVersionCode = mJSONObject.optInt("mix_version");
        mZheUpdateEntity.description = mJSONObject.optString("description");
        mZheUpdateEntity.mustUpdate = mJSONObject.optBoolean("must-update");
        mZheUpdateEntity.minSystemVersion = mJSONObject.optString("min_system_version");

        boolean isUpdate = VersionManager.compareVersion(MyApplication.getInstance().getVersionCode(), mZheUpdateEntity.remoteVersionCode);

        if (mZheUpdateEntity != null && isUpdate) {
            if (!AppUtil.isEmpty(partnerList)) partnerList.clear();
            partnerList.add(getUpdateBuilder(POS_ZHE, mZheUpdateEntity));
            showUpdateDialog(null, false, fromSettings);
        } else if (fromSettings) {
            AppUtil.showTaoToast(mContext, "当前已经是最新版本");
            isChecking = false;
        } else {
            isChecking = false;
        }
    }

    private static void showUpdateDialog(final UpdateBuilder tuanUpdateBuilder, boolean isNew, final boolean fromSettings) {
        final UpdateDialog dialog = new UpdateDialog(mContext);

        if (!AppUtil.isEmpty(partnerList)) {
            dialog.setDescriptionText(partnerList.get(0).partner.description.replace("\\n", "\n"));
            if (partnerList.get(0).partner.mustUpdate) {
                dialog.hideNoUpdateTip();
                dialog.setCancelable(false);
                dialog.setCanCancelByBackPress(false);
            }

            dialog.setOnPositiveListener(new UpdateDialog.OnDialogClick() {
                @Override
                public void onPositiveClick() {
                    LogUtil.d("---------------size---------- = " + partnerList.size());
                    for (UpdateBuilder builder : partnerList) {
                        UpdateService.getInstance().preDownload(mContext, builder);
                    }

                    if (partnerList.get(0).partner.mustUpdate) {
                        mContext.finish();
                    }
                }

                @Override
                public void onNegativeClick() {
                    if (dialog.isNoNoticeCheck()) {
                        PreferencesUtils.putString(GlobeFlags.NO_UPDATE_NOTICE_TAG, MyApplication.getInstance().getVersionName());
                    }

                    if (partnerList.get(0).partner.mustUpdate) {
                        mContext.finish();
                    }
                }
            });

            if (!mContext.isFinishing()) {
                dialog.show();
                isChecking = false;
            } else {
                isChecking = false;
            }

        } else if (fromSettings) {
            //AppUtil.showToast(mContext, "当前已经是最新版本");
            AppUtil.showTaoToast(mContext, "当前已经是最新版本");
            isChecking = false;
        } else {
            isChecking = false;
        }

    }

    /**
     * 应用升级的时候，数据库表更新操作
     */
    public static void updateBefore() {

        DatabaseManager.getInstance().openDatabase(AppConfig.DEFAULT_DATABASE).setOnUpgradeListener(new Database.DatabaseOnUpgradeListener() {
            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase) {

                if (sqLiteDatabase.getVersion() < 20400) return;

                
            }
        });
    }

    private static BaseUser getLoginUser(Cursor cursor) throws Exception {
        BaseUser user = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            user = new BaseUser();
            user.setId(cursor.getString(cursor.getColumnIndex("user_id")));
            user.setLogin(cursor.getInt(cursor.getColumnIndex("is_login")) == 1);
        }
        return user;
    }

    private static String getSignId(SQLiteDatabase sqLiteDatabase) {
        String sql = "select value from preferences where key = ? and (expire_time=-1 OR expire_time> " + System.currentTimeMillis() / 1000 + ")";
        String value = "";
        try {
            Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{"isSign"});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    value = cursor.getString(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            LogUtil.w(e);
        }
        return value;
    }

    //新版本，设置应用升级
    public static void newSettingUpdateVersion() {
        if (!AppUtil.isEmpty(partnerList)) {
            for (UpdateBuilder builder : partnerList) {
                UpdateService.getInstance().preDownload(mContext, builder);
            }
        }

//        Analytics.onEvent(Tao800Application.getInstance(), AnalyticsInfo.EVENT_DOWNLOAD_APP_BIND, "t:" + (dialog.isUpdateCheck() ? 1 : 0));
    }

    public static boolean isNewDownning() {
        if (AppUtil.isEmpty(partnerList)) return false;

        return UpdateService.getInstance().isNewSettingDownning();

    }


    public static boolean IsEmptyUpdateParam() {
        return partnerList.isEmpty();
    }

    public static void buildPartnerList(ZheUpdateEntity partner) {
        if (!partnerList.isEmpty()) {
            partnerList.clear();
        }
        partnerList.add(getUpdateBuilder(POS_ZHE, partner));
    }



}