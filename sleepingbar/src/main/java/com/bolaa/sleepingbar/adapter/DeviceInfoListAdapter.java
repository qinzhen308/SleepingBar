package com.bolaa.sleepingbar.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Watch;

/**
 * 个人中心-我的设备中的适配器
 */
public class DeviceInfoListAdapter extends AbstractListAdapter<Watch> {
    private UnbindListener unbindListener;

	public DeviceInfoListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_device_info, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Watch watch=mList.get(i);
		holder.tvName.setText(watch.name);
		if(watch.is_binding==1){
			holder.tvUnbind.setVisibility(View.VISIBLE);
		}else {
			holder.tvUnbind.setVisibility(View.INVISIBLE);
		}
		holder.tvUnbind.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if(unbindListener!=null){
                    unbindListener.doUnbind(watch);
                }
			}
		});

		return view;
	}


	class ViewHolder{
		public TextView tvName;
		public TextView tvUnbind;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvUnbind=(TextView) view.findViewById(R.id.tv_unbind);
		}
		
	}

    public void setUnbindListener(UnbindListener unbindListener){
        this.unbindListener=unbindListener;
    }

    public interface UnbindListener{
        public void doUnbind(Watch watch);
    }

}
