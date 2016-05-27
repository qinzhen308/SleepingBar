package com.bolaa.medical.utils;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.alipay.android.phone.mrpc.core.o;
import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.common.GlobeFlags;
import com.bolaa.medical.common.Settings;
import com.bolaa.medical.model.MyApplicationInfo;
import com.bolaa.medical.view.CustomToast;
import com.core.framework.app.AppSetting;
import com.core.framework.app.devInfo.DeviceInfo;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.DatabaseManager;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.StringUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA. User: kait Date: 12-5-19 Time: 下午2:24 To change
 * this template use File | SettingsActivity | File Templates.
 */
public class AppUtil {

	public static boolean isNull(String str) {
		return TextUtils.isEmpty(str) || "null".equals(str);
	}

	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(Object... objects) {
		return (objects == null || objects.length <= 0);
	}

	public static void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showTaoToast(Context context, int resId) {
		CustomToast myToast = CustomToast.getInstance(context);
		myToast.setMessage(context.getResources().getString(resId));
		myToast.showTime(1000);
		myToast.show();
	}

	public static void showTaoToast(Context context, String message) {
		CustomToast myToast = CustomToast.getInstance(context);
		myToast.setMessage(message);
		myToast.showTime(1000);
		myToast.show();
	}

	public static void setPaintFlags(TextView textView) {
		textView.setPaintFlags(textView.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);
	}

	public static String getDiscount(float cur, float pre) {
		DecimalFormat format = new DecimalFormat("0.0");
		if (cur == 0 || pre == 0) {
			return "";
		}

		String zhe = format.format(10 * cur / pre);

		if (zhe != null && zhe.endsWith(".0"))
			zhe = zhe.substring(0, zhe.length() - 2);

		return zhe;
	}

	public static String getPrice(float price) {
		float mPrice = price / 100;
		if (mPrice == (int) mPrice) {
			return String.valueOf((int) mPrice);
		} else {
			return String.valueOf(mPrice);
		}
	}

	public static int generaRandom(int size) {
		return (int) (Math.random() * size);
	}

