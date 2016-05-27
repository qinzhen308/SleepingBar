package com.core.framework.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.core.framework.R;
import com.core.framework.app.devInfo.ScreenUtil;

public class DialogUtil {

	/**
	 * 底部弹出式
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	@SuppressLint("NewApi")
	public static Dialog getMenuDialog(Activity context, View view) {

		final Dialog dialog = new Dialog(context, R.style.MenuDialogStyle);
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = ScreenUtil.getScreenWH(context)[0];
		lp.width = screenW;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.MenuDialogAnimation); // 添加动画
		return dialog;
	}

	/**
	 * 底部弹出式,自定义高度
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	@SuppressLint("NewApi")
	public static Dialog getMenuDialog2(Activity context, View view, int height) {

		final Dialog dialog = new Dialog(context, R.style.MenuDialogStyle);
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		lp.width = ScreenUtil.getScreenWH(context)[0];
		lp.height = height;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.MenuDialogAnimation); // 添加动画
		return dialog;
	}

	/**
	 * 自定义 other样式
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	public static Dialog getCenterDialog(Activity context, View view) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = ScreenUtil.getScreenWH(context)[0];
		lp.width = screenW;
		return dialog;
	}

	public static void showDialog(Dialog dialog) {
		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	public static void dismissDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
}
