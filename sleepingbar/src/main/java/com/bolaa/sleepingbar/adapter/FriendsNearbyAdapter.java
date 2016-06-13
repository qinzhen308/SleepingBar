package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.DealLog;
import com.bolaa.sleepingbar.model.Friends;

import org.w3c.dom.Text;

/**
 * 附近好友
 */
public class FriendsNearbyAdapter extends AbstractListAdapter<Friends> {

	private OnCareEventListener mOnCareEventListener;

	public FriendsNearbyAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_friends_nearby, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Friends friends=mList.get(i);

		holder.tvName.setText(friends.nick_name);
		holder.tvDistance.setText(friends.distance);
		if(friends.is_care==1){
			holder.tvCare.setText("已关注");
			holder.tvCare.setBackgroundResource(R.drawable.bg_rectangle_strake_half_circel_gray);
		}else {
			holder.tvCare.setText("关注");
			holder.tvCare.setBackgroundResource(R.drawable.bg_rectangle_strake_half_circel_purple);
		}
		final TextView careView=holder.tvCare;
		holder.tvCare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnCareEventListener!=null){
					mOnCareEventListener.onCareEvent(friends,careView);
				}
			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvDistance;
		public TextView tvCare;
		public ImageView ivAvatar;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvDistance =(TextView)view.findViewById(R.id.tv_distance);
			tvCare =(TextView)view.findViewById(R.id.tv_care);
			ivAvatar =(ImageView) view.findViewById(R.id.iv_avatar);
		}
		
	}

	public void setOnCareEventListener(OnCareEventListener onCareEventListener) {
		this.mOnCareEventListener = onCareEventListener;
	}

	public interface OnCareEventListener{
		public void onCareEvent(Friends friends,TextView view);
	}

}
