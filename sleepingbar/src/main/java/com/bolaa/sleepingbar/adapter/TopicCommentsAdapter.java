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

	private OnShowMenuListener mOnShowMenuListener;

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
		Image13Loader.getInstance().loadImage(item.avatar,holder.ivAvatar,R.drawable.user2);
		holder.ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnShowMenuListener!=null){
					mOnShowMenuListener.onShow(item);
				}
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

	public void setCaredStatusByUid(String user_id,int has_been_cared){
		int size=getCount();
		for(int i=0;i<size;i++){
			TopicComments comments=mList.get(i);
			if(user_id.equals(comments.user_id)){
				comments.has_been_cared=has_been_cared;
			}
		}
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

	public void setOnShowMenuListener(OnShowMenuListener onShowMenuListener){
		mOnShowMenuListener=onShowMenuListener;
	}

	public interface OnShowMenuListener{
		public void onShow(TopicComments comments);
	}

}
