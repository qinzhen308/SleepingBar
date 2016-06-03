package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.ui.AccountActivity;

public class AccountLogAdapter extends AbstractListAdapter<AccountActivity.AccountLog> {

	public AccountLogAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_account_log, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final AccountActivity.AccountLog accountLog=mList.get(i);
		holder.tvDate.setText(accountLog.date);
		holder.tvValue.setText(accountLog.user_money);
		holder.tvReason.setText(accountLog.change_desc);
		return view;
	}
	
	class ViewHolder{
		public TextView tvValue;
		public TextView tvDate;
		public TextView tvReason;

		public ViewHolder(View view){
			tvValue=(TextView)view.findViewById(R.id.tv_value);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			tvReason =(TextView)view.findViewById(R.id.tv_reason);
		}
		
	}

}
