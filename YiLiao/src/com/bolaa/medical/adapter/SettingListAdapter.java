package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Article;
import com.bolaa.medical.model.CashDeal;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.model.Score;
import com.bolaa.medical.ui.HospitalDetailActivity;
import com.bolaa.medical.ui.SettingDetailActivity;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingListAdapter extends AbstractListAdapter<Article>{

	public SettingListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_setting, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		
		final Article article=mList.get(i);
		holder.tvTitle.setText(article.title);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingDetailActivity.invoke(mContext, article.article_id, article.title);
			}
		});
		
		return view;
	}
	
	class ViewHolder{
		public TextView tvTitle;
		
		public ViewHolder(View view){
			tvTitle=(TextView)view.findViewById(R.id.tv_setting_item);
		}
		
	}

}
