package com.bolaa.medical.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.base.BaseFragmentActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;

public class HospitalDetailActivityV2 extends BaseFragmentActivity{
	
	private String name;
	private String id;
	
	private TextView btnMakeAppointment;

	RadioGroup rgTitle;
	FragmentManager mFm;
	Fragment fragments[]=new Fragment[3];
	private int curPosition=0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		initData();
		setListener();
	}
	

	private void initData() {
		// TODO Auto-generated method stub
//		fragments[0]= HospitalDescribeFragment.createInstance(id);
//		fragments[1]= HospitalComboFragment.createInstance(id);
//		fragments[2]= HospitalEvaluateFragment.createInstance(id);
		rgTitle.check(rgTitle.getChildAt(0).getId());
		switchFragment(0);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		btnMakeAppointment.setOnClickListener(this);
		rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for(int i=0;i<group.getChildCount();i++){
					if(group.getChildAt(i).getId()==checkedId){
						switchFragment(i);
					}
				}
			}
		});
	}

	//切换fragment
	private void switchFragment(int position){
		FragmentTransaction ft=mFm.beginTransaction();
		//添加fragment到fm里(添进去就不remove)
		if(mFm.findFragmentByTag(""+position)==null){
			ft.add(R.id.content,fragments[position],""+position);
		}
		//控制fragment显示/隐藏
		for(int i=0;i<fragments.length;i++){
			if(position==i){
				ft.show(fragments[i]);
			}else {
				if(fragments[i].isAdded()){
					ft.hide(fragments[i]);
				}
			}
		}
		ft.commit();
		curPosition=position;
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_hospital_detail_v2, false, true);
		setTitleText("", name, 0, true);
		btnMakeAppointment=(TextView)findViewById(R.id.btn_make_appointment);
		rgTitle=(RadioGroup)findViewById(R.id.rg_hospital_detail);

	}

	private void setExtra() {
		// TODO Auto-generated method stub
		mFm=getSupportFragmentManager();
		Intent intent=getIntent();
		name=intent.getStringExtra("h_name");
		id=intent.getStringExtra("h_id");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnMakeAppointment){
			ComboSelectActivity.invoke(this,id);
		}else {
			
			super.onClick(v);
		}
	}



	public static void invoke(Context context,Hospital hospital){
		Intent intent=new Intent(context,HospitalDetailActivityV2.class);
		if(hospital!=null){
			intent.putExtra("h_name", hospital.hospital_name);
			intent.putExtra("h_id", hospital.h_id);
		}
		context.startActivity(intent);
	}
	
	
	
	

}
