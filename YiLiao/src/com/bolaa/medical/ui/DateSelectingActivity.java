package com.bolaa.medical.ui;

import java.io.Serializable;
import java.util.Calendar;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.Constants;
import com.bolaa.medical.utils.DateTimeUtils;
import com.bolaa.medical.utils.LauarUtil;
import com.core.framework.app.devInfo.ScreenUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DateSelectingActivity extends BaseActivity{
	
	private String selectedDate;
	private Calendar limitDateCal;
	private ScrollView mScrollView;
	private DatepickerParam mDatepickerParam;
	private Context context = this;
	private int scrollHeight = 0;
	private LinearLayout mLinearLayoutSelected;
	private Handler mHandler = new Handler() { // 点击直接跳转到选择日的对应月份
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 11) {
				mScrollView.scrollTo(0, scrollHeight);
				mScrollView.setVisibility(View.VISIBLE);
			}
			super.handleMessage(msg);
		};
	};
	
	// 获取对应的属性值 Android框架自带的属性 attr
		int pressed = android.R.attr.state_pressed;
		int enabled = android.R.attr.state_enabled;
		int selected = android.R.attr.state_selected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void setListener() {
		// TODO Auto-generated method stub
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_date_selecting, true, true);
		setTitleText("", "选择日期", 0, true);
		mScrollView =(ScrollView)findViewById(R.id.layout_container);
		createCalendar();
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		selectedDate=intent.getStringExtra("selected_date");
		limitDateCal=(Calendar)intent.getSerializableExtra("limit_date");
	}
	
	protected void createCalendar() {
//		mScrollView = new ScrollView(this);
//		mScrollView.setLayoutParams(new FrameLayout.LayoutParams(
//				FrameLayout.LayoutParams.MATCH_PARENT,
//				FrameLayout.LayoutParams.MATCH_PARENT));
//
//
//		setContentView(mScrollView);
		mScrollView.setVisibility(View.INVISIBLE);
		mDatepickerParam = new DatepickerParam();
		mDatepickerParam.startDate = DateTimeUtils.getCurrentDateTime();
		mDatepickerParam.dateRange = 30;
		mDatepickerParam.selectedDay = AppUtil.isNull(selectedDate)? mDatepickerParam.startDate : DateTimeUtils.getCalendar(selectedDate);

		LinearLayout localLinearLayout1 = new LinearLayout(this);
		localLinearLayout1.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		localLinearLayout1.setOrientation(LinearLayout.VERTICAL);
		mScrollView.addView(localLinearLayout1);
		localLinearLayout1.setPadding(ScreenUtil.dip2px(context, 5f),
				ScreenUtil.dip2px(context, 5f), ScreenUtil.dip2px(context, 5f),
				0);
		Calendar localCalendar1 = (Calendar) mDatepickerParam.startDate.clone();
		Calendar calendarToday = (Calendar) localCalendar1.clone();
		Calendar calendarTomorrow = (Calendar) localCalendar1.clone();
		calendarTomorrow.add(Calendar.DAY_OF_MONTH, 1);
		Calendar calendarTwoMore = (Calendar) localCalendar1.clone();
		calendarTwoMore.add(Calendar.DAY_OF_MONTH, 2);
		Calendar selectedCalendar = (Calendar) mDatepickerParam.selectedDay
				.clone();
		int yearOfLocalCalendar1 = localCalendar1.get(Calendar.YEAR);
		int monthOfLocalCalendar1 = localCalendar1.get(Calendar.MONTH);
		Calendar localCalendarEnd = null;
		if(limitDateCal==null){
			localCalendarEnd = (Calendar) mDatepickerParam.startDate.clone();
			localCalendarEnd.add(Calendar.DAY_OF_MONTH,mDatepickerParam.dateRange - 1);
		}else {
			localCalendarEnd=limitDateCal;
		}

		int yearOfLocalCalendar2 = localCalendarEnd.get(Calendar.YEAR);
		int monthOfLocalCalendar2 = localCalendarEnd.get(Calendar.MONTH);

		int differOfYear = yearOfLocalCalendar2 - yearOfLocalCalendar1;
		int differOfMonth = monthOfLocalCalendar2 - monthOfLocalCalendar1;

		// 涉及到的月份数
		int totalDiffer = differOfYear * 12 + differOfMonth + 1;

		for (int i = 0; i < totalDiffer; i++) {
			LinearLayout localLinearLayout2 = (LinearLayout) View.inflate(
					context, R.layout.date_pick_head, null);
			localLinearLayout1.addView(localLinearLayout2,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			TextView localTextView1 = (TextView) localLinearLayout2
					.findViewById(R.id.tv_cal_year);
			TextView localTextView2 = (TextView) localLinearLayout2
					.findViewById(R.id.tv_cal_month);
			Calendar tempCalendar = (Calendar) localCalendar1.clone();
			tempCalendar.add(Calendar.YEAR, i / 11);// TODO
			localTextView1.setText(tempCalendar.get(Calendar.YEAR) + "年");
			Calendar tempCalendar2 = (Calendar) localCalendar1.clone();
			tempCalendar2.add(Calendar.MONTH, i);
			localTextView2.setText(tempCalendar2.get(Calendar.MONTH) + 1 + "月");
			tempCalendar2.set(Calendar.DAY_OF_MONTH, 1);
			// 星期天-星期六 Calendar.DAY_OF_WEEK = 1-7
			int weekOfDay = tempCalendar2.get(Calendar.DAY_OF_WEEK) - 1;
			int maxOfMonth = tempCalendar2
					.getActualMaximum(Calendar.DAY_OF_MONTH);
			int lines = (int) Math.ceil((weekOfDay + maxOfMonth) / 7.0f);
			// 开始日期之前和结束日期之后的变灰
			int startDay = localCalendar1.get(Calendar.DAY_OF_MONTH);

			for (int j = 0; j < lines; j++) {
				LinearLayout oneLineLinearLayout = getOneLineDayLinearLayout();
				if (j == 0) {// 第一行
					for (int k = 0; k < 7; k++) {
						RelativeLayout localSelectedRela = ((RelativeLayout) oneLineLinearLayout.getChildAt(k));
						TextView localTextViewSelected = (TextView) localSelectedRela
								.getChildAt(0);
						TextView localTextViewSelectedLunar = (TextView) localSelectedRela
								.getChildAt(1);
						if (k >= weekOfDay) {
							int index = k - weekOfDay + 1;
							localTextViewSelected.setText(index + "");
							Calendar tempCalendar3 = (Calendar) tempCalendar2.clone();
							tempCalendar3.set(Calendar.DAY_OF_MONTH, index);
							localTextViewSelectedLunar.setText(LauarUtil.getLunarDay(""+tempCalendar3.get(Calendar.YEAR),""+(tempCalendar3.get(Calendar.MONTH) + 1),""+tempCalendar3.get(Calendar.DAY_OF_MONTH)));
							String date = tempCalendar3.get(Calendar.YEAR)
									+ "-"
									+ (tempCalendar3.get(Calendar.MONTH) + 1)
									+ "-"
									+ tempCalendar3.get(Calendar.DAY_OF_MONTH);

							localSelectedRela.setTag(Long.valueOf(tempCalendar3
									.getTimeInMillis()));

							if (compareCal(tempCalendar3, calendarToday) == -1) {// 小于当天
								localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localSelectedRela.setEnabled(false);
							}

							if (Constants.HOLIDAYS.get(date) != null) {
								localTextViewSelected.setText(Constants.HOLIDAYS.get(date));
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
								localTextViewSelected.setTextColor(getTextColorGreen());
							}

							if (compareCal(tempCalendar3, calendarToday) == 0) {// 今天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("今天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}
							if (compareCal(tempCalendar3, calendarTomorrow) == 0) {// 明天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("明天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}
							if (compareCal(tempCalendar3, calendarTwoMore) == 0) {// 后天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("后天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}

							if (compareCal(tempCalendar3, selectedCalendar) == 0) {// 选择日
								localSelectedRela.setSelected(true);
								mLinearLayoutSelected = localLinearLayout2;
							}

							if (compareCal(tempCalendar3, localCalendarEnd) == 1) {// 大于截止日
								localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localSelectedRela.setEnabled(false);
							}

						} else {
							localSelectedRela.setVisibility(View.INVISIBLE);
						}
					}
				} else if (j == lines - 1) {// 最后一行
					int temp = maxOfMonth - (lines - 2) * 7 - (7 - weekOfDay);
					for (int k = 0; k < 7; k++) {
						RelativeLayout localSelectedRela = ((RelativeLayout) oneLineLinearLayout.getChildAt(k));
						TextView localTextViewSelected = (TextView) localSelectedRela.getChildAt(0);
						TextView localTextViewSelectedLunar = (TextView) localSelectedRela.getChildAt(1);
						if (k < temp) {
							int index = (7 - weekOfDay) + (j - 1) * 7 + k + 1;
							localTextViewSelected.setText(index + "");
							Calendar tempCalendar3 = (Calendar) tempCalendar2
									.clone();
							tempCalendar3.set(Calendar.DAY_OF_MONTH, index);
//							localTextViewSelectedLunar.setText(LauarUtil.getLunarDay(tempCalendar3));
							localTextViewSelectedLunar.setText(LauarUtil.getLunarDay(""+tempCalendar3.get(Calendar.YEAR),""+(tempCalendar3.get(Calendar.MONTH) + 1),""+tempCalendar3.get(Calendar.DAY_OF_MONTH)));
							String date = tempCalendar3.get(Calendar.YEAR)
									+ "-"
									+ (tempCalendar3.get(Calendar.MONTH) + 1)
									+ "-"
									+ tempCalendar3.get(Calendar.DAY_OF_MONTH);
							localSelectedRela.setTag(Long.valueOf(tempCalendar3.getTimeInMillis()));
							if (compareCal(tempCalendar3, calendarToday) == -1) {// 小于当天
								localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localSelectedRela.setEnabled(false);
							}
							if (Constants.HOLIDAYS.get(date) != null) {
								localTextViewSelected.setText(Constants.HOLIDAYS.get(date));
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
								localTextViewSelected.setTextColor(getTextColorGreen());
							}

							if (compareCal(tempCalendar3, calendarToday) == 0) {// 今天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("今天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}
							if (compareCal(tempCalendar3, calendarTomorrow) == 0) {// 明天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("明天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}
							if (compareCal(tempCalendar3, calendarTwoMore) == 0) {// 后天
								localTextViewSelected.setTextColor(getTextColorRed());
								localTextViewSelected.setText("后天");
								localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
							}

							if (compareCal(tempCalendar3, selectedCalendar) == 0) {// 选择日
								localSelectedRela.setSelected(true);
								mLinearLayoutSelected = localLinearLayout2;

							}
							if (compareCal(tempCalendar3, localCalendarEnd) == 1) {// 大于截止日
								localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
								localSelectedRela.setEnabled(false);
							}

						} else {
							localSelectedRela.setVisibility(View.INVISIBLE);
						}
					}

				} else {// 中间
					for (int k = 0; k < 7; k++) {
						// TextView localTextView = (TextView)
						// oneLineLinearLayout
						// .getChildAt(k);
						RelativeLayout localSelectedRela = (RelativeLayout) oneLineLinearLayout.getChildAt(k);
						TextView localTextViewSelected = (TextView) localSelectedRela.getChildAt(0);
						TextView localTextViewSelectedLunar = (TextView) localSelectedRela.getChildAt(1);
						int index = (7 - weekOfDay) + (j - 1) * 7 + k + 1;
						localTextViewSelected.setText(index + "");
						Calendar tempCalendar3 = (Calendar) tempCalendar2
								.clone();
						tempCalendar3.set(Calendar.DAY_OF_MONTH, index);
						localTextViewSelectedLunar.setText(LauarUtil.getLunarDay(""+tempCalendar3.get(Calendar.YEAR),""+(tempCalendar3.get(Calendar.MONTH) + 1),""+tempCalendar3.get(Calendar.DAY_OF_MONTH)));
//						localTextViewSelectedLunar.setText(LauarUtil.getLunarDay(tempCalendar3));
						String date = tempCalendar3.get(Calendar.YEAR) + "-"
								+ (tempCalendar3.get(Calendar.MONTH) + 1) + "-"
								+ tempCalendar3.get(Calendar.DAY_OF_MONTH);
						localSelectedRela.setTag(Long.valueOf(tempCalendar3
								.getTimeInMillis()));
						if (compareCal(tempCalendar3, calendarToday) == -1) {// 小于当天
							localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
							localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
							localSelectedRela.setEnabled(false);
						}
						if (Constants.HOLIDAYS.get(date) != null) {
							localTextViewSelected.setText(Constants.HOLIDAYS.get(date));
							localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
							localTextViewSelected.setTextColor(getTextColorGreen());
						}

						if (compareCal(tempCalendar3, calendarToday) == 0) {// 今天
							localTextViewSelected.setTextColor(getTextColorRed());
							localTextViewSelected.setText("今天");
							localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
						}
						if (compareCal(tempCalendar3, calendarTomorrow) == 0) {// 明天
							localTextViewSelected.setTextColor(getTextColorRed());
							localTextViewSelected.setText("明天");
							localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
						}
						if (compareCal(tempCalendar3, calendarTwoMore) == 0) {// 后天
							localTextViewSelected.setTextColor(getTextColorRed());
							localTextViewSelected.setText("后天");
							localTextViewSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
						}

						if (compareCal(tempCalendar3, selectedCalendar) == 0) {// 选择日
							localSelectedRela.setSelected(true);
							mLinearLayoutSelected = localLinearLayout2;

						}
						if (compareCal(tempCalendar3, localCalendarEnd) == 1) {// 大于截止日
							localTextViewSelected.setTextColor(getResources().getColor(R.color.calendar_color_gray));
							localTextViewSelectedLunar.setTextColor(getResources().getColor(R.color.calendar_color_gray));
							localSelectedRela.setEnabled(false);
						}

					}
				}
				localLinearLayout1.addView(oneLineLinearLayout);
			}
		}
	}

	/**
	 * 获取一行 七天的LinearLayout
	 * 
	 * @return
	 */
	private LinearLayout getOneLineDayLinearLayout() {
		LinearLayout localLinearLayout = new LinearLayout(this);
		localLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		localLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < 7; i++) {
			float height = (ScreenUtil.WIDTH
					- ScreenUtil.dip2px(context, 10f) - ScreenUtil.dip2px(
					context, 1.5f * 6)) / 7;
			LinearLayout.LayoutParams localLayoutParams4 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, (int) height, 1.0F);
			RelativeLayout localRelativeLayout = new RelativeLayout(context);
			localRelativeLayout.setLayoutParams(localLayoutParams4);
			localLayoutParams4.setMargins(ScreenUtil.dip2px(this, 1.5F),
					ScreenUtil.dip2px(this, 1.5F),
					ScreenUtil.dip2px(this, 1.5F),
					ScreenUtil.dip2px(this, 1.5F));

			localRelativeLayout.setOnClickListener(this);
			localRelativeLayout.setBackgroundDrawable(getBackGroundDrawable());
			TextView localTextView1 = new TextView(this);
			localTextView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0F);
			localTextView1.setId(R.id.date_page_one);
			localTextView1.setTextColor(getTextColorBlack());
			TextView localTextView2 = new TextView(this);
			localTextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0F);
			localTextView2.setTextColor(getTextColorBlack());
			RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			localLayoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
			localLayoutParams2.topMargin = ScreenUtil.dip2px(context, 4f);
			localRelativeLayout.addView(localTextView1, 0, localLayoutParams2);
			RelativeLayout.LayoutParams localLayoutParams3 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			localLayoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
			localLayoutParams3.addRule(RelativeLayout.BELOW, R.id.date_page_one);
			localRelativeLayout.addView(localTextView2, 1, localLayoutParams3);

			localLinearLayout.addView(localRelativeLayout, i);

		}
		return localLinearLayout;
	}

	@Override
	public void onClick(View paramView) {
		if (paramView.getTag() != null) {
			Calendar localCalendar = Calendar.getInstance();
			localCalendar.setTimeInMillis(((Long) paramView.getTag()).longValue());
			/*Toast.makeText(
					context,
					localCalendar.get(Calendar.YEAR) + "年"
							+ (localCalendar.get(Calendar.MONTH) + 1) + "月"
							+ localCalendar.get(Calendar.DAY_OF_MONTH) + "日",
					Toast.LENGTH_SHORT).show();*/
			setResult(RESULT_OK, getIntent().putExtra("pickedDate", localCalendar));
			finish();  
		}else {
			super.onClick(paramView);
		}
	}

	/**
	 * 比较两个日期的大小
	 * 
	 * @param paramCalendar1
	 * @param paramCalendar2
	 * @return
	 */
	private int compareCal(Calendar paramCalendar1, Calendar paramCalendar2) {
		if (paramCalendar1.get(Calendar.YEAR) > paramCalendar2
				.get(Calendar.YEAR)) {
			return 1;
		} else if (paramCalendar1.get(Calendar.YEAR) < paramCalendar2
				.get(Calendar.YEAR)) {
			return -1;
		} else {
			if (paramCalendar1.get(Calendar.MONTH) > paramCalendar2
					.get(Calendar.MONTH)) {
				return 1;
			} else if (paramCalendar1.get(Calendar.MONTH) < paramCalendar2
					.get(Calendar.MONTH)) {
				return -1;
			} else {
				if (paramCalendar1.get(Calendar.DAY_OF_MONTH) > paramCalendar2
						.get(Calendar.DAY_OF_MONTH)) {
					return 1;
				} else if (paramCalendar1.get(Calendar.DAY_OF_MONTH) < paramCalendar2
						.get(Calendar.DAY_OF_MONTH)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	/**
	 * 点击背景切换
	 * 
	 * @return
	 */
	private StateListDrawable getBackGroundDrawable() {
		// 获取对应的属性值 Android框架自带的属性 attr
		int pressed = android.R.attr.state_pressed;
		int enabled = android.R.attr.state_enabled;
		int selected = android.R.attr.state_selected;

		StateListDrawable localStateListDrawable = new StateListDrawable();
		ColorDrawable localColorDrawable1 = new ColorDrawable(context
				.getResources().getColor(android.R.color.transparent));
		// ColorDrawable localColorDrawable2 = new ColorDrawable(context
		// .getResources().getColor(R.color.blue));
		Drawable localColorDrawable2 = context.getResources().getDrawable(
				R.drawable.bg_calendar_seleced);
		ColorDrawable localColorDrawable3 = new ColorDrawable(context
				.getResources().getColor(android.R.color.transparent));
		localStateListDrawable.addState(new int[] { pressed, enabled },
				localColorDrawable2);
		localStateListDrawable.addState(new int[] { selected, enabled },
				localColorDrawable2);
		localStateListDrawable.addState(new int[] { enabled },
				localColorDrawable1);
		localStateListDrawable.addState(new int[0], localColorDrawable3);
		return localStateListDrawable;
	}

	/**
	 * 字体颜色 切换
	 * 
	 * @return
	 */
	private ColorStateList getTextColorBlack()

	{
		return new ColorStateList(new int[][] { { pressed, enabled },
				{ selected, enabled }, { enabled }, new int[0] }, new int[] {
				-1, -1,
				context.getResources().getColor(R.color.calendar_color_black),
				context.getResources().getColor(R.color.calendar_color_white) });
	}

	private ColorStateList getTextColorRed()

	{
		return new ColorStateList(new int[][] { { pressed, enabled },
				{ selected, enabled }, { enabled }, new int[0] }, new int[] {
				-1, -1,
				context.getResources().getColor(R.color.calendar_color_orange),
				context.getResources().getColor(R.color.calendar_color_white) });
	}

	private ColorStateList getTextColorGreen()

	{
		return new ColorStateList(new int[][] { { pressed, enabled },
				{ selected, enabled }, { enabled }, new int[0] }, new int[] {
				-1, -1,
				context.getResources().getColor(R.color.calendar_color_green),
				context.getResources().getColor(R.color.calendar_color_white) });
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			scrollHeight = mLinearLayoutSelected.getTop();
			mHandler.sendEmptyMessageDelayed(11, 100l);
		}
		
	}
	
	
	public static void invoke(Activity context,String selectedDate,Calendar limitDateCal){
		Intent intent=new Intent(context,DateSelectingActivity.class);
		intent.putExtra("selected_date", selectedDate);
		intent.putExtra("limit_date",limitDateCal);
		context.startActivityForResult(intent, 11);
	}
	
	
	public class DatepickerParam implements Serializable {
		public Calendar selectedDay = null;
		public Calendar startDate = null;
		//售票范围
		public int dateRange = 0;
		public String title = "出发日期";
	}
	

}
