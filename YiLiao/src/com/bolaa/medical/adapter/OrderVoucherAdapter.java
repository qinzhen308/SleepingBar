package com.bolaa.medical.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bolaa.medical.model.Voucher;
import com.bolaa.medical.utils.DateUtil;
import com.bolaa.medical.R;

/**
 * 订单里面的抵用券
 * 
 * @author jjj
 * 
 * @time 2016-1-18
 */
public class OrderVoucherAdapter extends BaseAdapter {
	private List<Voucher> mList;
	private LayoutInflater mInflater;
	private OrderVoucherListener mListener;
	private List<String> mList2;
	private int type = 0;// 可用 1不可用
	 

	public OrderVoucherAdapter(Context context, List<Voucher> list) {
		this.mList = list;
		mInflater = LayoutInflater.from(context);
		mListener = (OrderVoucherListener) context;
	}

	public void setmList2(List<String> mList2) {
		this.mList2 = mList2;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Voucher getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.item_voucherlist, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Voucher voucher = getItem(position);
		holder.nameTv.setText(voucher.voucher_title);
		holder.moneyTv.setText(voucher.voucher_price + "");
		holder.ruleTv.setText(voucher.desc);
		holder.timeTv.setText(DateUtil.getTimeUnitSecond("yy/MM/dd hh:mm",
				voucher.voucher_start_date)
				+ "——"
				+ DateUtil.getTimeUnitSecond("yy/MM/dd hh:mm",
						voucher.voucher_end_date));

		if (type == 0) {
			holder.selectBox.setVisibility(View.VISIBLE);
			if (mList2.contains(String.valueOf(voucher.voucher_id))) {
				holder.selectBox.setChecked(true);
			} else {
				holder.selectBox.setChecked(false);
			}
			final CheckBox checkBox = holder.selectBox;
			holder.selectBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mListener != null) {
						mListener.voucherSelect(position, checkBox);
					}

				}
			});
		} else {
			holder.selectBox.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		TextView nameTv;
		TextView moneyTv;
		CheckBox selectBox;
		TextView ruleTv;
		TextView timeTv;

		public ViewHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.item_voucherlist_nameTv);
			moneyTv = (TextView) view
					.findViewById(R.id.item_voucherlist_moneyTv);
			ruleTv = (TextView) view.findViewById(R.id.item_voucherlist_ruleTv);
			timeTv = (TextView) view.findViewById(R.id.item_voucherlist_timeTv);
			selectBox = (CheckBox) view
					.findViewById(R.id.item_voucherlist_selectBox);
			selectBox.setVisibility(View.VISIBLE);
		}
	}

	public interface OrderVoucherListener {
		public void voucherSelect(int position, CheckBox selectBox);
	}
}
