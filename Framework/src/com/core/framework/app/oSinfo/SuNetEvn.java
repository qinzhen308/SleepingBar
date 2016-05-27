package com.core.framework.app.oSinfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.core.framework.app.AppSetting;
import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;
import com.core.framework.develop.Su;


//网络环境
public class SuNetEvn {

	private Context mContext;
	// 是不是有网
	private boolean hasNet;
	public boolean isHasNet() {
		return hasNet;
	}

	private boolean isWifi;

    boolean isOneTimes;


    private static SuNetEvn feedBack;
    public static SuNetEvn getInstance() {
        if(feedBack==null)feedBack = new SuNetEvn(MyApplication.getInstance());
        return feedBack;
    }

	private SuNetEvn(Context mContext) {
		this.mContext = mContext;
        hasNet=isStaticHasNet();
		setNetLinstener();
	}

    private boolean isStaticHasNet() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState()== NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void UnsetNetLinstener() {
		// 去掉网络监听
		mContext.unregisterReceiver(this.mConnectivityChangedReceiver);
		mConnectivityChangedReceiver = null;
	}

	private void setNetLinstener() {

		mContext.registerReceiver(this.mConnectivityChangedReceiver,
				new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}

	/*// 网络变化
	private BroadcastReceiver mConnectivityChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
			hasNet = (networkInfo != null) && (networkInfo.isConnected());
			Su.log("接收网络监听  hasNet  " + hasNet);
            int netType=-1;
			if (hasNet) {
                netType = networkInfo.getType();
                MyApplication.usedImageType = ScreenUtil.getUsedImageType();
				if (netType == ConnectivityManager.TYPE_MOBILE) {
					// switch(subType){
					// case TelephonyManager.NETWORK_TYPE_1xRTT:
					// return NET_2G; // ~ 50-100 kbps
					// case TelephonyManager.NETWORK_TYPE_CDMA:
					// return NET_2G; // ~ 14-64 kbps
					// case TelephonyManager.NETWORK_TYPE_EDGE:
					// return NET_2G; // ~ 50-100 kbps
					// case TelephonyManager.NETWORK_TYPE_EVDO_0:
					// return NET_3G; // ~ 400-1000 kbps
					// case TelephonyManager.NETWORK_TYPE_EVDO_A:
					// return NET_3G; // ~ 600-1400 kbps
					// case TelephonyManager.NETWORK_TYPE_GPRS:
					// return NET_2G; // ~ 100 kbps
					// case TelephonyManager.NETWORK_TYPE_HSDPA:
					// return NET_3G; // ~ 2-14 Mbps
					// case TelephonyManager.NETWORK_TYPE_HSPA:
					// return NET_3G; // ~ 700-1700 kbps
					// case TelephonyManager.NETWORK_TYPE_HSUPA:
					// return NET_3G; // ~ 1-23 Mbps
					// case TelephonyManager.NETWORK_TYPE_UMTS:
					// return NET_3G; // ~ 400-7000 kbps
					// NOT AVAILABLE YET IN API LEVEL 7
					// case Connectivity.NETWORK_TYPE_EHRPD:
					// return NET_3G; // ~ 1-2 Mbps
					// case Connectivity.NETWORK_TYPE_EVDO_B:
					// return NET_3G; // ~ 5 Mbps
					// case Connectivity.NETWORK_TYPE_HSPAP:
					// return NET_3G; // ~ 10-20 Mbps
					// case Connectivity.NETWORK_TYPE_IDEN:
					// return NET_2G; // ~25 kbps
					// case Connectivity.NETWORK_TYPE_LTE:
					// return NET_3G; // ~ 10+ Mbps
					// Unknown
					// case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					// return NET_2G;
					// default:
					// return NET_2G;
					// }
                    isWifi = false;
				} else if (netType == ConnectivityManager.TYPE_WIFI) {
                    isWifi = true;
				}

                if(AppSetting.SHOW_NET_LOC_SWITCH==1) {
                    if (netType != MyApplication.netType ) {

                        if(!isOneTimes) {
                            isOneTimes = true;
                        }else {
                            if (!isRunningForeground(context)) {
                                MyApplication.netChanged = true;
                            } else {
                                alertNetDialog(context);
                                MyApplication.netChanged = false;
                            }
                        }
                    }
                }

			}else{
                netType= -1;
                if(!isOneTimes) {
                    isOneTimes = true;
                }
            }

            MyApplication.netType=netType;

            synchronized (mFaceCommCallBackList){
                for(FaceCommCallBack mmFaceCommCall :mFaceCommCallBackList){
                    mmFaceCommCall.callBack();
                }
            }
        }

	};*/

    private BroadcastReceiver mConnectivityChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] infos=manager.getAllNetworkInfo();
            int netType=-1;
            for(int i=0,length=infos.length;i<length;i++){
                NetworkInfo info=infos[i];
                if(info!=null&&info.isConnected()){
                    hasNet=true;
                    netType=info.getType();
                    if (netType==ConnectivityManager.TYPE_MOBILE&&info.getSubtype()==TelephonyManager.NETWORK_TYPE_LTE) {
                        netType=ConnectivityManager.TYPE_WIFI;
                    }
                    break;
                }else {
                    hasNet=false;
                }
            }

            Su.log("接收网络监听  hasNet  " + hasNet);
            if (hasNet) {
                if (netType == ConnectivityManager.TYPE_MOBILE) {

                    isWifi = false;
                } else if (netType == ConnectivityManager.TYPE_WIFI) {
                    isWifi = true;
                }

                if(AppSetting.SHOW_NET_LOC_SWITCH==1) {
                    if (netType != MyApplication.netType ) {

                        if(!isOneTimes) {
                            isOneTimes = true;
                        }else {
                            if (!isRunningForeground(context)) {
                                MyApplication.netChanged = true;
                            } else {
                                alertNetDialog(context);
                                MyApplication.netChanged = false;
                            }
                        }
                    }
                }

            }else{
                netType= -1;
                if(!isOneTimes) {
                    isOneTimes = true;
                }
            }

            MyApplication.netType=netType;
        }

    };

//    private static IntegralResultDialog mDialog = null;

    public static void alertNetDialog(final Context context) {
//        if (mDialog == null) {
//            mDialog = new IntegralResultDialog(context);
//            mDialog.setDialogText("网络提示", "网络状态变更,产生的流量费由运营商收费", "", "允许", "不允许");
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.setCancelable(false);
//            mDialog.setOnPositiveListener(new IntegralResultDialog.OnDialogClick() {
//                @Override
//                public void onPositiveClick() {
//                }
//
//                @Override
//                public void onNegativeClick() {
//                    ((MyApplication)MyApplication.getInstance()).exit();
//                }
//            });
//
//            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        }
//
//        if (!mDialog.isShowing()) {
//            mDialog.show();
//        }
    }


    public static String getIpAddress() {
        String ipAddress = "";

        ConnectivityManager manager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) return ipAddress;

        if (info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                ipAddress = inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException ex) {
                    LogUtil.w(ex);
                }
            } else {
                WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }

                ipAddress = intToIp(wifiManager.getConnectionInfo().getIpAddress());
            }
        }

        return ipAddress;
    }

    public static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;

    }




    private boolean isRunningForeground(Context context) {

        ActivityManager    mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = componentName.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(PACKAGE_NAME)) {
            return true;
        }
        return false;

    }

    private static final String PACKAGE_NAME = "com.boju.hiyo";
}
