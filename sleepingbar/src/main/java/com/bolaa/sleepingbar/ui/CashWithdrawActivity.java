package com.bolaa.sleepingbar.ui;

import java.util.Calendar;
import java.util.List;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.WithdrawPageInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.wheel.ArrayWheelAdapter;
import com.bolaa.sleepingbar.view.wheel.OnWheelScrollListener;
import com.bolaa.sleepingbar.view.wheel.WheelView;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CashWithdrawActivity extends BaseActivity{
	EditText etCash;
	EditText etName;
	EditText etBankNumber;
	TextView tvBankName;
	TextView btnSubmit;
	TextView tvCashMax;
    WheelView bankWheel;
	
	float maxCash;
	
	WithdrawPageInfo info;
	
	private WithdrawPageInfo.Bank selecedBank;

    Dialog bankListDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activtiy_cash_withdraw, false, true);
		setTitleText("", "提现", 0, true);
		etCash=(EditText)findViewById(R.id.et_cash);
		etName=(EditText)findViewById(R.id.et_name);
		etBankNumber=(EditText)findViewById(R.id.et_bank_number);
		tvBankName=(TextView)findViewById(R.id.tv_bank_name);
		tvCashMax=(TextView)findViewById(R.id.tv_max_cash);
		btnSubmit=(TextView)findViewById(R.id.btn_submit);
        tvBankName.setEnabled(false);
    }

	private void setListener() {
		// TODO Auto-generated method stub
		btnSubmit.setOnClickListener(this);
		tvBankName.setOnClickListener(this);
		etCash.addTextChangedListener(new TextWatcher() {
			
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
				int i=s.toString().indexOf(".");
				if(i>=0&&(s.length()-3)>i){
					etCash.setText(s.subSequence(0, i+3));
				}
			}
		});
	}
	
	private void handleData(){
		tvCashMax.setText(AppUtil.getTwoDecimal(info.user_money)+"元");
        bankListDialog = DialogUtil.getMenuDialog2(this, getBankListView(), ScreenUtil.getScreenWH(this)[1] / 2);
        bankListDialog.setCanceledOnTouchOutside(true);
        tvBankName.setEnabled(true);
	}
	
	private String getBankName(String id){
		List<WithdrawPageInfo.Bank> banks=info.bank_array;
		if(AppUtil.isEmpty(banks))return "";
		int size=banks.size();
		for(int i=0;i<size;i++){
			if(banks.get(i).id.equals(id))return banks.get(i).name;
		}
		return "";
	}
	
	private void initData(){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_WITHDRAW_PAGE_INFO), new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<WithdrawPageInfo> object=GsonParser.getInstance().parseToObj(result, WithdrawPageInfo.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							info=object.data;
							maxCash=info.user_money;
							handleData();
						}else {
							AppUtil.showToast(getApplicationContext(), object.info);
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
	
	private void submit(){
		String cash=etCash.getText().toString().trim();
		if(!AppUtil.isNull(cash)){
			float cashF=Float.valueOf(cash);
			if(cashF<=0){
				AppUtil.showToast(getApplicationContext(), "您输入的金额有误");
				return;
			}else if(cashF>maxCash){
				AppUtil.showToast(getApplicationContext(), "提现金额不能超过您的余额");
				return;
			}
		}else {
			AppUtil.showToast(getApplicationContext(), "请输入您要提现的金额");
			return;
		}
		
		HttpRequester requester=new HttpRequester();
		if(selecedBank!=null){
            requester.getParams().put("bank_id", selecedBank.id);
		}else {
			AppUtil.showToast(this, "请选择开户行");
			return;
		}
		String card=etBankNumber.getText().toString().trim();
		String name=etName.getText().toString().trim();
		if(AppUtil.isNull(card)){
			AppUtil.showToast(this, "请输入需要转入的账户");
			return ;
		}
		if(AppUtil.isNull(name)){
			AppUtil.showToast(this, "请输入户主名字");
			return ;
		}
		DialogUtil.showDialog(lodDialog);
        requester.getParams().put("bank_account", card);
        requester.getParams().put("bank_user_name", name);
        requester.getParams().put("money", cash);
		NetworkWorker.getInstance().get(AppUrls.getInstance().URL_CASH_WITHDRAW, new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> object= GsonParser.getInstance().parseToObj(result, Object.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
//							AppUtil.showToast(getApplicationContext(), object.info);
							CashWithdrawResultActivity.invoke(CashWithdrawActivity.this,1);
							//可以发个广播
							setResult(RESULT_OK);
							finish();
						}else {
							AppUtil.showToast(getApplicationContext(), object.info);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "提现失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "提现失败");
				}
			}
		},requester);
	}

	/**
	 * 银行选择
	 *
	 * @return
	 */
	private View getBankListView() {

		View view = LayoutInflater.from(this).inflate(R.layout.dialog_bank_wheel, null);
		view.findViewById(R.id.wheel_okTv).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                selecedBank=info.bank_array.get(bankWheel.getCurrentItem());
                tvBankName.setText(selecedBank.name);
				if (bankListDialog != null && bankListDialog.isShowing()) {
                    bankListDialog.dismiss();
				}
			}
		});
		bankWheel = (WheelView) view.findViewById(R.id.wheel_bank);

        ArrayWheelAdapter<WithdrawPageInfo.Bank> adapter = new ArrayWheelAdapter(this,info.bank_array.toArray());
        bankWheel.setViewAdapter(adapter);
        bankWheel.setCyclic(false);// 是否可循环滑动
        bankWheel.addScrollingListener(scrollListener);


		return view;
	}

    /*
	 * dataPick滑动 scrollListener
	 */
    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
        }

    };
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			Bundle bundle=data.getBundleExtra("data");
			selecedBank=(WithdrawPageInfo.Bank) bundle.getSerializable("bank");
			tvBankName.setText(selecedBank.name);
			etBankNumber.setText("");
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnSubmit){
			submit();
		}else if (v==tvBankName) {
            DialogUtil.showDialog(bankListDialog);
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Activity context){
		Intent intent=new Intent(context,CashWithdrawActivity.class);
		context.startActivityForResult(intent,11);
	}

}
