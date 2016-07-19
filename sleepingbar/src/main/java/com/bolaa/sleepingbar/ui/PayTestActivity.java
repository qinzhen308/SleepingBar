package com.bolaa.sleepingbar.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.PayModeAdapter;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Order;
import com.bolaa.sleepingbar.model.PayMode;
import com.bolaa.sleepingbar.model.WXInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.PayUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付选择页面-应付测试的
 */
public class PayTestActivity extends BaseActivity implements PayUtil.PayListener{
	TextView btnPay;
	EditText etBalance;

	ListView lvPayMode;
	PayModeAdapter mAdapter;

	BroadcastReceiver mReceiver;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		loadPageInfo();
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getIntExtra("state", 0) == 1) {
					resultForZhifubao(1, intent.getStringExtra("detail"));
				} else {
					resultForZhifubao(0, intent.getStringExtra("detail"));
				}
			}
		};

		registerReceiver(mReceiver, new IntentFilter("colsePay"));
	}

	@Override
	protected void onDestroy() {
		if(mReceiver!=null){
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}

	private void setExtra(){
		Intent intent=getIntent();
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_pay_test, true, true);
		setTitleText("", "充值", 0, true);
		btnPay=(TextView)findViewById(R.id.btn_pay);
		etBalance=(EditText)findViewById(R.id.et_balance);
		lvPayMode=(ListView)findViewById(R.id.lv_pay_mode);
		mAdapter=new PayModeAdapter(this);
		lvPayMode.setAdapter(mAdapter);

	}

	private void setListener(){
		btnPay.setOnClickListener(this);
		etBalance.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int i = s.toString().indexOf(".");
				if (i >= 0 && (s.length() - 3) > i) {
					etBalance.setText(s.subSequence(0, i + 3));
				}
			}
		});
	}


	//获取页面数据, 只有第一次返回支付列表和优惠券信息
	private void loadPageInfo(){
		List<PayMode> list=new ArrayList<>();
		PayMode mode1=new PayMode();
		mode1.pay_code="wxpay";
		mode1.pay_id="1";
		mode1.pay_name="微信    ";
		PayMode mode2=new PayMode();
		mode2.pay_code="2";
		mode2.pay_id="2";
		mode2.pay_name="支付宝";
		list.add(mode2);
		list.add(mode1);
		mAdapter.setList(list);
		mAdapter.notifyDataSetChanged();
	}

	private void pay(){
		HttpRequester requester=new HttpRequester();
		String balanceStr=etBalance.getText().toString().trim();
		if(!AppUtil.isNull(balanceStr)){
			float balance=Float.valueOf(balanceStr);
			if(Float.compare(balance,0)<=0){
				AppUtil.showToast(this, "请输入充值金额");
				return;
			}
			requester.getParams().put("money",balance+"");
		}else {
			AppUtil.showToast(this, "请输入充值金额");
			return;
		}
		if(AppUtil.isNull(mAdapter.getPayModeId())){
			AppUtil.showToast(this, "请选择支付方式");
			return;
		}
		DialogUtil.showDialog(lodDialog);
		requester.getParams().put("pay_id", mAdapter.getPayModeId());
		NetworkWorker.getInstance().post( AppUrls.getInstance().URL_DO_RECHARGE, new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if (!isFinishing()) {
					DialogUtil.dismissDialog(lodDialog);
				}
				if (status == 200) {
					BaseObject<PayOrderResult> object = GsonParser.getInstance().parseToObj(result, PayOrderResult.class);
					if (object != null) {
						if (object.status == BaseObject.STATUS_OK&&object.data!=null) {
							//提交成功
//							PayResultActivity.invoke(PayTestActivity.this, 1);
							if("wxpay".equals(mAdapter.getPayModeCode())){
								BaseObject<WXInfo> obj = GsonParser.getInstance().parseToObj(result, WXInfo.class);
								PayUtil.wayToWX(PayTestActivity.this,obj.data);
							}else {
								PayUtil.wayToZhifubao(PayTestActivity.this,object.data.price,object.data.out_trade_no,object.data.subject,object.data.notify_url);
							}
						} else {
							AppUtil.showToast(getApplicationContext(), object.info);
						}
					} else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				} else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}

			}
		},requester);
	}

	private void submit(){
		ParamBuilder params=new ParamBuilder();
		String balanceStr=etBalance.getText().toString().trim();
		if(!AppUtil.isNull(balanceStr)){
			float balance=Float.valueOf(balanceStr);
			if(Float.compare(balance,0)<=0){
				AppUtil.showToast(this, "请输入充值金额");
				return;
			}
			params.append("surplus",balance);
		}
		if(AppUtil.isNull(mAdapter.getPayModeId())){
			AppUtil.showToast(this, "请选择支付方式");
			return;
		}

		DialogUtil.showDialog(lodDialog);


		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DO_RECHARGE), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub

				if (status == 200) {
					BaseObject<Order> object = GsonParser.getInstance().parseToObj(result, Order.class);
					if (object != null) {
						if (object.data != null && object.status == BaseObject.STATUS_OK) {
							//提交成功
							if(object.data.pay_statu==0){//调起三方支付
//								pay(object.data.order_id);
								return;
							}else {//直接支付成功(原因：余额充足。。。)
								//成功
								PayResultActivity.invoke(PayTestActivity.this, 1);
								finish();
							}
						} else {
							PayResultActivity.invoke(PayTestActivity.this, 0);
						}
					} else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				} else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}
				if (!isFinishing()) {
					DialogUtil.dismissDialog(lodDialog);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnPay){
			pay();
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Context context){
		Intent intent=new Intent(context,PayTestActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void resultForZhifubao(int state, String detail) {
		if(state==1){
//			PayResultActivity.invoke(PayTestActivity.this, 1);
			AppUtil.showToast(this,"充值成功");
			finish();
		}else {
			AppUtil.showToast(this,"充值失败");
//			PayResultActivity.invoke(PayTestActivity.this, 0);
		}
	}


	public class Settlement{
		public String bonus;
		public String bonus_count;//可使用优惠券数量
		public String bonus_id;//使用的优惠券id
		public String date;
		public String h_id;
		public String ha_o_id;
		public String money_paid;//已支付的金额
		public float order_amount;//待支付金额
		public String order_fee;//体检金额
		public String surplus;//使用的余额
		public float user_money;//用户可用余额
		public List<PayMode> payment_list;

	}


	public class PayOrderResult{
		public String out_trade_no;
		public String pay_online;
		public String price;
		public String notify_url;
		public String subject;
	}

}