	public static boolean isNotFirstStart() {
		String userCheckTime = PreferencesUtils
				.getString(GlobeFlags.NEW_USER_CHECK);

		if (!TextUtils.isEmpty(userCheckTime)) {
			if (DateUtil.afterNow(userCheckTime)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOldUesr() {
		String userCheckTime = PreferencesUtils
				.getString(GlobeFlags.NEW_USER_CHECK);

		if (!TextUtils.isEmpty(userCheckTime)) {
			if (GlobeFlags.OLD_USER_FLAG.equals(userCheckTime)) {
				return true;
			}
		}
		return false;
	}

	public static String makeSchemeRefer(String schemeChannel, String moduleName) {
		return "scheme_" + schemeChannel + "|" + moduleName;
	}

	public static String makePushRefer(String pushId, String moduleName) {
		return "push_" + pushId + "|" + moduleName;
	}

	// 获取包里带的推广码
	public static String getSpreadCode() {
		String spread = "";
		try {
			InputStream is = HApplication.getInstance().getAssets()
					.open("control.txt");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String json = new String(buffer, "UTF-8");
			if (!TextUtils.isEmpty(json)) {
				JSONObject object = new JSONObject(json);
				if (object.has("spread_code")) {
					spread = object.optString("spread_code");
				}
			}
		} catch (FileNotFoundException fx) {
			LogUtil.d("access file txt is not exist");
		} catch (Exception ex) {
			LogUtil.d("access file txt get wrong");
			ex.printStackTrace();
		}

		return spread;
	}

	public static Spannable getStringForFormat(String string) {
		Spannable spannable;
		int hourIndex;
		int minuteIndex;
		int secondIndex;

		spannable = new SpannableString(string);
		hourIndex = string.indexOf(HApplication.getInstance().getResources()
				.getString(R.string.hour));
		minuteIndex = string.indexOf(HApplication.getInstance().getString(
				R.string.minute));
		secondIndex = string.indexOf(HApplication.getInstance().getString(
				R.string.second));

		spannable.setSpan(new RelativeSizeSpan(0.7f), 0, 5,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new RelativeSizeSpan(0.7f), hourIndex, hourIndex + 2,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new RelativeSizeSpan(0.7f), minuteIndex,
				minuteIndex + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new RelativeSizeSpan(0.7f), secondIndex,
				secondIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannable;
	}

	// 获取String类型当前时间
	public static String getNowTime() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTme = df.format(date);
		return nowTme;
	}

	// 将String类型时间换成Date类型
	public static Date getDateFromString(String time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date mDate = null;
		try {
			mDate = df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDate;
	}

	// coform qq
	public static boolean isQQ(String qq) {
		Pattern p = Pattern.compile("[1-9][0-9]{4,14}");
		Matcher m = p.matcher(qq);
		return m.matches();
	}

	// coform modilephone
	public static boolean isMobilePhone(String tel) {
		if (tel.contains(" ")) {
			tel = tel.replaceAll(" ", "");
		}

		if (tel.contains("-")) {
			tel = tel.replaceAll("-", "");
		}
		Pattern p = Pattern.compile("(1)\\d{10}$");
		return p.matcher(tel).matches();
	}

	// coform postcode
	public static boolean isPostCode(String postcode) {
		Pattern p = Pattern.compile("\\p{Digit}{6}");
		boolean isExist = false;
		if (p.matcher(postcode).matches())
			isExist = true;
		return isExist;
	}

	// coform email
	public static boolean isEmail(String str) {
		Pattern p1 = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
		Matcher m = p1.matcher(str);
		return m.matches();
	}

	public static void cleanCache() {
		try {
			// SELECT name FROM sqlite_master WHERE type='table' AND
			// name='table_name';
			SQLiteDatabase db = DatabaseManager.getInstance()
					.openDatabase(AppConfig.DEFAULT_DATABASE).getDb();
			String sql = "DELETE FROM dpc";
			db.execSQL(sql);

			sql = "DELETE FROM image";
			db.execSQL(sql);
		} catch (SQLiteException e) {
			LogUtil.w(e);
		}
	}

	public static void removeCookie() {
		CookieSyncManager.createInstance(HApplication.getInstance());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

	// 检查用户省份是不是可以邮寄
	public static boolean isRightAddress(String province) {
		if (TextUtils.isEmpty(province)) {
			return false;
		}
		if (!province.contains("香港") && !province.contains("澳门")
				&& !province.contains("台湾") && !province.contains("新疆")
				&& !province.contains("甘肃") && !province.contains("青海")
				&& !province.contains("西藏") && !province.contains("内蒙")) {
			return true;
		}

		return false;
	}

	@Deprecated
	// need use GET_TASKS permission
	public static boolean isRuning(Activity context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos = am.getRunningTasks(50);
		for (RunningTaskInfo taskInfo : taskInfos) {
			if (taskInfo.topActivity.getPackageName().equals(
					context.getPackageName())
					&& taskInfo.numActivities != 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Count string 's length.
	 * 
	 * @param str
	 * @return
	 */
	public static int getWordCount(CharSequence str) {
		int length = 0;
		if (str != null) {
			int size = str.length();
			int num = 0;
			for (int i = 0; i < size; i++) {
				final int ascii = Character.codePointAt(str, i);
				if (ascii >= 255) {
					length++;
				} else if (ascii >= 0 && ascii < 255) {
					num++;
					if (num != 2) {
						length++;
					} else {
						num = 0;
					}
				}
			}
		}

		return length;
	}

	public static String formatFileSize(long length) {
		String result = null;
		int sub_string;
		if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "M";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "K";
		} else if (length < 1024)
			result = Long.toString(length) + "B";

		return result;
	}

	// 二维码扫描dealID取得
	public static String getDealId(String dealUrl) {
		String baseUrl = "dealId=";
		String dealId = "";
		if (dealUrl.contains(baseUrl)) {
			dealId = dealUrl.split(baseUrl)[1].trim();
		}
		return dealId;
	}


	public static String getChannel() {
		StringBuilder channel = new StringBuilder();
		channel.append(AppConfig.CLIENT_TAG).append("|")
				.append(DeviceInfo.getDeviceId()).append("|").append("Android")
				.append("|")
				.append(HApplication.getInstance().getVersionName())
				.append("|").append(AppConfig.PARTNER_ID);
		return channel.toString();
	}

	public static String getCityId() {
		if (Settings.city == null || TextUtils.isEmpty(Settings.city.id)
				|| "null".equals(Settings.city.id)) {
			Settings.initCity();
			return "1";
		}

		return Settings.city.id;
	}

	/**
	 * 4.0.0 新增out参数
	 * 
	 * @param url
	 * @param pos_type
	 * @param pos_value
	 * @param position
	 * @param dealId
	 * @return
	 */
	public static String addActivityValueInOutUrl(String url, String pos_type,
			String pos_value, int position, String dealId) {
		if (TextUtils.isEmpty(url))
			return url;

		StringBuilder standardUrl = new StringBuilder(url);
		if (url.indexOf("?") > -1) {
			if (!url.endsWith("?")) {
				standardUrl.append("&");
			}
		} else {
			standardUrl.append("?");
		}
		standardUrl.append("pos_type").append("=").append(pos_type);
		standardUrl.append("&pos_value").append("=").append(pos_value);
		standardUrl.append("&model_name").append("=").append("deallist");
		standardUrl.append("&model_item_index").append("=")
				.append(position + 1);
		standardUrl.append("&model_id").append("=").append(dealId);
		standardUrl.append("&model_index").append("=").append("");
		return standardUrl.toString();
	}

	private static String generaTTID() {
		return "400000_21428298@zbbwx_Android_"
				+ HApplication.getInstance().getVersionName();
	}

	public static PackageInfo getTuan800Info() {
		PackageInfo tuan800Info = null;
		try {
			tuan800Info = HApplication.getInstance().getPackageManager()
					.getPackageInfo("com.tuan800.android", 0);
		} catch (Exception e) {
			LogUtil.w(e);
		}

		if (tuan800Info != null) {
			LogUtil.d("--------tuan800--------" + tuan800Info.versionCode);
		}

		return tuan800Info;
	}

	public static PackageInfo getTaoBaoInfo() {
		PackageInfo taoBaoInfo = null;
		try {
			taoBaoInfo = HApplication.getInstance().getPackageManager()
					.getPackageInfo("com.taobao.taobao", 0);
		} catch (Exception e) {
			LogUtil.w(e);
		}

		if (taoBaoInfo != null) {
			LogUtil.d("--------taobao--------" + taoBaoInfo.versionCode);
		}

		return taoBaoInfo;
	}

	public static long getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+"); // 多个空格转译 \\s+
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
			LogUtil.w(e);
		}
		return initial_memory;
	}

	public static boolean isThresholdMemory() {
		long start = System.currentTimeMillis();
		long totalMemory = getTotalMemory();
		if (totalMemory == 0)
			return false;
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) HApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(outInfo);
		long nativeThreshold = (long) ((outInfo.threshold / (totalMemory * 1.0) + 0.01) * totalMemory); // 内存临界值加大1%
		if (outInfo.availMem <= nativeThreshold) {
			Image13Loader.getInstance().flushCache();
			// releaseMemory();
			return true;
		}
		return false;
	}

	public static void releaseMemory() {
		Image13Loader.getInstance().flushCache();
		ActivityManager activityManager = (ActivityManager) HApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManager
				.getRunningAppProcesses();
		ActivityManager.RunningAppProcessInfo apinfo = null;
		String[] pkgList;
		if (!isEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				apinfo = list.get(i);
				pkgList = apinfo.pkgList;
				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					for (int j = 0; j < pkgList.length; j++) {
						if (SDK_INT < FROYO) {
							activityManager.restartPackage(pkgList[j]);
						}

						activityManager.killBackgroundProcesses(pkgList[j]);
					}
				}
			}
		}
	}

	// 获取已安装应用列表
	public static List<MyApplicationInfo> getAllInstalledPages(
			Activity _activity) {
		PackageManager packageManager = _activity.getPackageManager();
		List<MyApplicationInfo> myApplicationInfos = new ArrayList<MyApplicationInfo>();
		List<PackageInfo> infos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (int i = 0; i < infos.size(); i++) {
			if ((infos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				MyApplicationInfo myApplicationInfo = new MyApplicationInfo();
				myApplicationInfo.name = infos.get(i).applicationInfo
						.loadLabel(_activity.getPackageManager()).toString();
				myApplicationInfo.packageName = infos.get(i).packageName;
				myApplicationInfo.version = infos.get(i).versionName;
				myApplicationInfos.add(myApplicationInfo);
			} else {

			}
		}
		return myApplicationInfos;
	}

	// 启动其他应用
	public static boolean openApp(Activity context, String packageName) {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		if (apps != null) {
			ResolveInfo ri = null;
			if (apps.iterator().hasNext()) {
				ri = apps.iterator().next();
			}
			if (ri != null) {
				String packageName1 = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName1, className);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(cn);
				context.startActivity(intent);
				return true;
			}
		}
		return false;
	}

	public static void doLogout() {

	}

	// 显示软键盘
	public static void showSoftInputMethod(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	// 隐藏软键盘
	public static void hideSoftInputMethod(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static String getFormatPhone(String phone, String regular) {
		String tempPhone = "";
		char[] chars = phone.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i == 3 || i == 7) {
				tempPhone += regular;
			}
			tempPhone += c;
		}

		return tempPhone;
	}

	/**
	 * 将元单位转换成分单位
	 * 
	 * @return 分
	 */
	public static Integer change2int(String price) {
		BigDecimal decimal = new BigDecimal(price);
		BigDecimal decimal1 = new BigDecimal(100);
		return decimal.multiply(decimal1).intValue();
	}

	/**
	 * 将元单位转换成分单位
	 * 
	 * @return 分
	 */
	public static String change2String(String price) {
		BigDecimal decimal = new BigDecimal(price);
		BigDecimal decimal1 = new BigDecimal(100);
		return decimal.multiply(decimal1).toString();
	}

	/**
	 * 将分单位转换元单位
	 * 
	 * @param cent
	 * @return
	 */
	public static String change2String(int cent) {
		BigDecimal decimal = new BigDecimal(cent);
		BigDecimal decimal1 = new BigDecimal(100);
		return decimal.divide(decimal1).toString();
	}

	// 过滤字符串，比如过滤“.0”
	public static String filterStr(String src, String str) {
		if (StringUtil.isEmpty(src))
			return "";

		int index = src.lastIndexOf(str);
		if (index > 0) {
			if (src.substring(index, src.length()).equals(str)) {
				return src.substring(0, index);
			}
		}

		return src;
	}

	// 截取字符串
	public static String filterTopStr(String src, String str) {

		if (isEmpty(str)) {
			return "";
		}

		int index = src.indexOf(str);
		if (index > 0) {

			return src.substring(0, index);
		}

		return src;
	}

	public static void setWeiXinLoginFastBroadcast() {
		Intent intent = new Intent("com.weixin.loginfast");
		HApplication.getInstance().sendBroadcast(intent);

	}

	public static String getTopActivity(Activity context)

	{

		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)

			return (runningTaskInfos.get(0).topActivity).toString();

		else

			return null;

	}

	public static String getSalesCount(int number) {
		if (0 == AppSetting.SALES_COUNT_SHOW_WAN) {
			return number + "";
		}
		if (number < 10000) {
			return number + "";
		}

		int numOfHundred = number % 1000 / 100;
		int numOfThousand = number % 10000 / 1000;
		int numBeforeThousand = number / 10000;

		if (numOfHundred >= 5) {
			numOfThousand += 1;
			if (numOfThousand == 10) {
				numBeforeThousand += 1;
			}
		}

		if (numOfThousand == 0 || numOfThousand == 10) {
			return numBeforeThousand + "万";
		} else {
			return numBeforeThousand + "." + numOfThousand + "万";
		}
	}

	public static String getSalesCount(long number) {
		if (0 == AppSetting.SALES_COUNT_SHOW_WAN) {
			return number + "";
		}
		if (number < 10000) {
			return number + "";
		}

		long numOfHundred = number % 1000 / 100;
		long numOfThousand = number % 10000 / 1000;
		long numBeforeThousand = number / 10000;

		if (numOfHundred >= 5) {
			numOfThousand += 1;
			if (numOfThousand == 10) {
				numBeforeThousand += 1;
			}
		}

		if (numOfThousand == 0 || numOfThousand == 10) {
			return numBeforeThousand + "万";
		} else {
			return numBeforeThousand + "." + numOfThousand + "万";
		}
	}

	public static int getRandomNum(int maxNum) {
		return (int) (Math.random() * maxNum);
	}

	public static String getNewType() {
		StringBuilder type = new StringBuilder();
		try {
			ConnectivityManager cm = (ConnectivityManager) HApplication
					.getInstance().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
			if ("wifi".equalsIgnoreCase(typeName)) {
				type.append(typeName);
			} else {
				type.append(typeName);
				type.append("-");
				type.append(info.getSubtypeName());
				type.append("-");
				type.append(info.getExtraInfo());
			}
		} catch (Exception e) {
			LogUtil.w(e);
		}

		return type.toString();
	}

	/**
	 * web页加载完的时候会保存当前的URL,但是Scheme跳转的URL是不应该保存的，不然返回的时候会找不到页面
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isNeedSaveUrlAfterLoadFinish(String url) {
		if (url.startsWith("zhe800")) {// 自己的Scheme跳转
			return false;
		}

		// mqq://im/chat?chat_type=wpa&uin=3103049266&version=1&src_type=web&web_src=h5.m.zhe800.com
		// cmp=com.tencent.mobileqq/.activity.JumpActivity
		if (url.startsWith("mqq")) {// 商城点击QQ客户跳转到QQ聊天
			return false;
		}
		return true;
	}

	// 设置显示商品的数量
	public static void setSellCount(TextView mSellCountTv, int sellCount) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		String hasSaledAmount = AppUtil.getSalesCount(sellCount);
		int length = 2 + hasSaledAmount.length();
		ssb.append("已售");
		ssb.append(hasSaledAmount);
		ssb.append("件");
		ssb.setSpan(new ForegroundColorSpan(HApplication.getInstance()
				.getResources().getColor(R.color.red1)), 2, length,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSellCountTv.setText(ssb);
	}

	/**
	 * 设置商品去向的view的内容 优先级：品牌和主题显示对应的，其余的显示淘宝天猫或者商城
	 * 
	 * @param tvStore
	 *            显示内容的view
	 * @param flag
	 *            商品的归属
	 * @param deal_type
	 * @param shop_type
	 */
	public static void setDealStore(TextView tvStore, int flag,
			String deal_type, int shop_type) {
		tvStore.setVisibility(View.VISIBLE);
		if (flag == 4) {
			tvStore.setText("主题馆");
		} else if (flag == 5) {
			tvStore.setText("品牌特卖");
		} else {
			if ("1".equals(deal_type)) {
				tvStore.setText("特卖商城");
			} else if (shop_type == 1) {
				tvStore.setText("去天猫");
			} else if (shop_type == 0) {
				tvStore.setText("去淘宝");
			} else {
				tvStore.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 摄像头是否可用
	 * 
	 * @return
	 */
	public static boolean isCameraCanUse() {
		boolean canUse = true;
		Camera mCamera = null;
		try {
			// TODO camera驱动挂掉,处理??
			mCamera = Camera.open();
		} catch (Exception e) {
			canUse = false;
		}
		if (canUse) {
			mCamera.release();
			mCamera = null;
		}

		return canUse;
	}

	/**
	 * 逆向解析URL，获取参数值
	 * 
	 * @param URL
	 * @return
	 */
	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 * 
	 * @param strURL
	 *            url地址
	 * @return url请求参数部分
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}

	// 生成9位随机码//好像不能保证唯一性
	public static String generateRandomString(int len) {
		String all = "0123456789abcdefghijklmnopqrstuvwxyz";
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < len; i++) {
			s.append(all.charAt(getRandomNum(36)));
		}

		return s.toString();
	}

	// 检查应用是否有此权限
	public static boolean checkPermission(Context context, String permission) {
		return (PackageManager.PERMISSION_GRANTED == context
				.checkCallingOrSelfPermission(permission));
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @return true 表示开启
	 */
	public static boolean isGpsOpen(final Context context) {
		try {
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
			boolean gps = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
			boolean network = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			return gps || network;
		} catch (Exception e) {
			LogUtil.w(e, "gps error");
		}

		return false;
	}

	public static boolean isGpsEnable(final Context context) {

		return isGpsOpen(context)
				&&
				// 下面部分有些手机不是很管用
				(AppUtil.checkPermission(context,
						"android.permission.ACCESS_COARSE_LOCATION") || AppUtil
						.checkPermission(context,
								"android.permission.ACCESS_FINE_LOCATION"));

	}

	public static String encodeUrl(String key) {
		return URLEncoder.encode(key);
	}

	// 关键词url编码 【截取前10个字符】 //按久麟的意思，先截取再 encode
	public static String encodeUrlAfterCut(String key, int len) {
		if (!TextUtils.isEmpty(key) && key.length() > len)
			key = key.substring(0, len);

		return URLEncoder.encode(key);
	}

	/**
	 * 读取表情配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets()
					.open("data/emoji.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 判断图片文件是否是gif格式
	public static boolean isGifFile(String filepath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filepath);
			byte[] b = new byte[16];
			if (fis.read(b) > 10) {
				if (b[0] == (byte) 'G' && b[1] == (byte) 'I'
						&& b[2] == (byte) 'F')
					return true;
				// PNG b[1~3]=="PNG" JPG:b[6~9]=="JGIF"
			}
		} catch (Exception e) {
			LogUtil.d(e.getMessage());
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
					LogUtil.d(e.getMessage());
				}
			}
		}

		return false;
	}

	// 同步下载文件
	public static boolean downloadFile(String remoteFile, String localFile) {
		HttpURLConnection conn = null;
		InputStream in = null;
		FileOutputStream out = null;

		int readSize = 0;

		try {
			if (null != remoteFile
					&& !remoteFile.toLowerCase().startsWith("http")) {
				remoteFile = "http://" + remoteFile;
			}
			URL url = new URL(remoteFile);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();

			in = conn.getInputStream();
			out = new FileOutputStream(localFile, false);

			byte[] buff = new byte[1024];
			while ((readSize = in.read(buff)) > 0)
				out.write(buff, 0, readSize);

			return true;
		} catch (Exception e) {
			LogUtil.w(e, "downloadFile error");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				LogUtil.w(e, "downloadFile error");
			}
		}

		return false;
	}


	// 判断身份证：要么是15位，要么是18位
	public static boolean verifyIDcode(String idCode) {
		// 通过流处理获得用户身份证号
		// 定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
		Pattern idNumPattern = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		// 通过Pattern获得Matcher
		Matcher idNumMatcher = idNumPattern.matcher(idCode);
		// 判断用户输入是否为身份证号
		if (idNumMatcher.matches()) {
			return true;

		} else {
			return false;
		}
	}

	/**
	 * 验证手机号码格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
	
	
	public static String getTwoDecimal(float src){
		DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p=decimalFormat.format(src);//format 返回的是字符串
		if(p.startsWith(".")){
			p="0"+p;
		}
		return p==null?"":p;
	}
}