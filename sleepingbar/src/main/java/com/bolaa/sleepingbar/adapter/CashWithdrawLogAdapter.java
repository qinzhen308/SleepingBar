package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.CashLog;

public class CashWithdrawLogAdapter extends AbstractListAdapter<CashLog> {

	OnCancelListener mOnCancelListener;

	public CashWithdrawLogAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_cash_withdraw_history, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final CashLog cashLog=mList.get(i);

		holder.tvAccountNumber.setText(cashLog.bank_account);
		holder.tvAccountInfo.setText(cashLog.bank_user_name);
		holder.tvDate.setText(cashLog.create_time_str);
		holder.tvValue.setText(cashLog.money);
		holder.tvStatus.setText(cashLog.apply_status_str);
		if(cashLog.apply_status==1){
			holder.tvCashWithdrawStatus.setVisibility(View.VISIBLE);
		}else {
			holder.tvCashWithdrawStatus.setVisibility(View.GONE);
		}
		holder.tvCashWithdrawStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnCancelListener!=null)mOnCancelListener.onCancel(cashLog);
			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvValue;
		public TextView tvDate;
		public TextView tvStatus;
		public TextView tvDescribe;
		public TextView tvAccountInfo;
		public TextView tvAccountNumber;
		public TextView tvCashWithdrawStatus;
		public View layoutBottom;

		public ViewHolder(View view){
			tvValue=(TextView)view.findViewById(R.id.tv_value);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			tvStatus =(TextView)view.findViewById(R.id.tv_status);
			tvDescribe =(TextView)view.findViewById(R.id.tv_describe);
			tvAccountInfo =(TextView)view.findViewById(R.id.tv_account_info);
			tvAccountNumber =(TextView)view.findViewById(R.id.tv_account_number);
			tvCashWithdrawStatus =(TextView)view.findViewById(R.id.tv_cash_withdraw_status);
			layoutBottom =view.findViewById(R.id.layout_bottom);
		}
	}

	public void setOnCancelListener(OnCancelListener onCancelListener){
		this.mOnCancelListener=onCancelListener;
	}

	public interface OnCancelListener{
		public void onCancel(CashLog cashLog);
	}

}
