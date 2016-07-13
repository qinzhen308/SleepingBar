package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.Message;
import com.bolaa.sleepingbar.ui.BBSPostsDetailActivity;
import com.bolaa.sleepingbar.ui.OtherUserHomeActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;

/**
 * 社区首页---专题列表 适配器
 * auther paulz
 */
public class MyMsgAdapter extends AbstractListAdapter<Message> {

	private OnCancelEventListener mOnCancelEventListener;

	public MyMsgAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_my_msg, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final Message item=mList.get(i);
		holder.tvName.setText(item.nick_name);
		holder.tvContent.setText(item.syno);
        holder.tvDate.setText(item.c_time);
        if(item.type==1&& !AppUtil.isNull(item.content)){
            holder.tvComment.setText(item.content);
            holder.tvComment.setVisibility(View.VISIBLE);
        }else {
            holder.tvComment.setVisibility(View.GONE);
        }
		Image13Loader.getInstance().loadImage(item.avatar,holder.ivAvatar,R.drawable.user2);

		holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OtherUserHomeActivity.invoke(mContext,item.user_id,item.nick_name);
			}
		});

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if(item.type==1){
                	BBSPostsDetailActivity.invoke(mContext,item.id);
				}
            }
        });

		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvContent;
		public TextView tvDate;
		public TextView tvComment;
		public ImageView ivAvatar;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvContent =(TextView)view.findViewById(R.id.tv_content);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
            tvComment =(TextView)view.findViewById(R.id.tv_comment);
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
