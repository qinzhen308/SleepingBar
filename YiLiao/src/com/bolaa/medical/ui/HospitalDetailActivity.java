package com.bolaa.medical.ui;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class HospitalDetailActivity extends BaseActivity{
	
	private String name;
	private String id;
	
	private ImageView ivBanner;
	private TextView tvContent;
	private TextView tvName;
	private TextView btnMakeAppointment;
	
	private Hospital mDetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
		
	}
	
	private void handleData(){
		Image13Loader.getInstance().loadImageFade(mDetail.banner_img, ivBanner);
		tvContent.setText(Html.fromHtml(mDetail.hospital_desc));
		tvName.setText(mDetail.hospital_name);
	}

	private void initData() {
		// TODO Auto-generated method stub
		showLoading();
		ParamBuilder params=new ParamBuilder();
		params.append("h_id", id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_HOSPITAL_DETAIL), new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<Hospital> baseObject=GsonParser.getInstance().parseToObj(result, Hospital.class);
					if(baseObject!=null&&baseObject.data!=null){
						showSuccess();
						mDetail=baseObject.data;
						handleData();
					}else {
						showNodata();
					}
				}else {
					showFailture();
				}
			}
		});
	}

	private void setListener() {
		// TODO Auto-generated method stub
		btnMakeAppointment.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_hospital_detail, true, true);
		setTitleText("", name, 0, true);
		btnMakeAppointment=(TextView)findViewById(R.id.btn_make_appointment);
		ivBanner=(ImageView)findViewById(R.id.iv_pic);//0.571
		LayoutParams layoutParams=ivBanner.getLayoutParams();
		layoutParams.height=(int)(ScreenUtil.WIDTH*0.571);
		ivBanner.setLayoutParams(layoutParams);
		tvName=(TextView)findViewById(R.id.tv_name);
		tvContent=(TextView)findViewById(R.id.tv_content);
		
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		name=intent.getStringExtra("h_name");
		id=intent.getStringExtra("h_id");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnMakeAppointment){
			TimeSelectingActivity.invoke(this, mDetail);
		}else {
			
			super.onClick(v);
		}
	}
	
	
	public static void invoke(Context context,Hospital hospital){
		Intent intent=new Intent(context,HospitalDetailActivity.class);
		if(hospital!=null){
			intent.putExtra("h_name", hospital.hospital_name);
			intent.putExtra("h_id", hospital.h_id);
		}
		context.startActivity(intent);
	}
	
	
	
	

}
