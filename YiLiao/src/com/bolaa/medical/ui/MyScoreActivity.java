package com.bolaa.medical.ui;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.adapter.ScoreListAdapter;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.wrapper.ScoreWraper;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyScoreActivity extends BaseActivity{
	TextView btnScoreChange;
	TextView btnScoreHistory;
	TextView tvScoreTitle;
	TextView tvBack;
	ImageView ivHelp;
	ImageView layoutWall;
	ListView lvScore;
	ScoreListAdapter mAdapter;
	public int score;
	public float exchange_money;
	
	Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		initData();
		getScore();
	}

	private void initData() {
		// TODO Auto-generated method stub
		ParamBuilder params=new ParamBuilder();
		params.append("page_size", 3);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SCORE_HISTORY), new ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if (status == 200) {
					BaseObject<ScoreWraper> object = GsonParser.getInstance().parseToObj(result, ScoreWraper.class);
					if (object != null) {
						if (object.data != null && object.status == BaseObject.STATUS_OK) {
							mAdapter.setList(object.data.getItems());
							mAdapter.notifyDataSetChanged();
						} else {
						}
					} else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				} else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}
			}
		});
	}
	
	

	private void setListener() {
		// TODO Auto-generated method stub
		btnScoreChange.setOnClickListener(this);
		btnScoreHistory.setOnClickListener(this);
		ivHelp.setOnClickListener(this);
		tvBack.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_my_score, false, false);
		btnScoreHistory=(TextView)findViewById(R.id.btn_score_history);
		btnScoreChange=(TextView)findViewById(R.id.btn_score_change);
		tvScoreTitle=(TextView)findViewById(R.id.tv_score_title);
		tvBack=(TextView)findViewById(R.id.title_left);
		ivHelp=(ImageView)findViewById(R.id.title_right);
		layoutWall=(ImageView)findViewById(R.id.layout_wall);
		
		tvScoreTitle.setText(""+score+"分");
		lvScore=(ListView)findViewById(R.id.lv_score);
		mAdapter=new ScoreListAdapter(this);
		lvScore.setAdapter(mAdapter);
		adjustmentThisBg();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==11&&RESULT_OK==resultCode){//兑换成功
			getScore();
			initData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnScoreChange){
			ScoreChangeActivity.invoke(this,exchange_money);
		}else if (v==btnScoreHistory) {
			ScoreHistoryActivity.invoke(this);
		}else if (v==ivHelp) {
			showWindow();
		}else if (v==tvBack) {
			finish();
		}
	}
	
	private void initWebView() {
		
	}
	
	
	private void showWindow(){
		if(dialog==null){
			View view=View.inflate(this, R.layout.dialog_score_describ, null);
			View content=view.findViewById(R.id.layout_content);
//			ScrollView scrollView=(ScrollView)view.findViewById(R.id.webview);
//			scrollView.setOverScrollMode(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
			LayoutParams lParams=content.getLayoutParams();
			lParams.width=ScreenUtil.WIDTH-ScreenUtil.dip2px(this, 20);
			lParams.height=(int)(ScreenUtil.HEIGHT*0.61);
			content.setLayoutParams(lParams);
			view.findViewById(R.id.iv_cancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!isFinishing())DialogUtil.dismissDialog(dialog);
				}
			});
			TextView tvContent=(TextView)view.findViewById(R.id.tv_content);
			tvContent.setText(HApplication.getInstance().scoreHelp==null?"":HApplication.getInstance().scoreHelp.content);
			dialog=DialogUtil.getCenterDialog(this, view);
		}
		DialogUtil.showDialog(dialog);
	}
	
	public static void invoke(Context context){
		Intent intent =new Intent(context,MyScoreActivity.class);
		context.startActivity(intent);
	}
	
	private void getScore() {
		// TODO Auto-generated method stub
		
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MY_SCORE),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<Score> object=GsonParser.getInstance().parseToObj(result, Score.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							exchange_money=object.data.exchange_money;
							score=object.data.pay_points;
							tvScoreTitle.setText(""+score+"分");
						}else {
						}
					}else {
					}
				}else {
				}
			}
		});
	}
	


	public class Score{
		public float exchange_money;
		public int pay_points;
	}
	
	
	private void adjustmentThisBg(){
		int height=(int)(ScreenUtil.WIDTH*0.7427);
		ViewGroup.LayoutParams lp1=layoutWall.getLayoutParams();
		lp1.height=height;
		layoutWall.setLayoutParams(lp1);
		int iv2Size=(int)(ScreenUtil.WIDTH*0.4013);
		ViewGroup.LayoutParams lp2=tvScoreTitle.getLayoutParams();
		lp2.height=iv2Size;
		lp2.width=iv2Size;
		tvScoreTitle.setLayoutParams(lp2);
	}

}
