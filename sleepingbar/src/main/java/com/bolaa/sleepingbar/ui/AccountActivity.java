package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.AccountLogAdapter;
import com.bolaa.sleepingbar.adapter.SupporterAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.SupporterWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import java.util.List;

/**
 * 亲友团列表页面
 */
public class AccountActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{
	private TextView tvBalance;
	private TextView tvRecharge;
	private TextView tvCashWithdraw;
	private TextView tvDealHistory;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
	}
	
	private void setExtra(){
		
	}



	private void initView(){
		setActiviyContextView(R.layout.activity_account, true, true);
		setTitleText("", "我的账户", 0, true);
		tvBalance =(TextView) findViewById(R.id.tv_balance);
		tvRecharge =(TextView) findViewById(R.id.btn_recharge);
		tvCashWithdraw =(TextView) findViewById(R.id.btn_cash_withdraw);
		tvDealHistory =(TextView) findViewById(R.id.tv_deal_history);
		mPullListView=(PullListView) findViewById(R.id.pull_listview);
		mPullListView.setMode(-1);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new AccountLogAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
//		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		tvRecharge.setOnClickListener(this);
		tvCashWithdraw.setOnClickListener(this);
		tvDealHistory.setOnClickListener(this);
	}


    private void initData(){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ACCOUNT_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing()){
                    DialogUtil.dismissDialog(lodDialog);
                }
                if(status==200){
                    BaseObject<AccountData> obj= GsonParser.getInstance().parseToObj(result,AccountData.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK && obj.data!=null){
                            mAdapter.setList(obj.data.account_log);
                            mAdapter.notifyDataSetChanged();
                            tvBalance.setText(AppUtil.getTwoDecimal(obj.data.user_money));
                        }else {
                            AppUtil.showToast(getApplicationContext(),obj.info);
                        }
                    }else {
                        AppUtil.showToast(getApplicationContext(),"数据解析出错");
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"请检查网络");
                }
            }
        });
    }
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new SupporterWraper();
	}

    private void setCommonInfo(){
        BeanWraper wraper=getBeanWraper();
        if(wraper!=null){
            SupporterWraper data=(SupporterWraper)wraper;
            tvBalance.setText(data.my_funds_count+"");
        }
    }

	@Override
	public void onClick(View v) {
		if(v==tvDealHistory){
            DealHistoryActivity.invoke(this);
		}else if(v==tvCashWithdraw){
            CashWithdrawActivity.invoke(this);
		}else if(v==tvRecharge){
			PayTestActivity.invoke(this);
		}else{
			super.onClick(v);
		}
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
        setCommonInfo();
		mAdapter.setList(allData);
		mAdapter.notifyDataSetChanged();
			
	}

	@Override
	protected void loadError(String message, Throwable throwable, int page) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
		
	}
	

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(!isLoading()){
			initData();
		}
	}

	@Override
	public void onAgainRefresh() {
		// TODO Auto-generated method stub
		initData();
	}
	
	public static void invoke(Context context){
		Intent intent=new Intent(context,AccountActivity.class);
		context.startActivity(intent);
	}

	public class AccountData{
		public float user_money;
		public List<AccountLog> account_log;
	}

	public class AccountLog{
		public String change_desc;
		public String date;
		public String user_money;

	}

}
