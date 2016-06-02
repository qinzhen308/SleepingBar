package com.bolaa.sleepingbar.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.ImageUtil;
import com.bolaa.sleepingbar.view.CircleImageView;
import com.bolaa.sleepingbar.view.wheel.NumericWheelAdapter;
import com.bolaa.sleepingbar.view.wheel.OnWheelScrollListener;
import com.bolaa.sleepingbar.view.wheel.WheelView;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.core.framework.util.IOSDialogUtil.OnSheetItemClickListener;
import com.core.framework.util.IOSDialogUtil.SheetItemColor;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 基本信息
 * 
 * @author paulz
 * 
 */
public class MyInfoActivity extends BaseActivity {
	private final static int TAKE_PHOTO = 1;// 拍照
	private final static int TAKE_PICTURE = 2;// 本地获取
	private final static int TAKE_CROP = 3;// 裁剪
	private final static int RE_REGION = 4;// 裁剪
	private final static int RE_NAME = 5;// 裁剪
	
	private boolean isModifyMode;

	private CircleImageView mIconIv;
	private TextView mNameTv;
	private TextView mBirthTv;
	private EditText mNameEt;
	private EditText mIDCardEt;
	private TextView mSexTv;
	private TextView mIDCardTv;
	private TextView mBloodTv;
	private LinearLayout mTimeLayout;

	private WheelView year;
	private WheelView month;
	private WheelView day;

	private int mYear = 2010;
	private int mMonth = 3;//min=0
	private int mDay = 3;
	private View view;
	private String mBirthday;
	private Dialog mDialog;

	private String mFilePath;
	private UserInfo mUserInfo;
	
