package com.bolaa.sleepingbar.adapter;


import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Watch;
import com.bolaa.sleepingbar.watch.WatchService;

/**
 * 绑定设备时的列表适配器
 */
public class DeviceBindingListAdapter extends AbstractListAdapter<BluetoothDevice> {
    private UnbindListener unbindListener;

	public DeviceBindingListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_device_info_bind, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final BluetoothDevice watch=mList.get(i);
		holder.tvName.setText(watch.getName());
		if(watch.getBondState()==BluetoothDevice.BOND_BONDED){
			holder.tvStatus.setVisibility(View.VISIBLE);
		}else {
			holder.tvStatus.setVisibility(View.GONE);
		}
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, WatchService.class);
				intent.putExtra(WatchService.FLAG_CURRENT_DEVICE_ADDRESS,watch.getAddress());
				mContext.startService(intent);
			}
		});
		return view;
	}


	class ViewHolder{
		public TextView tvName;
		public TextView tvStatus;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvStatus=(TextView) view.findViewById(R.id.tv_unbind);
		}
		
	}

    public void setUnbindListener(UnbindListener unbindListener){
        this.unbindListener=unbindListener;
    }

    public interface UnbindListener{
        public void doUnbind(Watch watch);
    }

}
