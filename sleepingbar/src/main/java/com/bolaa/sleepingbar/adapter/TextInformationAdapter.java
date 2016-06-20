package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Information;
import com.bolaa.sleepingbar.model.Watch;
import com.bolaa.sleepingbar.ui.InformationActivity;

/**
 * 社区首页--文字资讯设配器
 */
public class TextInformationAdapter extends AbstractListAdapter<Information> {
    private UnbindListener unbindListener;

	public TextInformationAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_text_information, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Information information=mList.get(i);
		holder.tvTitle.setText(information.title!=null?information.title:"");
		holder.tvTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InformationActivity.invoke(mContext,information.article_id);
			}
		});
		return view;
	}


	class ViewHolder{
		public TextView tvTitle;

		public ViewHolder(View view){
			tvTitle =(TextView)view.findViewById(R.id.tv_title);
		}
		
	}

    public void setUnbindListener(UnbindListener unbindListener){
        this.unbindListener=unbindListener;
    }

    public interface UnbindListener{
        public void doUnbind(Watch watch);
    }

}
