package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
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
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Order;
import com.bolaa.sleepingbar.model.PayMode;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.PayUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import java.util.List;

/**
 * 支付选择页面-应付测试的
 */
public class PayTestActivity extends BaseActivity implements PayUtil.PayListener{
	TextView btnPay;
	TextView tvBalance;
	TextView tvRebate;//抵扣券
	TextView tvMoneyShouldPay;//需支付金额
	TextView tvMoneySum;//体检金额
	EditText etBalance;

	ListView lvPayMode;
	PayModeAdapter mAdapter;

	String date;
	String h_id;
	String ha_o_id;
	String coupon_id;
	String coupon_value;
	String surplus;
	String combo_id;//套餐id

	Settlement settlement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		loadPageInfo(true);
	}

	private void setExtra(){
		Intent intent=getIntent();
		date=intent.getStringExtra("date");
		h_id=intent.getStringExtra("h_id");
		ha_o_id=intent.getStringExtra("ha_o_id");
		combo_id=intent.getStringExtra("combo_id");
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_pay_test, true, true);
		setTitleText("", "支付", 0, true);
		btnPay=(TextView)findViewById(R.id.btn_pay);
		tvBalance=(TextView)findViewById(R.id.tv_balance);
		etBalance=(EditText)findViewById(R.id.et_balance);
		tvRebate=(TextView)findViewById(R.id.tv_rebate);
		tvMoneyShouldPay=(TextView)findViewById(R.id.tv_money_should_pay);
		tvMoneySum=(TextView)findViewById(R.id.tv_money_sum);
		lvPayMode=(ListView)findViewById(R.id.lv_pay_mode);
		mAdapter=new PayModeAdapter(this);
		lvPayMode.setAdapter(mAdapter);

	}
	
	private void setListener(){
		btnPay.setOnClickListener(this);
//		tvBalance.setOnClickListener(this);
		tvRebate.setOnClickListener(this);
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
				setShouldPay();
			}
		});
	}

	private void setShouldPay(){
		String balaceStr=etBalance.getText().toString();
		float balance=0;
		if(balaceStr.length()>0){
			balance=Float.valueOf(balaceStr);
		}
		if(Float.compare(balance, settlement.order_amount)>0){
			tvMoneyShouldPay.setText("0.00");
			etBalance.setText("" +AppUtil.getTwoDecimal(settlement.order_amount) );
		}else {
			tvMoneyShouldPay.setText("" + AppUtil.getTwoDecimal(settlement.order_amount - balance));
		}
	}

	private void handleData(boolean isFirst){
		if(isFirst){
			mAdapter.setList(settlement.payment_list);
			mAdapter.notifyDataSetChanged();
			tvRebate.setText("体检抵用券：您有"+settlement.bonus_count+"张抵用券可用");
		}else {
			tvRebate.setText("体检抵用券："+coupon_value+"元");
		}
		tvMoneyShouldPay.setText(""+settlement.order_amount);
		tvMoneySum.setText(settlement.order_fee);
		etBalance.setHint("您的余额：" + settlement.user_money);
		setShouldPay();
	}

	//获取页面数据, 只有第一次返回支付列表和优惠券信息
	private void loadPageInfo(final boolean isFirst){
		if(isFirst){
			showLoading();
		}else{
			DialogUtil.showDialog(lodDialog);
		}
		ParamBuilder params=new ParamBuilder();
		params.append("date", date);
		params.append("h_id", h_id);
		params.append("package_id", combo_id);
		params.append("ha_o_id", ha_o_id);
		if(isFirst){
			params.append("is_first", 1);
		}else{
			params.append("is_first", 0);
			params.append("bonus_id", coupon_id);
			params.append("surplus", surplus);
		}
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_PAY_PAGE_INFO), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if (isFirst) {
					showSuccess();
				} else {
					if (!isFinishing()) {
						DialogUtil.dismissDialog(lodDialog);
					}
				}
				if (status == 200) {
					BaseObject<Settlement> object = GsonParser.getInstance().parseToObj(result, Settlement.class);
					if (object != null) {
						if (object.data != null && object.status == BaseObject.STATUS_OK) {
							//提交成功  跳转到支付页面
//							WayPayActivity.invoke(TimeSelectingActivity.this, object.data);
							settlement = object.data;
							handleData(isFirst);

//							AppUtil.showToast(getApplicationContext(), "暂未接入支付");
						} else {
							if (isFirst) {
								showNodata();
							} else {
							}
							AppUtil.showToast(getApplicationContext(), object.info);
						}
					} else {
						if (isFirst) {
							showNodata();
						} else {
							AppUtil.showToast(getApplicationContext(), "请检查网络");
						}
					}
				} else {
					if (isFirst) {
						showNodata();
					} else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				}
			}
		});
	}

	private void pay(String order_id){
		ParamBuilder params=new ParamBuilder();
		params.append("pay_id", mAdapter.getPayModeId());
		params.append("order_id", order_id);
		params.append("package_id", combo_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DO_PAY), new NetworkWorker.ICallback() {

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
								AppUtil.showToast(getApplicationContext(), "提交成功，等待后续接入支付");
							}else {
								PayUtil.wayToZhifubao(PayTestActivity.this,object.data.order_amount,object.data.order_sn);
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
		});
	}

	private void submit(){
		ParamBuilder params=new ParamBuilder();
		String balanceStr=etBalance.getText().toString().trim();
		if(!AppUtil.isNull(balanceStr)){
			float balance=Float.valueOf(balanceStr);
			if(Float.compare(balance,settlement.user_money)>0){
				AppUtil.showToast(this, "您最多能使用" + settlement.user_money + "元余额");
				etBalance.setText(""+settlement.user_money);
				return;
			}
			params.append("surplus",balance);
		}
		if(Float.compare(Float.valueOf(tvMoneyShouldPay.getText().toString()),0)>0&&AppUtil.isNull(mAdapter.getPayModeId())){
			AppUtil.showToast(this, "请选择支付方式");
			return;
		}

		DialogUtil.showDialog(lodDialog);
		params.append("date", date);
		params.append("h_id", h_id);
		params.append("ha_o_id", ha_o_id);
		params.append("package_id", combo_id);
		if(!AppUtil.isNull(coupon_id)){
			params.append("bonus_id", coupon_id);
		}

		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_MAKE_AN_APPOINTMENT), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub

				if (status == 200) {
					BaseObject<Order> object = GsonParser.getInstance().parseToObj(result, Order.class);
					if (object != null) {
						if (object.data != null && object.status == BaseObject.STATUS_OK) {
							//提交成功
							if(object.data.pay_statu==0){//调起三方支付
								pay(object.data.order_id);
								return;
							}else {//直接支付成功(原因：余额充足。。。)
								//成功
								PayResultActivity.invoke(PayTestActivity.this, 1);
								finish();
							}
						} else {
							//应付金额大于0，需要进行后续的三方支付，否则就已经完成了支付过程，所以即便是支付失败，这里也应该进入支付结果页(失败状态)
							if(Float.compare(Float.valueOf(tvMoneyShouldPay.getText().toString()),0)>0){
								AppUtil.showToast(getApplicationContext(), object.info);
							}else {
								//失败
								PayResultActivity.invoke(PayTestActivity.this, 0);
							}
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
		if(requestCode==11&&resultCode==RESULT_OK){//选择券
			coupon_id=data.getStringExtra("coupon_id");
			coupon_value=data.getStringExtra("coupon_value");
			loadPageInfo(false);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnPay){
			submit();
		}else if(v==tvBalance){
			tvBalance.setSelected(!tvBalance.isSelected());
		}else if(v==tvRebate){
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Context context,String date,String h_id,String ha_o_id,String combo_id){
		Intent intent=new Intent(context,PayTestActivity.class);
		intent.putExtra("date",date);
		intent.putExtra("h_id",h_id);
		intent.putExtra("ha_o_id",ha_o_id);
		intent.putExtra("combo_id",combo_id);
		context.startActivity(intent);
	}

	@Override
	public void resultForZhifubao(int state, String detail) {
		if(state==1){
			PayResultActivity.invoke(PayTestActivity.this, 1);
			finish();
		}else {
			PayResultActivity.invoke(PayTestActivity.this, 0);
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
		public String order_amount;
		public String order_fee;
		public String order_sn;
	}

}
