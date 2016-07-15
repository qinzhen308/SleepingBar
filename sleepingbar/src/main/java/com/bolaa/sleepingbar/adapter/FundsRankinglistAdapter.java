package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.RankinglistItem;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;

/**
 * 排行榜 适配器
 * auther paulz
 */
public class FundsRankinglistAdapter extends AbstractListAdapter<RankinglistItem> {

	private int pageType;
	private OnSupportEventListener mOnSupportEventListener;

	public FundsRankinglistAdapter(Context context) {
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
			view=View.inflate(mContext, R.layout.item_funds_rankinglist, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final RankinglistItem item=mList.get(i);
		holder.tvFundsTotal.setText(AppUtil.getTwoDecimal(item.sleep_fund));
		holder.tvName.setText(item.nick_name);
		holder.tvRanking.setText(""+item.rank);
		holder.tvSupportCount.setText(""+item.support_num);
		Image13Loader.getInstance().loadImageFade(item.avatar,holder.ivAvatar);
		holder.checkBox.setImageResource(item.is_praise==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_gray);
		final ImageView tagCheckbox=holder.checkBox;
		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnSupportEventListener!=null){
					mOnSupportEventListener.onPraise(item,tagCheckbox);
				}
			}
		});

		holder.ivSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnSupportEventListener!=null){
					mOnSupportEventListener.onSupport(item);
				}
			}
		});

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvRanking;
		public TextView tvSupportCount;
		public TextView tvFundsTotal;
		public ImageView checkBox;
		public ImageView ivAvatar;
		public ImageView ivSupport;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvRanking =(TextView)view.findViewById(R.id.tv_ranking);
			tvSupportCount =(TextView)view.findViewById(R.id.tv_support_count);
			tvFundsTotal =(TextView)view.findViewById(R.id.tv_funds_total);
			checkBox=(ImageView) view.findViewById(R.id.checkbox);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
			ivSupport=(ImageView) view.findViewById(R.id.iv_support);
		}
	}

	public void setOnSupportEventListener(OnSupportEventListener onSupportEventListener){
		mOnSupportEventListener=onSupportEventListener;
	}

	public interface OnSupportEventListener{
		public void onSupport(RankinglistItem item);

		public void onPraise(RankinglistItem item,ImageView tagView);
	}

}
