package com.core.framework.app.devInfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.core.framework.app.MyApplication;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 4/2/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScreenUtil {

    public static int HEIGHT;
    public static int WIDTH;
    public static int DENSITY_DPI;
    public static int DENSITY_SIZE=0;

    public static int STATUSUIBAR = 0;

    public static final int HIGH_MODE = 0;
    public static final int MIDDLE_MODE = 1;
    public static final int LOW_MODE = 2;

    public static final int DENSITY_480 = 0;
    public static final int DENSITY_720 = 1;
    public static final int DENSITY_1080 = 2;


    public static final int IMAGE_TYRPE_480 = 0;
    public static final int IMAGE_TYRPE_1080_720_WIFI = 1;


    public static int getTitleDisplayMode() {
        int highHeight = 800;
        int lowHeight = 320;

        if (highHeight > HEIGHT && lowHeight < HEIGHT) {
            return MIDDLE_MODE;
        } else if (HEIGHT >= highHeight) {
            return HIGH_MODE;
        } else {
            return LOW_MODE;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }catch (Exception exception){
            return (int) (dpValue * 1 + 0.5f);
        }

    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
	/**
	 * 获取当前窗体宽度和高度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int[] getScreenWH(Activity mContext) {
		DisplayMetrics dm = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        Window win = activity.getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);

        return rect.top;
    }

    public static int getTitleBarHeight(Activity activity) {
        Rect rect = new Rect();
        Window win = activity.getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);
        return win.findViewById(Window.ID_ANDROID_CONTENT).getTop() - rect.top;
    }

    public static void setDisplay(Activity activity) {
        setContextDisplay(activity);
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        STATUSUIBAR = rect.top;
    }

    public static void setContextDisplay(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        WIDTH = dm.widthPixels;
        HEIGHT = dm.heightPixels;
        DENSITY_DPI = dm.densityDpi;
        if (WIDTH >= 1080) {
            DENSITY_SIZE= DENSITY_1080;
        } else if (WIDTH >= 720) {
            DENSITY_SIZE= DENSITY_720;
        } else {
            DENSITY_SIZE= DENSITY_480;
        }
    }

    public static int getScreenDensity(Context context) {
        return  DENSITY_SIZE;
    }

    public static int getUsedImageType() {
        if(MyApplication.netType== ConnectivityManager.TYPE_WIFI
                && ScreenUtil.DENSITY_SIZE>= ScreenUtil.DENSITY_720){
            return IMAGE_TYRPE_1080_720_WIFI;
        }else{
            return  IMAGE_TYRPE_480;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        listView.setTag(false);
        int j=-1;
        int totalHeight = 0;
        View listItem=null;
        for (int i = listAdapter.getCount()-1; i >=0; i--) { // listAdapter.getCount()返回数据项的数目

            if(j==listAdapter.getItemViewType(i)&&listItem!=null){
                //View listItem=
            }

             else{
                listItem = listAdapter.getView(i, null, listView);
            }
            j=listAdapter.getItemViewType(i);
            if(listItem==null)continue;
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns = 0;
        int horizontalBorderHeight = 0;
        Class<?> clazz = gridView.getClass();
        try {
            // 利用反射，取得每行显示的个数
            Field column = clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns = (Integer) column.get(gridView);
            // 利用反射，取得横向分割线高度
            Field horizontalSpacing = clazz
            .getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
            } catch (Exception e) {
            e.printStackTrace();
            }
        // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
            } else {
            rows = listAdapter.getCount() / columns;
            }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度

        gridView.setLayoutParams(params);
        }




}
