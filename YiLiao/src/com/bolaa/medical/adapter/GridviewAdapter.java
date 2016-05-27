package com.bolaa.medical.adapter;

import java.util.List;

import com.bolaa.medical.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * gridView适配器
 * 
 * @author jjj
 * 
 * @time 2015-12-16
 */
public class GridviewAdapter extends BaseAdapter {
	private List<String> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	private int select = -1;

	public GridviewAdapter(Context context, List<String> list) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mList = list;
	}

	public void setSelect(int select) {
		this.select = select;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_gridview, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tView.setText(mList.get(position));

		if (position == select) {
			holder.tView.setTextColor(mContext.getResources().getColor(
					R.color.yellow));
			holder.tView
					.setBackgroundResource(R.drawable.bg_rectangle_stroke_yellow);
		} else {
			holder.tView.setTextColor(mContext.getResources().getColor(
					R.color.gray));
			holder.tView
					.setBackgroundResource(R.drawable.bg_rectangle_stroke_grey);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tView;

		public ViewHolder(View view) {
			tView = (TextView) view.findViewById(R.id.item_gridview_tv);
		}
	}

}
