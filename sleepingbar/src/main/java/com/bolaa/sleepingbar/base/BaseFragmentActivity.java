package com.bolaa.sleepingbar.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.core.framework.util.DialogUtil;

/**
 * 基础FragmentActivity
 * 
 * @author jjj
 * 
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener{

	@Override
	public void finish() {
		super.finish();
		AppStatic.getInstance().removeActivity(this);
	}

	private TextView mLeftTv;
	private TextView milddleTv;
	private ImageView rightIv;
	private TextView rightTv;
	protected InputMethodManager manager;
	private RelativeLayout base_titleLayout;
	protected LoadStateController mLoadStateController;// 加载状态的控制器
	protected boolean hasLoadingState = false;

	private View xian;
	protected Dialog lodDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		lodDialog = DialogUtil.getCenterDialog(this, LayoutInflater.from(this).inflate(R.layout.load_doag, null));
	}

	// @TargetApi(19)
	// private void setTranslucentStatus(boolean on) {
	// Window win = getWindow();
	// WindowManager.LayoutParams winParams = win.getAttributes();
	// final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
	// if (on) {
	// winParams.flags |= bits;
	// } else {
	// winParams.flags &= ~bits;
	// }
	// win.setAttributes(winParams);
	// }

	public void base_title_goneor_not(Boolean b) {
		if (b) {
			base_titleLayout.setVisibility(View.VISIBLE);
		} else {
			base_titleLayout.setVisibility(View.GONE);

		}
	}

	/**
	 * 初始化布局
	 *
	 * @param paramInt
	 */
	protected void setActiviyContextView(int paramInt) {

		AppStatic.getInstance().addActivity(this);
		setContentView(R.layout.activity_base);

		base_titleLayout = (RelativeLayout) findViewById(R.id.base_titleLayout);
		xian = (View) findViewById(R.id.xian);
		mLeftTv = (TextView) findViewById(R.id.baseTitle_leftTv);
		milddleTv = (TextView) findViewById(R.id.baseTitle_milddleTv);
		rightIv = (ImageView) findViewById(R.id.baseTitle_rightIv);
		rightTv = (TextView) findViewById(R.id.baseTitle_rightTv);

		FrameLayout localFrameLayout = (FrameLayout) findViewById(R.id.base_contentLayout);
		localFrameLayout.removeAllViews();
		LayoutInflater.from(this).inflate(paramInt, localFrameLayout);
	}

	/**
	 * 初始化布局
	 *
	 * @param paramInt
	 */
	protected void setActiviyContextView(int paramInt, boolean hasLoadingState,
										 boolean hasTitle) {
		this.hasLoadingState = hasLoadingState;
		AppStatic.getInstance().addActivity(this);
		setContentView(R.layout.activity_base);

		base_titleLayout = (RelativeLayout) findViewById(R.id.base_titleLayout);
		xian = (View) findViewById(R.id.xian);
		mLeftTv = (TextView) findViewById(R.id.baseTitle_leftTv);
		milddleTv = (TextView) findViewById(R.id.baseTitle_milddleTv);
		rightIv = (ImageView) findViewById(R.id.baseTitle_rightIv);
		rightTv = (TextView) findViewById(R.id.baseTitle_rightTv);
		if (!hasTitle) {
			base_titleLayout.setVisibility(View.GONE);
		}
		FrameLayout localFrameLayout = (FrameLayout) findViewById(R.id.base_contentLayout);
		localFrameLayout.removeAllViews();
		LayoutInflater.from(this).inflate(paramInt, localFrameLayout);
		if (hasLoadingState) {
			mLoadStateController = new LoadStateController(this,
					localFrameLayout);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置标题 右边没有字体的时候
	 *
	 * @param left
	 * @param middle
	 * @param right
	 * @param isShowLeft
	 */
	public void setTitleText(String left, String middle, int right,
							 boolean isShowLeft) {
		rightTv.setVisibility(View.GONE);
		if (isShowLeft) {
			mLeftTv.setVisibility(View.VISIBLE);
			mLeftTv.setOnClickListener(this);
		} else {
			mLeftTv.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(left)) {
			mLeftTv.setText(left);
		}

		if (!TextUtils.isEmpty(middle)) {
			milddleTv.setText(middle);
		}

		if (right != 0) {
			rightIv.setImageResource(right);
			rightIv.setVisibility(View.VISIBLE);
			rightIv.setOnClickListener(this);
		} else {
			rightIv.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置标题 右边有字体的时候
	 *
	 * @param left
	 * @param middle
	 * @param right
	 * @param isShowLeft
	 */
	public void setTitleTextRightText(String left, String middle, String right,
									  boolean isShowLeft) {
		rightIv.setVisibility(View.GONE);
		if (isShowLeft) {
			mLeftTv.setVisibility(View.VISIBLE);
			mLeftTv.setOnClickListener(this);
		} else {
			mLeftTv.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(left)) {
			mLeftTv.setText(left);
		}

		if (!TextUtils.isEmpty(middle)) {
			milddleTv.setText(middle);
		}

		if (!TextUtils.isEmpty(right)) {
			rightTv.setVisibility(View.VISIBLE);
			rightTv.setText(right);
			rightTv.setOnClickListener(this);
		} else {
			rightTv.setVisibility(View.GONE);
		}
	}

	public void setTitleTextRightText_color(String left, String middle,
											String right, boolean isShowLeft, int color_id, Boolean isShowLine) {
		rightIv.setVisibility(View.GONE);
		if (isShowLeft) {
			mLeftTv.setVisibility(View.VISIBLE);
			mLeftTv.setOnClickListener(this);
		} else {
			mLeftTv.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(left)) {
			mLeftTv.setText(left);
		}

		if (!TextUtils.isEmpty(middle)) {
			milddleTv.setText(middle);
		}
		if (!isShowLine) {
			xian.setVisibility(View.GONE);
		}
		base_titleLayout.setBackgroundColor(getResources().getColor(color_id));

		if (!TextUtils.isEmpty(right)) {
			rightTv.setVisibility(View.VISIBLE);
			rightTv.setText(right);
			rightTv.setOnClickListener(this);
		} else {
			rightTv.setVisibility(View.GONE);
		}
	}

	public void setTitleBarBgColor(int color_id){

		base_titleLayout.setBackgroundColor(getResources().getColor(color_id));
	}

	public void setRightTvText(String text){
		rightTv.setText(text);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.baseTitle_leftTv:
				onLeftClick();
				this.finish();
				break;
			case R.id.baseTitle_rightIv:
				onRightClick();
				break;
			case R.id.baseTitle_rightTv:
				onRightClick();
				break;

			default:
				break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppStatic.getInstance().removeActivity(this);
	}

	/**
	 * 右边按钮点击事件
	 */
	public void onRightClick() {

	}

	/**
	 * 左边按钮的事件
	 */
	public void onLeftClick() {

	}

	public void showNodata() {
		if (hasLoadingState) {
			mLoadStateController.showNodata();
		}
	}

	public void showFailture() {
		if (hasLoadingState) {
			mLoadStateController.showFailture();
		}
	}

	public void showSuccess() {
		if (hasLoadingState) {
			mLoadStateController.showSuccess();
		}
	}

	public void showLoading() {
		if (hasLoadingState) {
			mLoadStateController.showLoading();
		}
	}

}
