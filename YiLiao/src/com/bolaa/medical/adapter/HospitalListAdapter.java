package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.ui.HospitalDetailActivity;
import com.bolaa.medical.ui.HospitalDetailActivityV2;
import com.bolaa.medical.utils.Image13Loader;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HospitalListAdapter extends AbstractListAdapter<Hospital>{

	public HospitalListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_hispital, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Hospital hospital=mList.get(i);
		holder.tvName.setText(hospital.hospital_name);
		holder.tvAddress.setText(hospital.address);
		holder.tvDistance.setText(hospital.distance);
		Image13Loader.getInstance().loadImageFade(hospital.banner_img, holder.ivPic);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				HospitalDetailActivity.invoke(mContext,hospital);
				HospitalDetailActivityV2.invoke(mContext,hospital);
			}
		});
		
		return view;
	}
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvAddress;
		public TextView tvDistance;
		public ImageView ivPic;
		
		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvAddress=(TextView)view.findViewById(R.id.tv_address);
			tvDistance=(TextView)view.findViewById(R.id.tv_distance);
			ivPic=(ImageView)view.findViewById(R.id.iv_pic);
		}
		
	}

}
