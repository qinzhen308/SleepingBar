package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.adapter.CashListAdapter;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.wrapper.BalanceLogWraper;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 我的账号
 * @author pualbeben
 *
 */
public class MyAccountActivity extends BaseListActivity implements OnRefreshListener{
	TextView tvBalance;
	TextView tvBack;
	TextView btnScoreChange;
	TextView btnScoreHistory;
	ImageView ivHelp;
	ImageView layoutWall;
	CashListAdapter mAdapter;
	Dialog dialog;
	
	private float balance;//当前余额
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		initData(false);
		getScore();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void initData(boolean isRefresh){
		ParamBuilder params=new ParamBuilder();
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_LOG), BalanceLogWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_LOG), BalanceLogWraper.class);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==11&&resultCode==RESULT_OK){//兑换之后回来
			initData(false);
			getScore();
		}
	}

	@Deprecated
	private void initData() {
		// TODO Auto-generated method stub
		ParamBuilder params=new ParamBuilder();
		params.append("page_size", 3);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_LOG),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<BalanceLogWraper> object=GsonParser.getInstance().parseToObj(result, BalanceLogWraper.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							mAdapter.setList(object.data.getItems());
							mAdapter.notifyDataSetChanged();
						}else {
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

	private void setListener() {
		// TODO Auto-generated method stub
		mPullListView.setOnRefreshListener(this);
		btnScoreChange.setOnClickListener(this);
		btnScoreHistory.setOnClickListener(this);
		ivHelp.setOnClickListener(this);
		tvBack.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_my_account, false, false);
		btnScoreHistory=(TextView)findViewById(R.id.btn_score_history);
		btnScoreChange=(TextView)findViewById(R.id.btn_score_change);
		tvBalance=(TextView)findViewById(R.id.tv_score_title);
		tvBack=(TextView)findViewById(R.id.title_left);
		ivHelp=(ImageView)findViewById(R.id.title_right);
		layoutWall=(ImageView)findViewById(R.id.layout_wall);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullListView.MODE_PULL_UP_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new CashListAdapter(this);
		mListView.setAdapter(mAdapter);
		adjustmentThisBg();
	}
	
	private void getScore() {
		// TODO Auto-generated method stub
		
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CURRENT_BALANCE),new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					BaseObject<Balance> object=GsonParser.getInstance().parseToObj(result, Balance.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							balance=object.data.user_money;
							tvBalance.setText(""+AppUtil.getTwoDecimal(balance)+"元");
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
		if(v==btnScoreChange){
			CashWithdrawActivity.invoke(this);
		}else if (v==btnScoreHistory) {
			CashHistoryActivity.invoke(this);
		}else if (v==ivHelp) {
			showWindow();
		}else if(v==tvBack){
			finish();
		}
	}
	
	private void showWindow(){
		if(dialog==null){
			View view=View.inflate(this, R.layout.dialog_score_describ, null);
			View content=view.findViewById(R.id.layout_content);
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
			TextView tvTitle=(TextView)view.findViewById(R.id.tv_title);
			tvTitle.setText("提现说明");
			tvContent.setText(HApplication.getInstance().cashHelp==null?"":HApplication.getInstance().cashHelp.content);
			dialog=DialogUtil.getCenterDialog(this, view);
		}
		DialogUtil.showDialog(dialog);
	}
	
	public static void invoke(Context context){
		Intent intent =new Intent(context,MyAccountActivity.class);
		context.startActivity(intent);
	}
	
	public class Balance{
		public float user_money;
	}

	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new BalanceLogWraper();
	}
	

	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		if(AppUtil.isEmpty(allData)){
			showNodata();
		}else {
			showSuccess();
		}
		mAdapter.setList(allData);
		mAdapter.notifyDataSetChanged();
			
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
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(mPullListView.isFooterShown()){
			loadNextPageData();
		}
	}
	
	
	private void adjustmentThisBg(){
		int height=(int)(ScreenUtil.WIDTH*0.7427);
		ViewGroup.LayoutParams lp1=layoutWall.getLayoutParams();
		lp1.height=height;
		layoutWall.setLayoutParams(lp1);
		int iv2Size=(int)(ScreenUtil.WIDTH*0.4013);
		ViewGroup.LayoutParams lp2=tvBalance.getLayoutParams();
		lp2.height=iv2Size;
		lp2.width=iv2Size;
		tvBalance.setLayoutParams(lp2);
	}
	

}