	private String real_name;
	private String blood;
	private String id_card;
	private String sex;
	private File avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_myinfo);
		setTitleTextRightText("", "我的信息", "修改", true);
		mBirthday = "1900-01-01";
		initView();
		setListener();
		initData();
		mDialog = DialogUtil.getMenuDialog2(this, getDataPick(), ScreenUtil.getScreenWH(this)[1] / 2);
		mDialog.setCanceledOnTouchOutside(true);
	}

	private void initView() {
		mIconIv = (CircleImageView) findViewById(R.id.myInfo_iconIv);
		mBloodTv = (TextView) findViewById(R.id.tv_blood);
		mNameTv = (TextView) findViewById(R.id.tv_name);
		mBirthTv = (TextView) findViewById(R.id.tv_birthday);
		mSexTv = (TextView) findViewById(R.id.tv_sex);
		mIDCardTv = (TextView) findViewById(R.id.tv_id_card);
		mIDCardEt = (EditText) findViewById(R.id.et_id_card);
		mNameEt = (EditText) findViewById(R.id.et_name);
		mIconIv.setEnabled(false);
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		mSexTv.setOnClickListener(this);
		mBloodTv.setOnClickListener(this);
		mIconIv.setOnClickListener(this);
		mBirthTv.setOnClickListener(this);
	}


	private void initData() {
		mUserInfo = AppStatic.getInstance().getmUserInfo();
		if (mUserInfo != null) {
			mIconIv.setUrl(mUserInfo.avatar);
			setText(mUserInfo.nick_name, mNameTv);
			setText(mUserInfo.nick_name, mNameEt);
			setText(mUserInfo.birthday, mBirthTv);
			mBirthday=mUserInfo.birthday;
			sex=mUserInfo.sex;
			if (mUserInfo.sex.equals("1")) {
				mSexTv.setText("男");
			} else if (mUserInfo.sex.equals("2")) {
				mSexTv.setText("女");
			} else {
				mSexTv.setText("保密");
			}
			//设置时间选择弹框的初始值
			if(!AppUtil.isNull(mUserInfo.birthday)){
				String[] dateStrs=mUserInfo.birthday.split("-");
				if(dateStrs.length>=3){
					mYear=Integer.valueOf(dateStrs[0]);
					mMonth=Integer.valueOf(dateStrs[1])-1;
					mDay=Integer.valueOf(dateStrs[2]);
				}else {
					mYear=Integer.valueOf(dateStrs[0]);
				}
			}
		} else {
			mIconIv.setImageResource(R.drawable.icon_small);
			setText("", mNameTv);
			setText("", mBirthTv);
			setText("", mSexTv);
		}
	}

	private void setText(String string, TextView textView) {
		if (TextUtils.isEmpty(string)) {
			string = "";
		}
		textView.setText(string);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(isModifyMode){
			switchMode();
			return;
		}
		super.onBackPressed();
	}
	
	private void switchMode(){
		isModifyMode=!isModifyMode;
		if(isModifyMode){
			mIDCardTv.setVisibility(View.GONE);
			mIDCardEt.setVisibility(View.VISIBLE);
			mNameTv.setVisibility(View.GONE);
			mNameEt.setVisibility(View.VISIBLE);
			mNameEt.requestFocus();
			mNameEt.setSelection(mNameEt.getText().length());
			setRightTvText("完成");
			AppUtil.showSoftInputMethod(this, mNameEt);
		}else {
			mIDCardTv.setVisibility(View.VISIBLE);
			mIDCardEt.setVisibility(View.GONE);
			mNameTv.setVisibility(View.VISIBLE);
			mNameEt.setVisibility(View.GONE);
			setRightTvText("修改");
			AppUtil.hideSoftInputMethod(this, mNameEt);
		}
		
		mIconIv.setEnabled(isModifyMode);
		mBirthTv.setEnabled(isModifyMode);
		mBloodTv.setEnabled(isModifyMode);
		mSexTv.setEnabled(isModifyMode);
	}
	
	
	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub
		if(isModifyMode){
			updateInfo();
		}else {
			switchMode();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBirthTv){
			if(mDialog!=null&&!mDialog.isShowing()){
				mDialog.show();
			}
		}else if (v==mBloodTv) {
			showBloodWindow();
		}else if (v==mIconIv) {
			showPhotoWindow();
		}else if (v==mSexTv) {
			showSexWindow();
		}else {
			super.onClick(v);
		}
	}


	private void showPhotoWindow() {
		new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
				.addSheetItem("拍照", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						File file = new File(ImageUtil.filePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent1.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(ImageUtil.filePath, "123.jpg")));
						startActivityForResult(intent1, TAKE_PHOTO);
					}
				}).addSheetItem("本地获取", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						Intent intent2 = new Intent(Intent.ACTION_PICK,
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent2.setType("image/*");
						startActivityForResult(intent2, TAKE_PICTURE);
					}
				}).show();
	}
	
	

	private void showSexWindow() {
		new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
				.addSheetItem("保密", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mSexTv.setText("保密");
						sex="0";
					}
				}).addSheetItem("男", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mSexTv.setText("男");
						sex="1";
					}
				}).addSheetItem("女", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mSexTv.setText("女");
						sex="2";
					}
				}).show();
	}
	
	private void showBloodWindow() {
		new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
				.addSheetItem("A", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mBloodTv.setText("A");
						blood="A";
					}
				}).addSheetItem("B", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mBloodTv.setText("B");
						blood="B";
					}
				}).addSheetItem("AB", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mBloodTv.setText("AB");
						blood="AB";
					}
				}).addSheetItem("O", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mBloodTv.setText("O");
						blood="O";
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String sdCardState = Environment.getExternalStorageState();
			if (requestCode == RE_REGION) {
				initData();
			} else if (requestCode == RE_NAME) {

				initData();
			} else if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {
				return;
			} else if (requestCode == 222) {
				finish();
			} else {

				switch (requestCode) {
				case TAKE_PHOTO:
					mFilePath = ImageUtil.filePath + "123.jpg";
					mFilePath = ImageUtil.bitmap2File(mFilePath, new Date().getTime() + ".jpg");

					File file = new File(mFilePath);
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (Exception e) {
						}
					}
					startPhotoZoom(Uri.fromFile(file), 100);

					break;
				case TAKE_CROP:// // 裁剪成功后显示图片

					Bundle bundle = data.getExtras();
					if (bundle != null) {
						Bitmap bitmap = bundle.getParcelable("data");
						if (bitmap != null) {

							avatar = ImageUtil.saveImag(bitmap, new Date().getTime() + ".jpg");
							mIconIv.setImageBitmap(bitmap);
//							putAvatar(bitmap, pFile);
						}

					}
					break;
				case TAKE_PICTURE:

					Uri imgUri_2 = data.getData();

					startPhotoZoom(imgUri_2, 100);
					break;

				}

			}
		}
	}


	private void setUserInfo(UserInfo userInfo) {
		// TODO Auto-generated method stub
		mUserInfo=userInfo;
		setText(userInfo.birthday, mBirthTv);
		setText(userInfo.nick_name, mNameTv);
		setText(userInfo.nick_name, mNameEt);
		if (userInfo.sex.equals("1")) {
			mSexTv.setText("男");
		} else if (userInfo.sex.equals("2")) {
			mSexTv.setText("女");
		} else {
			mSexTv.setText("保密");
		}
	}

	/**
	 * 修改信息
	 */
	private void updateInfo() {
		HttpRequester requester = new HttpRequester();
		if (mBirthday != null) {
			requester.mParams.put("birthday", mBirthday);
		}
		if (sex != null) {
			requester.mParams.put("sex", sex);
		}
		String real_name=mNameEt.getText().toString().trim();
		if (real_name .length()>0) {
			requester.mParams.put("real_name", real_name);
		}else {
			requester.mParams.put("real_name", "");
			/*AppUtil.showToast(this, "请输入姓名");
			return;*/
		}
		if (blood != null) {
			requester.mParams.put("blood", blood);
		}
		String id_card=mIDCardEt.getText().toString().trim();
		if (id_card .length()>0) {
			requester.mParams.put("id_card", id_card);
		}else {
			requester.mParams.put("id_card", "");
//			AppUtil.showToast(this, "请输入身份证");
//			return;
		}
		if (avatar != null) {
			requester.mParams.put("avatar", avatar);
		}
		DialogUtil.showDialog(lodDialog);
		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_USER_INFO_SAVE, new ICallback() {

			@Override
			public void onResponse(int status, String result) {
				DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<UserInfo> object=GsonParser.getInstance().parseToObj(result, UserInfo.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							AppUtil.showToast(getApplicationContext(), "修改成功");
							AppStatic.getInstance().isLogin = true;
							
							PreferencesUtils.putBoolean("isLogin", true);

							AppStatic.getInstance().setmUserInfo(
									object.data);
							AppStatic.getInstance().saveUser(object.data);
							switchMode();
							setUserInfo(object.data);
							finish();
						}else {
							AppUtil.showToast(getApplicationContext(), object.info);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "修改失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}

			}

		}, requester);

	}


	/**
	 * 跳转至系统截图界面进行截图
	 * 
	 * @param data
	 * @param size
	 */
	private void startPhotoZoom(Uri data, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		// crop为true时表示显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, TAKE_CROP);
	}

	/*
	 * dataPick滑动 scrollListener
	 */
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 1950;// 年
			int n_month = month.getCurrentItem() + 1;// 月

			initDay(n_year, n_month);

			mBirthday = new StringBuilder().append((year.getCurrentItem() + 1950)).append("-")
					.append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
							: (month.getCurrentItem() + 1))
					.append("-").append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1)
							: (day.getCurrentItem() + 1))
					.toString();

		}
	};

	private void initDay(int arg1, int arg2) {
		// 设置天数
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel("日");
		day.setViewAdapter(numericWheelAdapter);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @return int
	 * @author lilifeng
	 */
	private int getDay(int year, int month) {
		int day = 31;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 4:
			day = 30;
			break;
		case 6:
			day = 30;
			break;
		case 9:
			day = 30;
			break;
		case 11:
			day = 30;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 31;
			break;
		}
		return day;
	}

	/**
	 * 时间选择控价
	 * 
	 * @return
	 */
	private View getDataPick() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);
		int curYear = mYear;
		int curMonth = mMonth + 1;
		int curDate = mDay;

		view = LayoutInflater.from(this).inflate(R.layout.dialog_wheel, null);
		view.findViewById(R.id.wheel_okTv).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBirthTv.setText(mBirthday);
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
		year = (WheelView) view.findViewById(R.id.wheel_yearWv);
		/**
		 * 设置年份
		 */
		NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, 1950, norYear);
		numericWheelAdapter1.setLabel("年");
		year.setViewAdapter(numericWheelAdapter1);
		year.setCyclic(true);// 是否可循环滑动
		year.addScrollingListener(scrollListener);

		month = (WheelView) view.findViewById(R.id.wheel_monthWv);
		/**
		 * 设置月份
		 */
		NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		month.setViewAdapter(numericWheelAdapter2);
		month.setCyclic(true);
		month.addScrollingListener(scrollListener);

		day = (WheelView) view.findViewById(R.id.wheel_dayWv);
		initDay(curYear, curMonth);
		day.addScrollingListener(scrollListener);
		day.setCyclic(true);

		year.setVisibleItems(9);// 设置显示行数
		month.setVisibleItems(9);
		day.setVisibleItems(9);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);

		return view;
	}
	
	public static void invoke(Context context){
		Intent intent =new Intent(context,MyInfoActivity.class);
		context.startActivity(intent);
	}

}
