package com.bolaa.sleepingbar.adapter;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.ui.SupporterDetailActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SupporterAdapter extends AbstractListAdapter<Supporter> {

	public SupporterAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_supporter, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Supporter supporter=mList.get(i);
		holder.tvFunds.setText(AppUtil.getTwoDecimal(supporter.got_fund)+"/"+ AppUtil.getTwoDecimal(supporter.sleep_fund)+"元");
		holder.tvName.setText(supporter.nick_name);
		holder.tvRanking.setText(supporter.rank);
		Image13Loader.getInstance().loadImage(supporter.avatar,holder.ivAvatar,R.drawable.user2);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SupporterDetailActivity.invoke(mContext,supporter);
			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvFunds;
		public TextView tvRanking;
		public ImageView ivAvatar;
		
		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvFunds=(TextView)view.findViewById(R.id.tv_funds);
			tvRanking=(TextView)view.findViewById(R.id.tv_ranking);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
		}
		
	}

}
