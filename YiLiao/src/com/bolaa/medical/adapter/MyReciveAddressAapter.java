package com.bolaa.medical.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bolaa.medical.listener.AddressListener;
import com.bolaa.medical.model.AddressInfo;
import com.bolaa.medical.R;

/**
 * 我的收货地址适配器
 * 
 * @author jjj
 * 
 * @time 2015-12-11
 */
public class MyReciveAddressAapter extends BaseAdapter {
	private List<AddressInfo> mList;
	private LayoutInflater mInflater;
	private AddressListener mListener;
	private int curPosition = -1;
	private boolean isFirst = true;

	public MyReciveAddressAapter(Context context, List<AddressInfo> list) {
		mInflater = LayoutInflater.from(context);
		this.mList = list;
		mListener = (AddressListener) context;
	}

	public void setCurPosition(int curPosition) {
		this.curPosition = curPosition;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public AddressInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.item_myreciveaddress, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AddressInfo info = getItem(position);
		if (info != null) {
			setTex(info.getTrue_name(), holder.nameTv);
			setTex(info.getMob_phone(), holder.phoneTv);
			setTex(info.getArea_info() + info.getAddress(), holder.addressTv);

		}

		if (isFirst&&info.getIs_default().equals("1")) {
//			if () {
				holder.selectBox.setChecked(true);
//			} else {
//				holder.selectBox.setChecked(false);
//			}
		} else if (curPosition == position) {
			
	 
//			if () {
				holder.selectBox.setChecked(true);
//			} else {
//				holder.selectBox.setChecked(false);
//			}
		}else {
				holder.selectBox.setChecked(false);
		}

		holder.wirteTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.addressUpdate(position);

				}
			}
		});
		holder.deteleTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.addressDelete(position);

				}
			}
		});
		final CheckBox box = holder.selectBox;
		holder.selectBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.setDefault(position, box);
				}
			}
		});
		return convertView;
	}

	private void setTex(String string, TextView tView) {
		if (TextUtils.isEmpty(string)) {
			string = "";
		}
		tView.setText(string);
	}

	class ViewHolder {
		TextView nameTv;
		TextView phoneTv;
		TextView addressTv;
		TextView wirteTv;
		TextView deteleTv;
		CheckBox selectBox;

		public ViewHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.item_myRA_nameTv);
			phoneTv = (TextView) view.findViewById(R.id.item_myRA_phoneTv);
			addressTv = (TextView) view.findViewById(R.id.item_myRA_addressTv);
			wirteTv = (TextView) view.findViewById(R.id.item_myRA_writeTv);
			deteleTv = (TextView) view.findViewById(R.id.item_myRA_deleteTv);
			selectBox = (CheckBox) view.findViewById(R.id.item_myRA_defaultBox);
		}

	}

}
