package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.SupporterDetail;
import com.bolaa.sleepingbar.utils.AppUtil;

public class SupporterDetailAdapter extends AbstractListAdapter<SupporterDetail> {

	public SupporterDetailAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_supporter_detail, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final SupporterDetail detail=mList.get(i);
		if("0".equals(detail.stages_num)){
			holder.tvFunds.setText(AppUtil.isNull(detail.type)?"直接赠送":detail.type);
		}else {
			holder.tvStatus.setText(detail.days_num+"/"+detail.stages_num);
		}
		holder.tvFunds.setText(AppUtil.getTwoDecimal(detail.got_money)+"/"+ AppUtil.getTwoDecimal(detail.money)+" 元");
		holder.tvDate.setText(detail.c_time);
		return view;
	}
	
	class ViewHolder{
		public TextView tvDate;
		public TextView tvFunds;
		public TextView tvStatus;
		
		public ViewHolder(View view){
			tvDate=(TextView)view.findViewById(R.id.tv_date);
			tvFunds=(TextView)view.findViewById(R.id.tv_funds);
			tvStatus=(TextView)view.findViewById(R.id.tv_status);
		}
		
	}

}
