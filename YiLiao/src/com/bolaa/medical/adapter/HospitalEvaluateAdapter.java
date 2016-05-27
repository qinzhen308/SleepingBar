package com.bolaa.medical.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.BalanceLog;
import com.bolaa.medical.model.Evaluate;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.image.Image;

/**
 * 体检套餐 适配器
 * auther paulz
 */
public class HospitalEvaluateAdapter extends AbstractListAdapter<Evaluate>{

	public HospitalEvaluateAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_hospital_evaluate, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Evaluate evaluate=mList.get(i);
		holder.tvDate.setText(evaluate.create_time);
		holder.tvContent.setText(evaluate.content);
		holder.tvName.setText(evaluate.real_name);
		holder.ratingBar.setRating(evaluate.score);
		Image13Loader.getInstance().loadImageFade(evaluate.avatar,holder.ivAvatar);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvDate;
		public TextView tvContent;
		public RatingBar ratingBar;
		public ImageView ivAvatar;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvDate=(TextView)view.findViewById(R.id.tv_date);
			tvContent=(TextView)view.findViewById(R.id.tv_content);
			ratingBar=(RatingBar) view.findViewById(R.id.ratingbar);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
		}
		
	}

}
