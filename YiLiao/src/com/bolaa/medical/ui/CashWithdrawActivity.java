package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.WithdrawPageInfo;
import com.bolaa.medical.model.WithdrawPageInfo.Bank;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.EditInputFilter;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
	
	float maxCash;
	
	WithdrawPageInfo info;
	
	private Bank selecedBank;
	
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
		tvBankName.setText(getBankName(info.account_bank));
		etBankNumber.setText(info.account_number);
		etName.setText(info.account_name);
		tvCashMax.setText("当前可提现最大金额："+AppUtil.getTwoDecimal(info.user_money)+"元");
	}
	
	private String getBankName(String id){
		List<Bank> banks=info.bank_list;
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
							AppUtil.showToast(getApplicationContext(), object.msg);
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
		
		ParamBuilder params=new ParamBuilder();
		if(selecedBank!=null){
			params.append("account_bank", selecedBank.id);
		}else if(info!=null&&!AppUtil.isNull(info.account_bank)){
			params.append("account_bank", info.account_bank);
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
		params.append("account_number", card);
		params.append("account_name", name);
		params.append("amount", cash);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_WITHDRAW), new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> object=GsonParser.getInstance().parseToObj(result, Object.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							AppUtil.showToast(getApplicationContext(), object.msg);
							//可以发个广播
							setResult(RESULT_OK);
							finish();
						}else {
							AppUtil.showToast(getApplicationContext(), object.msg);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "兑换失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "兑换失败");
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			Bundle bundle=data.getBundleExtra("data");
			selecedBank=(Bank)bundle.getSerializable("bank");
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
			SelectBankActivity.invoke(this, info.bank_list);
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Activity context){
		Intent intent=new Intent(context,CashWithdrawActivity.class);
		context.startActivityForResult(intent,11);
	}

}
