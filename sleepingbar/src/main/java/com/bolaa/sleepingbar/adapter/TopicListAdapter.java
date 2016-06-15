package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;

/**
 * 社区首页---专题列表 适配器
 * auther paulz
 */
public class TopicListAdapter extends AbstractListAdapter<Topic> {

	private OnCancelEventListener mOnCancelEventListener;

	public TopicListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_topic, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final Topic item=mList.get(i);
		holder.tvName.setText(item.nick_name);
		String content=item.content+" 全文";
		SpannableString spannableString1 = new SpannableString(content);
		spannableString1.setSpan(new ClickableSpan(){
			@Override
			public void onClick(View widget) {

			}
		}, content.length()-2, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.main)),content.length()-2, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.tvContent.setText(content);
		holder.tvDate.setText(item.c_time);
		holder.ivAvatar.setImageResource(item.status==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_purple2);
		holder.tvCommtenCount.setText("留言："+item.comment_num);
		holder.tvPraiseCount.setText("赞："+item.praise_num);
		Image13Loader.getInstance().loadImage(item.avatar,holder.ivAvatar,R.drawable.user2);
		holder.pictureAdapter.setList(item.topic_imgs);
		holder.pictureAdapter.notifyDataSetChanged();
		holder.ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvContent;
		public TextView tvDate;
		public TextView tvPraiseCount;
		public TextView tvCommtenCount;
		public ImageView ivAvatar;
		public ImageView ivMenu;
		public ImageView ivPraise;
		public GridView gvPics;
		public PictureAdapter pictureAdapter;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvPraiseCount=(TextView)view.findViewById(R.id.tv_praise_count);
			tvCommtenCount=(TextView)view.findViewById(R.id.tv_comments_count);
			tvContent =(TextView)view.findViewById(R.id.tv_content);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
			ivPraise=(ImageView) view.findViewById(R.id.iv_praise);
			ivMenu=(ImageView) view.findViewById(R.id.iv_menu);
			gvPics=(GridView) view.findViewById(R.id.gv_pics);
			pictureAdapter=new PictureAdapter(mContext);
			pictureAdapter.setWidth(ScreenUtil.WIDTH-ScreenUtil.dip2px(mContext,70),3);
			gvPics.setAdapter(pictureAdapter);
		}
	}

	public void setOnCancelEventListener(OnCancelEventListener onCancelEventListener){
		mOnCancelEventListener=onCancelEventListener;
	}

	public interface OnCancelEventListener{
		public void onCancel(Friends friends);
	}

}
