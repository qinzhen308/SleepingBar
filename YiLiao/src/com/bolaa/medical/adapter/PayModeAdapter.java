package com.bolaa.medical.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Coupon;
import com.bolaa.medical.model.PayMode;

import org.w3c.dom.Text;

public class PayModeAdapter extends AbstractListAdapter<PayMode>{

	private PayMode lastPayMode;

	public PayModeAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_pay_mode, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final PayMode payMode=mList.get(i);
		holder.tvName.setText(payMode.pay_name);
		if(payMode.pay_code.equals("wxpay")){
			holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.test_wx,0,R.drawable.selector_checkbox_pay,0);
		}else {
			holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.test_zfb,0,R.drawable.selector_checkbox_pay,0);
		}
		if(i==getCount()-1){
			holder.divider.setVisibility(View.GONE);
		}else {
			holder.divider.setVisibility(View.VISIBLE);
		}
		holder.tvName.setSelected(payMode.isSelected);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lastPayMode==payMode){
					return;
				}
				if(lastPayMode!=null){
					lastPayMode.isSelected=false;
				}
				payMode.isSelected = true;
				lastPayMode=payMode;
				notifyDataSetChanged();
			}
		});

		return view;
	}

	public String getPayModeId(){
		for(int i=0;i<getCount();i++){
			PayMode mode=(PayMode)getItem(i);
			if(mode.isSelected){
				return mode.pay_id;
			}
		}
		return "";
	}
	public String getPayModeCode(){
		for(int i=0;i<getCount();i++){
			PayMode mode=(PayMode)getItem(i);
			if(mode.isSelected){
				return mode.pay_code;
			}
		}
		return "";
	}

	class ViewHolder{
		public TextView tvName;
		public View divider;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_pay);
			divider=view.findViewById(R.id.divider);
		}
		
	}

}
