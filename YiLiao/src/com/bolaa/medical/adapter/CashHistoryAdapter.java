package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.CashDeal;
import com.bolaa.medical.utils.AppUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CashHistoryAdapter extends AbstractListAdapter<CashDeal>{

	public CashHistoryAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_cash_history, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final CashDeal deal=mList.get(i);
//		holder.tvCash.setText(deal.process_type_str+"金额："+(float)(Math.round(deal.amount*100)/100)+"元");
		holder.tvCash.setText(deal.process_type_str+"金额："+AppUtil.getTwoDecimal(deal.amount)+"元");
		holder.tvDealStatus.setText(deal.is_paid_str);
		holder.tvDealDate.setText(deal.add_time);
		
		return view;
	}
	
	class ViewHolder{
		public TextView tvCash;
		public TextView tvDealStatus;
		public TextView tvDealDate;
		
		public ViewHolder(View view){
			tvCash=(TextView)view.findViewById(R.id.tv_cash_value);
			tvDealStatus=(TextView)view.findViewById(R.id.tv_deal_status);
			tvDealDate=(TextView)view.findViewById(R.id.tv_deal_date);
		}
		
	}

}
