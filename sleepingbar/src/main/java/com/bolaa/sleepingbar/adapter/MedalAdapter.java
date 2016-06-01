package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Medal;
import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.ui.SupporterDetailActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;

/**
 * 勋章
 */
public class MedalAdapter extends AbstractListAdapter<Medal> {

	public MedalAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_medal, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Medal medal=mList.get(i);
		holder.tvName.setText(medal.m_name);
		holder.tvDate.setText(medal.c_time);
		Image13Loader.getInstance().loadImageFade(medal.img,holder.ivAvatar);
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvDate;
		public ImageView ivAvatar;
		
		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvDate=(TextView)view.findViewById(R.id.tv_date);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
			ViewGroup.LayoutParams lp=ivAvatar.getLayoutParams();
			lp.height= (ScreenUtil.WIDTH - ScreenUtil.dip2px(mContext,4*20))/3;
			ivAvatar.setLayoutParams(lp);
		}
		
	}

}
