package com.bolaa.sleepingbar.adapter;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.ShareChannel;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareGridAdapter extends AbstractListAdapter<ShareChannel> {
	
	public ShareGridAdapter(Activity context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext=context;
	}
	
	public ShareGridAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext=context;
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			holder=new ViewHolder();
			view=View.inflate(mContext, R.layout.item_share_item, null);
			holder.ivPic=(ImageView)view.findViewById(R.id.iv_share_icon);
			holder.tvShareName=(TextView)view.findViewById(R.id.tv_share_name);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final ShareChannel share=mList.get(i);
		holder.tvShareName.setText(share.name);
		holder.ivPic.setImageResource(share.icon);
		return view;
	}
	
	class ViewHolder{
		ImageView ivPic;
		TextView tvShareName;
	}

}
