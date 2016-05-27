package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Coupon;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.ui.HospitalDetailActivity;
import com.core.framework.app.devInfo.ScreenUtil;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CouponListAdapter extends AbstractListAdapter<Coupon>{

	public CouponListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_coupon, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Coupon coupon=mList.get(i);
		holder.tvDate.setText("有效期：" + coupon.use_start_date + " ~ " + coupon.use_end_date);
		holder.tvName.setText(coupon.type_name);
		holder.tvValue.setText("￥"+coupon.type_money);
		holder.tvStatus.setText(coupon.statu_str);
		if(coupon.statu==0){
			holder.tvValue.setBackgroundResource(R.drawable.bg_coupon_valid);
		}else {
			holder.tvValue.setBackgroundResource(R.drawable.bg_coupon_invalid);
		}

		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvStatus;
		public TextView tvDate;
		public TextView tvValue;
		public ViewGroup layoutRight;
		
		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvStatus=(TextView)view.findViewById(R.id.tv_status);
			tvDate=(TextView)view.findViewById(R.id.tv_date);
			tvValue=(TextView)view.findViewById(R.id.tv_value);
			layoutRight=(ViewGroup)view.findViewById(R.id.layout_right);
			int measureHeight=(int)((ScreenUtil.WIDTH-ScreenUtil.dip2px(mContext,20))*0.3275);
			ViewGroup.LayoutParams lp=tvValue.getLayoutParams();
			lp.height=measureHeight;
			tvValue.setLayoutParams(lp);
			lp=layoutRight.getLayoutParams();
			lp.height=measureHeight;
			layoutRight.setLayoutParams(lp);
		}
	}

}
