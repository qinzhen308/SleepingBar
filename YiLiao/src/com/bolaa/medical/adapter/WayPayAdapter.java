package com.bolaa.medical.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.medical.model.WayInfo;
import com.bolaa.medical.R;

/**
 * 购物卡订单列表适配器
 * 
 * @author jjj
 * 
 * @time 2015-12-14
 */
public class WayPayAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<WayInfo> mList;

	public WayPayAdapter(Context con, List<WayInfo> list) {
		mInflater = LayoutInflater.from(con);
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public WayInfo getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.item_waypay, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		WayInfo info = getItem(position);
		if (info != null) {
			String name = info.getPayment_name();
			sett(name, holder.nameTv);
			sett(name + "安全支付", holder.descTv);
			if (name.contains("支付宝")) {
				holder.iv.setImageResource(R.drawable.zhifubao);
			} else if (name.contains("微信")) {
				holder.iv.setImageResource(R.drawable.wechat);
			} else if (name.contains("招商")) {
				holder.iv.setImageResource(R.drawable.zs_bank);
			} else if (name.contains("银联")) {
				holder.iv.setImageResource(R.drawable.union_pay);
			} else {
				holder.iv.setImageResource(R.drawable.union_pay);
			}
		}
		return convertView;
	}

	private void sett(String string, TextView tView) {

		if (TextUtils.isEmpty(string)) {
			string = "";
		}
		tView.setText(string);
	}

	class ViewHolder {
		TextView nameTv;
		TextView descTv;
		ImageView iv;

		public ViewHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.item_waypay_nameTv);
			descTv = (TextView) view.findViewById(R.id.item_waypay_descTv);
			iv = (ImageView) view.findViewById(R.id.item_waypay_iv);
		}
	}

}
