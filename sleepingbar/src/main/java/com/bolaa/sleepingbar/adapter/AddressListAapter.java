package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.listener.AddressListener;
import com.bolaa.sleepingbar.model.AddressInfo;

import java.util.List;

/**
 * 收货地址列表适配器-订单
 * 
 * @author jjj
 * 
 * @time 2015-12-11
 */
public class AddressListAapter extends BaseAdapter {
	private List<AddressInfo> mList;
	private LayoutInflater mInflater;
	private AddressListener listener;
	private String ID = "";

	public AddressListAapter(Context context, List<AddressInfo> list,
			String addressId) {
		mInflater = LayoutInflater.from(context);
		this.mList = list;
		listener = (AddressListener) context;
		this.ID = addressId;
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
			convertView = mInflater.inflate(R.layout.item_address, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == mList.size() - 1) {
			holder.lineView.setVisibility(View.GONE);
		} else {
			holder.lineView.setVisibility(View.VISIBLE);
		}

		AddressInfo info = getItem(position);
		if (ID.equals(info.getAddress_id())) {
			holder.selectIv.setVisibility(View.VISIBLE);
		} else {
			holder.selectIv.setVisibility(View.GONE);
		}
		holder.nameTv.setText(info.getTrue_name());
		holder.addressTv.setText(info.getArea_info() + info.getAddress());
		holder.phoneTv.setText(info.getMob_phone());
		if (info.getIs_default().equals("1")) {
			holder.defaultTv.setVisibility(View.VISIBLE);
		} else {
			holder.defaultTv.setVisibility(View.GONE);
		}

		holder.editIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.addressUpdate(position);
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView selectIv;
		TextView nameTv;
		TextView phoneTv;
		TextView addressTv;
		TextView defaultTv;
		ImageView editIv;
		View lineView;

		public ViewHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.item_address_nameTv);
			phoneTv = (TextView) view.findViewById(R.id.item_address_phoneTv);
			addressTv = (TextView) view
					.findViewById(R.id.item_address_addressTv);
			defaultTv = (TextView) view
					.findViewById(R.id.item_address_defaultTv);
			editIv = (ImageView) view.findViewById(R.id.item_address_editIv);
			selectIv = (ImageView) view
					.findViewById(R.id.item_address_selectIv);
			lineView = view.findViewById(R.id.item_address_line);
		}

	}
}
