package com.bolaa.sleepingbar.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.Watch;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.watch.WatchService;
import com.core.framework.app.oSinfo.SuNetEvn;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;

/**
 * 绑定设备时的列表适配器
 */
public class DeviceBindingListAdapter extends AbstractListAdapter<BluetoothDevice> {
    private UnbindListener unbindListener;

	private Dialog dialog;
	private TextView tipView;

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
		final String name=watch.getName();
		holder.tvName.setText(AppUtil.isNull(name)?watch.getAddress():name);
		if(watch.getBondState()==BluetoothDevice.BOND_BONDED){
			holder.ivStatus.setVisibility(View.VISIBLE);
		}else {
			holder.ivStatus.setVisibility(View.GONE);
		}
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutDialog(AppUtil.isNull(name)?watch.getAddress():name,watch.getAddress());

			}
		});
		return view;
	}

	/**
	 * 显示退出登录
	 */
	private void showLogoutDialog(final String name,final String address) {
		if (dialog == null) {
			View logoutView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bind_watch_tip, null);
			tipView=(TextView)logoutView.findViewById(R.id.tv_tip);
			tipView.setText(name);
			logoutView.findViewById(R.id.dialog_logout_cancelBtn).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!((Activity)mContext).isFinishing())dialog.dismiss();
				}
			});
			logoutView.findViewById(R.id.dialog_logout_okBtn).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!((Activity)mContext).isFinishing())dialog.dismiss();
					if(!SuNetEvn.getInstance().isHasNet()){
						AppUtil.showToast(mContext,"未检测到网络，请检查网络");
						return;
					}
					Intent intent=new Intent(mContext, WatchService.class);
					intent.putExtra(WatchService.FLAG_CURRENT_DEVICE_ADDRESS,address);
					intent.putExtra(WatchService.FLAG_CURRENT_DEVICE_NAME,name);
					mContext.startService(intent);
                }
			});
			dialog = DialogUtil.getCenterDialog((Activity) mContext, logoutView);
			dialog.show();
		} else {
			tipView.setText(name);
			dialog.show();
		}
	}


	class ViewHolder{
		public TextView tvName;
		public ImageView ivStatus;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			ivStatus =(ImageView) view.findViewById(R.id.iv_status);
		}
		
	}

    public void setUnbindListener(UnbindListener unbindListener){
        this.unbindListener=unbindListener;
    }

    public interface UnbindListener{
        public void doUnbind(Watch watch);
    }

}
