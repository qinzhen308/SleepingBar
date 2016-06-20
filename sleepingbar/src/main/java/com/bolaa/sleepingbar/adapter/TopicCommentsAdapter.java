package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.model.TopicComments;
import com.bolaa.sleepingbar.ui.OtherUserHomeActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;

/**
 * 社区首页---专题列表 适配器
 * auther paulz
 */
public class TopicCommentsAdapter extends AbstractListAdapter<TopicComments> {

	private OnCancelEventListener mOnCancelEventListener;

	public TopicCommentsAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_topic_comments, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final TopicComments item=mList.get(i);
		holder.tvName.setText(item.nick_name);
		holder.tvContent.setText(item.content);
        holder.tvDate.setText(item.c_time);
		holder.ivAvatar.setImageResource(item.status==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_purple2);
		Image13Loader.getInstance().loadImage(item.avatar,holder.ivAvatar,R.drawable.user2);
		holder.ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OtherUserHomeActivity.invoke(mContext,item.user_id,item.nick_name);
			}
		});


		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvContent;
		public TextView tvDate;
		public ImageView ivAvatar;
		public ImageView ivMenu;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvContent =(TextView)view.findViewById(R.id.tv_content);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
			ivMenu=(ImageView) view.findViewById(R.id.iv_menu);
		}
	}

	public void setOnCancelEventListener(OnCancelEventListener onCancelEventListener){
		mOnCancelEventListener=onCancelEventListener;
	}

	public interface OnCancelEventListener{
		public void onCancel(Friends friends);
	}

}
