package com.bolaa.medical.ui;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.TimeSelectingAdapter;
import com.bolaa.medical.adapter.TimeSelectingAdapter.OnAppointmentListener;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.BookInDay;
import com.bolaa.medical.model.BookInDay.Book;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.DateTimeUtils;
import com.bolaa.medical.utils.DateUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeSelectingActivity extends BaseListActivity{
	private String h_id;
	private String combo_id;
	private BookInDay bookInDay;
	private String curDate;
	private TextView tvTomorrow;
	private TextView tvYestoday;
	private TextView tvCurrent;
	private Calendar curCalendar;
	private String today;
	private String limitDay;//可以预约的最后一天
	
	private int maxDay=30; //最多可以预约多少天
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		h_id=intent.getStringExtra("h_id");
		combo_id=intent.getStringExtra("combo_id");
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_time_selecting, false, true);
		mLoadStateController=new LoadStateController(this, (ViewGroup)findViewById(R.id.layout_load_state_container));
		hasLoadingState=true;
		setTitleTextRightText("", "预约时间", "选择日期", true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		tvYestoday=(TextView)findViewById(R.id.tv_date_left);
		tvTomorrow=(TextView)findViewById(R.id.tv_date_right);
		tvCurrent=(TextView)findViewById(R.id.tv_date_middle);
		tvYestoday.setEnabled(false);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new TimeSelectingAdapter(this);
		mListView.setAdapter(mAdapter);
		curDate=DateUtil.getYMDDate(new Date());
		today=curDate;
		curCalendar=DateTimeUtils.getCurrentDateTime();
		Calendar limitC=(Calendar)curCalendar.clone();
		limitC.add(Calendar.DAY_OF_MONTH, maxDay-1);
		limitDay=DateUtil.getYMDDate(limitC.getTime());
		changeDateView();
	}
	
	private void setListener(){
		tvTomorrow.setOnClickListener(this);
		tvYestoday.setOnClickListener(this);
		((TimeSelectingAdapter)mAdapter).setOnAppointmentListener(new OnAppointmentListener() {

			@Override
			public void onClick(Book book) {
				// TODO Auto-generated method stub
				if (AppStatic.getInstance().isLogin) {
//					makeAnAppointment(book);
					PayTestActivity.invoke(TimeSelectingActivity.this,curDate,h_id,book.ha_o_id,combo_id);
				} else {
					UserLoginActivity.invoke(TimeSelectingActivity.this);
				}
			}
		});
	}
	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append("date", curDate);
		params.append("package_id", combo_id);
		params.append("h_id", h_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_APPOINTMENT_DAY_INFO),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<BookInDay> object=GsonParser.getInstance().parseToObj(result, BookInDay.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							showSuccess();
							bookInDay=object.data;
							mAdapter.setList(bookInDay.info_list);
							mAdapter.notifyDataSetChanged();
							setLimitAndStartDate();
						}else {
							showNodata();
						}
					}else {
						showFailture();
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				}else {
					showFailture();
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}
			}
		});
	}
	
	private void makeAnAppointment(final Book book){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("date", curDate);
		params.append("h_id", h_id);
		params.append("ha_o_id", book.ha_o_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_MAKE_AN_APPOINTMENT),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if (!isFinishing()) {
					DialogUtil.dismissDialog(lodDialog);
				}
				if(status==200){
					BaseObject<Order> object=GsonParser.getInstance().parseToObj(result, Order.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							//提交成功  跳转到支付页面
//							WayPayActivity.invoke(TimeSelectingActivity.this, object.data);
							PayTestActivity.invoke(TimeSelectingActivity.this, curDate,h_id,book.ha_o_id,combo_id);
//							AppUtil.showToast(getApplicationContext(), "暂未接入支付");
						}else {
							AppUtil.showToast(getApplicationContext(), object.msg);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}
			}
		});
	}

	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadError(String message, Throwable throwable, int page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub
		DateSelectingActivity.invoke(this, curDate,DateTimeUtils.getCalendar(limitDay));
	}
	
	private String getFuckingDate(String date){
		
		return date.substring(5);
	}

	private void setLimitAndStartDate(){
		if(!AppUtil.isNull(bookInDay.day_time_end)&&!bookInDay.day_time_end.equals("0")){
			Calendar calendarEnd=DateTimeUtils.getCalendar(bookInDay.day_time_end);
			if(calendarEnd.after(DateTimeUtils.getCalendar(today))){//服务器设置的最大日期在今天之后，所以有效
				limitDay=bookInDay.day_time_end;
			}
		}
		//需求还不明确
//		if(!AppUtil.isNull(bookInDay.day_time_start)&&!bookInDay.day_time_start.equals("0")){
//			curCalendar=DateTimeUtils.getCalendar(bookInDay.day_time_start);
//			curDate=DateUtil.getYMDDate(curCalendar.getTime());
//		}
	}

	private void changeDateView(){
		if(curDate.equals(today)){
			tvYestoday.setText("无");
			tvCurrent.setText("今天");
			Calendar tomorrowC=(Calendar)curCalendar.clone();
			tomorrowC.add(Calendar.DAY_OF_MONTH, 1);
			tvTomorrow.setText(getFuckingDate(DateUtil.getYMDDate(tomorrowC.getTime())));
			tvTomorrow.setEnabled(true);
			tvYestoday.setEnabled(false);
		}else if (curDate.equals(limitDay)) {
			tvCurrent.setText(getFuckingDate(curDate));
			tvTomorrow.setText("暂不开放");
			Calendar yestodayC=(Calendar)curCalendar.clone();
			yestodayC.add(Calendar.DAY_OF_MONTH, -1);
			String yestoday=DateUtil.getYMDDate(yestodayC.getTime());
			tvYestoday.setText(yestoday.equals(today)?"今天":getFuckingDate(yestoday));
			tvTomorrow.setEnabled(false);
			tvYestoday.setEnabled(true);
		}else {
			tvCurrent.setText(getFuckingDate(curDate));
			Calendar yestodayC=(Calendar)curCalendar.clone();
			yestodayC.add(Calendar.DAY_OF_MONTH, -1);
			String yestoday=DateUtil.getYMDDate(yestodayC.getTime());
			tvYestoday.setText(yestoday.equals(today)?"今天":getFuckingDate(yestoday));
			Calendar tomorrowC=(Calendar)curCalendar.clone();
			tomorrowC.add(Calendar.DAY_OF_MONTH, 1);
			tvTomorrow.setText(getFuckingDate(DateUtil.getYMDDate(tomorrowC.getTime())));
			tvTomorrow.setEnabled(true);
			tvYestoday.setEnabled(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==tvYestoday){
			curCalendar.add(Calendar.DAY_OF_MONTH, -1);
			curDate=DateUtil.getYMDDate(curCalendar.getTime());
			initData(false);
			changeDateView();
		}else if (v==tvTomorrow) {
			curCalendar.add(Calendar.DAY_OF_MONTH, 1);
			curDate=DateUtil.getYMDDate(curCalendar.getTime());
			initData(false);
			changeDateView();
		}else {
			super.onClick(v);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==11){
			if(resultCode==RESULT_OK){
				curCalendar=(Calendar)data.getSerializableExtra("pickedDate");
				curDate=DateUtil.getYMDDate(curCalendar.getTime());
				initData(false);
				changeDateView();
			}
		}
	}
	
	public static void invoke(Context context,Hospital hospital){
		Intent intent=new Intent(context,TimeSelectingActivity.class);
		intent.putExtra("h_id", hospital.h_id);
		context.startActivity(intent);
	}

	public static void invoke(Context context,String h_id,String comboId){
		Intent intent=new Intent(context,TimeSelectingActivity.class);
		intent.putExtra("h_id", h_id);
		intent.putExtra("combo_id", comboId);
		context.startActivity(intent);
	}

}
