package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.RankinglistItem;
import com.bolaa.sleepingbar.ui.OtherUserHomeActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;

/**
 * 我的好友 适配器
 * auther paulz
 */
public class FriendsListAdapter extends AbstractListAdapter<Friends> {

	private int pageType;
	private OnCancelEventListener mOnCancelEventListener;

	public FriendsListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setPageType(int type){
		pageType=type;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_friends, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final Friends item=mList.get(i);
		holder.tvName.setText(item.nick_name);
		holder.tvType.setText(item.f_type);
		Image13Loader.getInstance().loadImageFade(item.avatar,holder.ivAvatar);
//		if(item.is_care==1){
//			holder.tvCancelCare.setText("取消\n关注");
//		}else {
//			holder.tvCancelCare.setText("关注");
//		}

		holder.tvCancelCare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnCancelEventListener!=null){
					mOnCancelEventListener.onCancel(item);
				}
			}
		});

		holder.tvName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OtherUserHomeActivity.invoke(mContext,item.uid,item.nick_name);
			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvType;
		public TextView tvCancelCare;
		public ImageView ivAvatar;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvType =(TextView)view.findViewById(R.id.tv_f_type);
			tvCancelCare =(TextView)view.findViewById(R.id.tv_cancel_care);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
		}
	}

	public void setOnCancelEventListener(OnCancelEventListener onCancelEventListener){
		mOnCancelEventListener=onCancelEventListener;
	}

	public interface OnCancelEventListener{
		public void onCancel(Friends friends);
	}

}
