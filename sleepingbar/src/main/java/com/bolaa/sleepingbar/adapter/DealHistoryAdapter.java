package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.DealLog;
import com.bolaa.sleepingbar.utils.AppUtil;

public class DealHistoryAdapter extends AbstractListAdapter<DealLog> {

	public DealHistoryAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_deal_history, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final DealLog dealLog=mList.get(i);
		holder.tvDescribe.setText(dealLog.in_out_str);
		if(dealLog.in_out==1){
			holder.tvDescribe.setTextColor(mContext.getResources().getColor(R.color.base_green));
		}else {
			holder.tvDescribe.setTextColor(mContext.getResources().getColor(R.color.base_red));
		}

		if((dealLog.change_type==1||dealLog.change_type==2)){//充值
			if(!AppUtil.isNull(dealLog.trade_no)){
				holder.layoutBottom.setVisibility(View.VISIBLE);
				holder.tvAccountNumber.setText(dealLog.trade_no);
			}else {
				holder.layoutBottom.setVisibility(View.GONE);
			}
			holder.tvAccountInfo.setText(dealLog.pay_name);
		}else if(dealLog.change_type==6){//提现
            holder.layoutBottom.setVisibility(View.VISIBLE);
            holder.tvAccountNumber.setText(dealLog.account_no);
            holder.tvAccountInfo.setText(dealLog.realname);
        }else {
            holder.layoutBottom.setVisibility(View.GONE);
            holder.tvAccountInfo.setText(dealLog.pay_name);
        }
		holder.tvDate.setText(dealLog.change_time);
		holder.tvValue.setText(dealLog.user_money_s);
		holder.tvStatus.setText(dealLog.change_desc);
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

}
