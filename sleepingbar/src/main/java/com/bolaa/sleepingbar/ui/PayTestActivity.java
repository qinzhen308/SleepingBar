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
	EditText etBalance;

	ListView lvPayMode;
	PayModeAdapter mAdapter;

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



	private void handleData(boolean isFirst){
		if(isFirst){
			mAdapter.setList(settlement.payment_list);
			mAdapter.notifyDataSetChanged();
		}else {
		}
	}

	//获取页面数据, 只有第一次返回支付列表和优惠券信息
	private void loadPageInfo(final boolean isFirst){
		if(isFirst){
			showLoading();
		}else{
			DialogUtil.showDialog(lodDialog);
		}
		ParamBuilder params=new ParamBuilder();
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
			submit();
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
