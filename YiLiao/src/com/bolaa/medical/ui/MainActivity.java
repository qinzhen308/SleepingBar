package com.bolaa.medical.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseFragmentActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.model.wrapper.OrderWraper;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 首页
 * 
 * @author paulz
 * 
 */
public class MainActivity extends BaseFragmentActivity implements
		OnClickListener {
	
	Handler mHandler=new Handler();
	private int mClickCount;
	
	private ViewGroup layoutMain;
	private ViewGroup layoutRight;
	private DrawerLayout mDrawerLayout;
	
	private ListView lvMenu;
	private ImageView ivAvatar;
	private ImageView ivAppointment;
	private ImageView ivMenu;
	private TextView btnScore;
	private TextView tvLogin;
	private TextView btnMyAppintment;
	private TextView tvLookReport;
	private TextView tvStatus;
	private TextView tvStatusEnglish;
	private TextView tvReportDate;
	
	private int maxX;
	
	private MenuAdapter mAdapter;
	private Order nearlyReport;//最近的报告
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setListener();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent!=null){
			if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
		}
	}

	private void setListener() {
		// TODO Auto-generated method stub
		ivAppointment.setOnClickListener(this);
		btnScore.setOnClickListener(this);
		tvLogin.setOnClickListener(this);
		ivMenu.setOnClickListener(this);
		ivAvatar.setOnClickListener(this);
		btnMyAppintment.setOnClickListener(this);
		tvLookReport.setOnClickListener(this);
		
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				LogUtil.d("drawer---onDrawerStateChanged----"+arg0);
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				LogUtil.d("drawer---onDrawerSlide----"+arg0+"--"+arg1);
				layoutMain.scrollTo((int)(maxX*arg1), layoutMain.getTop());
				
			}
			
			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				
				LogUtil.d("drawer---onDrawerOpened----view="+arg0);
				
			}
			
			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				LogUtil.d("drawer---onDrawerClosed----view="+arg0);
				
			}
		});
		
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Image13Loader.getInstance().clearMemoryCache();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(AppStatic.getInstance().isLogin&&AppStatic.getInstance().getmUserInfo()!=null){
			Image13Loader.getInstance().loadImage(AppStatic.getInstance().getmUserInfo().avatar, ivAvatar, R.drawable.ic_user);
			getReportNearly();
			tvLogin.setVisibility(View.INVISIBLE);
		}else {
			ivAvatar.setImageResource(R.drawable.ic_user);
			tvLogin.setVisibility(View.VISIBLE);
			tvStatus.setText("尚未体检");
//			tvStatusEnglish.setText("—— Not yet physical examination  ——");
			tvReportDate.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			maxX=layoutRight.getWidth();
		}
	}
	
	private void initView() {
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		layoutMain=(ViewGroup)findViewById(R.id.layout_main);
		layoutRight=(ViewGroup)findViewById(R.id.layout_right);
		lvMenu=(ListView)layoutRight.findViewById(R.id.lv_menu);
		ivAvatar=(ImageView)layoutRight.findViewById(R.id.iv_avatar);
		ivAppointment=(ImageView)layoutMain.findViewById(R.id.iv_make_appointment);
		btnScore=(TextView)layoutMain.findViewById(R.id.btn_health_score);
		tvLogin=(TextView)layoutRight.findViewById(R.id.tv_login);
		ivMenu=(ImageView)layoutMain.findViewById(R.id.iv_menu);
		btnMyAppintment=(TextView)layoutMain.findViewById(R.id.btn_my_appintment);
		tvLookReport=(TextView)layoutMain.findViewById(R.id.tv_look_report);
		tvStatus=(TextView)layoutMain.findViewById(R.id.tv_status);
		tvStatusEnglish=(TextView)layoutMain.findViewById(R.id.tv_status_english);
		tvReportDate=(TextView)layoutMain.findViewById(R.id.tv_report_date);
		
		initMenu();
	}
	
	private void initMenu(){
		List<MenuItem> list=new ArrayList<MainActivity.MenuItem>();
		list.add(new MenuItem(0,"我的信息"));
		list.add(new MenuItem(1,"预约体检"));
		list.add(new MenuItem(2,"体检报告"));
		list.add(new MenuItem(3,"健康积分"));
		list.add(new MenuItem(4,"优惠券"));
		list.add(new MenuItem(5,"我的账户"));
		list.add(new MenuItem(6,"设置"));
		mAdapter=new MenuAdapter(this);
		mAdapter.setList(list);
		lvMenu.setAdapter(mAdapter);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			return;
		}

		if (mClickCount++ < 1) {
			AppUtil.showToast(this, "再按一次就退出");
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mClickCount = 0;
				}
			}, 2000);
			return;
		}

		mHandler.removeCallbacksAndMessages(null);
		HApplication.getInstance().exit();
		super.onBackPressed();
	}
	
	private void setLoginInfo(){
		
	}
	
	/**
	 * 获取最近的体检报告
	 */
	private void getReportNearly(){
		ParamBuilder params=new ParamBuilder();
		params.append("page_size", 1);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REPORT_LIST),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<OrderWraper> object=GsonParser.getInstance().parseToObj(result, OrderWraper.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK&&!AppUtil.isEmpty(object.data.order_list)){
							nearlyReport=object.data.order_list.get(0);
							tvStatus.setText(AppUtil.isNull(nearlyReport.health_statu_str)?"未知":nearlyReport.health_statu_str);
//							tvStatusEnglish.setText("—— "+nearlyReport.health_statu_str_en+" ——");
							tvReportDate.setText("体检日期："+nearlyReport.day_time);
							tvReportDate.setVisibility(View.VISIBLE);
						}else {
						}
					}else {
					}
				}else {
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==ivAppointment){
			MakingAppointmentActivity.invoke(this);
		}else if (v==btnScore) {
			if(!AppStatic.getInstance().isLogin){
				UserLoginActivity.invokeForResult(this, 5);
			}else {
				MyScoreActivity.invoke(this);
			}
		}else if (v==ivAvatar) {
			if(!AppStatic.getInstance().isLogin){
				UserLoginActivity.invokeForResult(this, 2);
			}else {
				MyInfoActivity.invoke(this);
			}
		}else if(v==tvLogin){
			UserLoginActivity.invokeForResult(this, 1);
		}else if (v==ivMenu) {
			mDrawerLayout.openDrawer(Gravity.RIGHT);
		}else if (v==btnMyAppintment) {
			if(!AppStatic.getInstance().isLogin){
				UserLoginActivity.invokeForResult(this, 3);
			}else {
				AlreadyReservationActivity.invoke(this,"我的预约");
			}
		}else if (v==tvLookReport) {
			if(!AppStatic.getInstance().isLogin){
				UserLoginActivity.invokeForResult(this, 4);
			}else {
				Intent intent=new Intent(this,MedicalReportActivity.class);
				startActivity(intent);
			}
			
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0==1){
			if(arg1==RESULT_OK){
				setLoginInfo();
			}
		}
		if(arg0==2){
			if(arg1==RESULT_OK){
				
			}
		}
		if(arg0==3){
			if(arg1==RESULT_OK){
				
			}
		}
		if(arg0==4){//查看体检报告
			if(arg1==RESULT_OK){
				Intent intent=new Intent(this,MedicalReportActivity.class);
				startActivity(intent);
			}
		}
		if(arg0==5){//健康积分
			if(arg1==RESULT_OK){
				MyScoreActivity.invoke(this);
			}
		}
	}


	public static void invoke(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
	
	public class MenuAdapter extends AbstractListAdapter<MenuItem>{


		public MenuAdapter(Activity context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			TextView tView=new TextView(mContext);
			final MenuItem item=mList.get(i);
			tView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,(int)mContext.getResources().getDimension(R.dimen.text_height)));
			tView.setGravity(Gravity.CENTER_VERTICAL);
			tView.setText(item.title);
			tView.setPadding(ScreenUtil.dip2px(mContext, 20), 0, 0, 0);
			tView.setBackgroundResource(R.drawable.selector_home_right_list);
			tView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			try {
				tView.setTextColor(ColorStateList.createFromXml(mContext.getResources(), mContext.getResources().getXml(R.color.btn_menu_item)));
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					item.onClick(mContext);
				}
			});
			return tView;
		}
		
	}
	
	public Class[] activities={MyInfoActivity.class,AlreadyReservationActivity.class,MedicalReportActivity.class,MyScoreActivity.class,CouponListActivity.class,MyAccountActivity.class,SettingsActivity.class};
	public class MenuItem{
		public int position;
		public String title;
		
		public MenuItem(int position,String title){
			this.position=position;
			this.title=title;
		}
		
		public void onClick(Context context){
			if(position==6||AppStatic.getInstance().isLogin){
				Intent intent=new Intent(context,activities[position]);
				context.startActivity(intent);
			}else {
				AppUtil.showToast(getApplicationContext(), "请先登录");
			}
		}
	}

}