package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.DealLog;

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
		if(dealLog.user_money_s.contains("-")){
			holder.tvDescribe.setText("转出");
			holder.tvDescribe.setTextColor(mContext.getResources().getColor(R.color.base_red));
		}else {
			holder.tvDescribe.setText("转入");
			holder.tvDescribe.setTextColor(mContext.getResources().getColor(R.color.base_green));
		}
		holder.tvDate.setText(dealLog.change_time);
		holder.tvValue.setText(dealLog.user_money_s);
		holder.tvStatus.setText(dealLog.change_desc);
		holder.tvAccountInfo.setText(dealLog.realname+" "+dealLog.account_no);
		return view;
	}
	
	class ViewHolder{
		public TextView tvValue;
		public TextView tvDate;
		public TextView tvStatus;
		public TextView tvDescribe;
		public TextView tvAccountInfo;

		public ViewHolder(View view){
			tvValue=(TextView)view.findViewById(R.id.tv_value);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			tvStatus =(TextView)view.findViewById(R.id.tv_status);
			tvDescribe =(TextView)view.findViewById(R.id.tv_describe);
			tvAccountInfo =(TextView)view.findViewById(R.id.tv_account_info);
		}
		
	}

}
