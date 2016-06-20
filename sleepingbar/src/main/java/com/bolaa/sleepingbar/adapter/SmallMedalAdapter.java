package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Medal;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;


public class SmallMedalAdapter extends AbstractListAdapter<Medal> {

	private int itemHeight;

	public SmallMedalAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setWidth(int width,int column){
		itemHeight=(width-ScreenUtil.dip2px(mContext,5)*(column-1))/column;
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
            view=View.inflate(mContext, R.layout.item_picture, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		Image13Loader.getInstance().loadImageFade(mList.get(i).img,holder.ivPic);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return view;
	}

	class ViewHolder{
		public ImageView ivPic;
		public ViewHolder(View view){
            ivPic=(ImageView) view.findViewById(R.id.iv_pic);
            ViewGroup.LayoutParams lp=ivPic.getLayoutParams();
            lp.height=itemHeight;
            ivPic.setLayoutParams(lp);
		}
		
	}

}
