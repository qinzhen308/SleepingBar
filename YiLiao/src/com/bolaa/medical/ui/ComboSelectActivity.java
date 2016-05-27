package com.bolaa.medical.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.adapter.HospitalComboAdapter;
import com.bolaa.medical.adapter.HospitalComboSelectAdapter;
import com.bolaa.medical.adapter.SettingListAdapter;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController;
import com.bolaa.medical.httputil.HttpRequester;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Combo;
import com.bolaa.medical.model.wrapper.ComboWraper;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.Image13Loader;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

import java.util.List;

public class ComboSelectActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener,LoadStateController.OnLoadErrorListener{

	private String h_id;
	private TextView tvMakeAppointment;



	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {

	}

	@Override
	protected void loadError(String message, Throwable throwable, int page) {

	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {

	}

	@Override
	protected void loadNoNet() {

	}

	@Override
	protected void loadServerError() {

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	private void setExtra(){
		Intent intent=getIntent();
		h_id=intent.getStringExtra("hospital_id");
	}

	public void initView() {
		setActiviyContextView(R.layout.activity_hospital_combo_select, true,true);
		setTitleText("","选择套餐",0,true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		tvMakeAppointment=(TextView)findViewById(R.id.btn_make_appointment);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new HospitalComboSelectAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	private void setListener() {
		mPullListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Combo combo=(Combo) mAdapter.getItem(position-mListView.getHeaderViewsCount());
				TimeSelectingActivity.invoke(ComboSelectActivity.this,h_id,combo.id);
			}
		});
		tvMakeAppointment.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==tvMakeAppointment){
			AppUtil.showToast(getApplicationContext(),"请选择套餐");
		}else {
			super.onClick(v);
		}
	}

	private void initData(final boolean isRefresh) {
		// TODO Auto-generated method stub
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append("h_id", h_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_COMBO_LIST), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
                if(isRefresh){
                    mPullListView.onRefreshComplete();
                }
				if(status==200){
					LogUtil.d("combolist="+result);
					BaseObject<ComboWraper> baseObject= GsonParser.getInstance().parseToObj(result, ComboWraper.class);
					if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
						if(baseObject.data.getItems().size()==0){
							showNodata();
						}else {
							showSuccess();
							mAdapter.setList(baseObject.data.getItems());
							mAdapter.notifyDataSetChanged();
						}
					}else {
						showNodata();
					}
				}else {
					showFailture();
				}
			}
		});
	}


	@Override
	public void onRefresh() {
		if(!isLoading()){
			initData(true);
		}
	}


	public static void invoke(Context context,String h_id){
		Intent intent=new Intent(context,ComboSelectActivity.class);
		intent.putExtra("hospital_id",h_id);
		context.startActivity(intent);
	}

	@Override
	public void onAgainRefresh() {
		initData(false);
	}
}
