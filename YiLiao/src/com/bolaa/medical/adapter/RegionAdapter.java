package com.bolaa.medical.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bolaa.medical.model.RegionInfo;
import com.bolaa.medical.R;

/**
 * 地区适配器
 * 
 * @author jjj
 * 
 * @time 2015-12-24
 */
public class RegionAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<RegionInfo> mList;
	private int type = 0;// 0-地址列表 1-popwindow

	public RegionAdapter(Context context, List<RegionInfo> list, int type) {
		this.mList = list;
		mInflater = LayoutInflater.from(context);
		this.type = type;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public RegionInfo getItem(int position) {
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
			if (type == 0) {
				convertView = mInflater.inflate(R.layout.item_region, null);
			} else {
				convertView = mInflater.inflate(R.layout.item_region_pwindow,
						null);
			}
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nameTv.setText(getItem(position).region_name);
		return convertView;
	}

	class ViewHolder {
		TextView nameTv;

		public ViewHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.item_region_tv);
		}

	}
}
