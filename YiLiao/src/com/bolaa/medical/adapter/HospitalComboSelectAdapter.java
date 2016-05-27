package com.bolaa.medical.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Combo;
import com.bolaa.medical.ui.TimeSelectingActivity;

/**
 * 体检套餐 适配器
 * auther paulz
 */
public class HospitalComboSelectAdapter extends AbstractListAdapter<Combo>{

	public HospitalComboSelectAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_hospital_combo_select, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		
		final Combo combo=mList.get(i);
		holder.tvName.setText(combo.name);
		holder.tvPrice.setText(combo.price+"元");

		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvPrice;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_combo_name);
			tvPrice=(TextView)view.findViewById(R.id.tv_combo_price);
		}
		
	}

}
