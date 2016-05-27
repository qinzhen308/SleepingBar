package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.PayModeAdapter;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
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
public class PayFromOrderActivity extends BaseActivity implements PayUtil.PayListener{
	TextView btnPay;
	TextView tvBalance;
	TextView tvRebate;//抵扣券
	TextView tvMoneyShouldPay;//需支付金额
	TextView tvMoneySum;//体检金额

	ListView lvPayMode;
	PayModeAdapter mAdapter;

	String coupon_id;
	String coupon_value;
	String order_id;

	OrderPageInfo pageInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		loadPageInfo();
	}

	private void setExtra(){
		Intent intent=getIntent();
		order_id=intent.getStringExtra("order_id");
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_pay_from_order, true, true);
		setTitleText("", "支付", 0, true);
		btnPay=(TextView)findViewById(R.id.btn_pay);
		tvBalance=(TextView)findViewById(R.id.tv_balance);
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
	}

	private void handleData(){
		mAdapter.setList(pageInfo.payment_list);
		mAdapter.notifyDataSetChanged();
		tvMoneyShouldPay.setText("" + pageInfo.order_amount);
		tvMoneySum.setText(pageInfo.order_fee);
		//抵扣券和余额使用情况
		if(Float.compare(pageInfo.surplus,0)<=0&&Float.compare(pageInfo.bonus,0)<=0){
			findViewById(R.id.layout_deduction).setVisibility(View.GONE);
		}else {
			if(Float.compare(pageInfo.surplus,0)>0){
				tvBalance.setText("使用账户余额：" + pageInfo.surplus + "元");
			}else {
				tvBalance.setVisibility(View.GONE);
				findViewById(R.id.divider).setVisibility(View.GONE);
			}
			if(Float.compare(pageInfo.bonus,0)>0){
				tvRebate.setText("体检抵用券：" + pageInfo.bonus + "元");
			}else {
				tvRebate.setVisibility(View.GONE);
				findViewById(R.id.divider).setVisibility(View.GONE);
			}
		}
	}

	//获取页面数据, 只有第一次返回支付列表和优惠券信息
	private void loadPageInfo(){
		showLoading();
		ParamBuilder params=new ParamBuilder();
		params.append("order_id", order_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_PAY_PAGE_INFO), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				showSuccess();

				if (status == 200) {
					BaseObject<OrderPageInfo> object = GsonParser.getInstance().parseToObj(result, OrderPageInfo.class);
					if (object != null) {
						if (object.data != null && object.status == BaseObject.STATUS_OK) {
							pageInfo = object.data;
							handleData();
						} else {
							showFailture();
						}
					} else {
						showFailture();
					}
				} else {
					showFailture();
				}
			}
		});
	}

	private void pay(){
		if(AppUtil.isNull(mAdapter.getPayModeId())){
			AppUtil.showToast(this,"请选择支付方式");
			return;
		}
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("pay_id", mAdapter.getPayModeId());
		params.append("order_id", order_id);
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
						if ( object.status == BaseObject.STATUS_OK&&object.data!=null) {
							//提交成功
							if("wxpay".equals(mAdapter.getPayModeCode())){
								AppUtil.showToast(getApplicationContext(), "提交成功，等待后续接入支付");
							}else {
								PayUtil.wayToZhifubao(PayFromOrderActivity.this,object.data.order_amount,object.data.order_sn);
							}
						} else {
							AppUtil.showToast(getApplicationContext(), object.msg);
							PayResultActivity.invoke(PayFromOrderActivity.this, 0);

						}
					} else {
						PayResultActivity.invoke(PayFromOrderActivity.this, 0);
					}
				} else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}

			}
		});
	}

	/*private void submit(){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("date", date);
		params.append("h_id", h_id);
		params.append("ha_o_id", ha_o_id);
		if(!AppUtil.isNull(coupon_id)){
			params.append("bonus_id", coupon_id);
		}
		String balanceStr=etBalance.getText().toString().trim();
		if(!AppUtil.isNull(balanceStr)){
			float balance=Float.valueOf(balanceStr);
			if(Float.compare(balance,pageInfo.user_money)>0){
				AppUtil.showToast(this, "您最多能使用" + pageInfo.user_money + "元余额");
				etBalance.setText(""+pageInfo.user_money);
				return;
			}
			params.append("surplus",balance);
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
								pay();
								return;
							}else {//直接支付成功(原因：余额充足。。。)
								finish();
							}
						} else {
							AppUtil.showToast(getApplicationContext(), object.msg);
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
	}*/



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnPay){
			pay();
		}else if(v==tvBalance){
			tvBalance.setSelected(!tvBalance.isSelected());
		}else if(v==tvRebate){

		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Context context,String order_id){
		Intent intent=new Intent(context,PayFromOrderActivity.class);
		intent.putExtra("order_id",order_id);
		context.startActivity(intent);
	}

	@Override
	public void resultForZhifubao(int state, String detail) {
		if(state==1){
			PayResultActivity.invoke(PayFromOrderActivity.this, 1);
			finish();
		}else {
			PayResultActivity.invoke(PayFromOrderActivity.this, 0);
		}
	}


	public class OrderPageInfo{
		public String address;
		public float bonus;//优惠券的价值
		public String bonus_id;//使用的优惠券id
		public String day_time;
		public String h_id;
		public String ha_o_id;
		public String hospital_name;
		public String link_man;//医疗机构联系人
		public String money_paid;//已支付的金额
		public String order_amount;//待支付金额
		public String order_fee;//体检金额
		public String region;//医疗机构 国/省市区的 中文字符串
		public String tel;//医疗机构联系人电话
		public float surplus;//使用的余额
		public List<PayMode> payment_list;

	}

	public class PayOrderResult{
		public String order_amount;
		public String order_fee;
		public String order_sn;
	}

}
