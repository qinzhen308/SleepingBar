package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.ui.CommonWebActivity;
import com.bolaa.medical.ui.HospitalDetailActivity;
import com.bolaa.medical.ui.HospitalDetailActivityV2;
import com.bolaa.medical.ui.MyPDFActivity;
import com.bolaa.medical.ui.PayFromOrderActivity;
import com.bolaa.medical.ui.ReportDetailActivity;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.FileUtils;
import com.bolaa.medical.utils.HttpDownloader;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AlreadyReservationAdapter extends AbstractListAdapter<Order>{
	DialogStateListener mDialogStateListener;
	private int color[]={R.color.red1,R.color.red1,R.color.red1,R.color.text_grey_french1,R.color.text_grey_french1};
//	private String status[]={"已预约","查看报告","已取消","已过期"};

	public AlreadyReservationAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_already_reservation, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Order order=mList.get(i);
		if(order.operable_list!=null&&order.operable_list.pay){
			holder.tvStatus.setText("去支付");
			holder.tvStatus.setTextColor(mContext.getResources().getColor(color[0]));
		}else {
			holder.tvStatus.setText(order.order_statu_str);
			holder.tvStatus.setTextColor(mContext.getResources().getColor(color[order.order_statu]));
		}
		holder.tvHospital.setText(order.hospital_name);
		String date=order.day_time+" "+order.start_time+"-"+order.end_time;
		holder.tvDate.setText(date);
		holder.tvCode.setText("预约码：" + order.order_sn);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Hospital hospital=new Hospital();
				hospital.h_id=order.h_id;
				hospital.hospital_name=order.hospital_name;
				HospitalDetailActivityV2.invoke(mContext, hospital);
//				HospitalDetailActivity.invoke(mContext, hospital);
				/*if(2==order.order_statu){
					if(1==order.report_type){//pdf
						ReportDetailActivity.invoke(mContext,order);
					}else{
						CommonWebActivity.invoke(mContext, order.report_url);
					}
				}*/
			}
		});
		holder.tvStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(order.operable_list!=null&&order.operable_list.pay){
					PayFromOrderActivity.invoke(mContext,order.order_id);
					return;
				}
				if(2==order.order_statu){
//					CommonWebActivity.invoke(mContext, order.report_url);
					if(1==order.report_type){//pdf
						ReportDetailActivity.invoke(mContext,order);
					}else{
						CommonWebActivity.invoke(mContext, order.report_url);
					}
//					downLoadAndEnterPDF(order.report_url);
				}
			}
		});
		return view;
	}
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==100){
				if(mDialogStateListener!=null){
					mDialogStateListener.onCallback(false);
				}
			}
		};
	};
	
	FileUtils futils;
	public void downLoadAndEnterPDF(final String fileUrl){
		mDialogStateListener.onCallback(false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(futils==null){
					futils = new FileUtils();
				}
				String fileName = getFileNameFromUrl(fileUrl);
				String sdPath = futils.getSDPATH() + "/medical/" + fileName;
				HttpDownloader httpDownLoader = new HttpDownloader();
				int result = httpDownLoader.downfile(fileUrl, "/medical/", fileName);
				if(!AppUtil.isNull(sdPath)&&result==0){//下载成功
					
				}else if(result==1){//已存在
					
				}else {//失败
					
				}
				handler.sendEmptyMessage(100);
				MyPDFActivity.invoke(mContext, sdPath);
			}

		});
		t.start();
	}
	
	public String getFileNameFromUrl(String fileUrl) {
		String fileName = "";
		int index;
		if (fileUrl != null || fileUrl.trim() != "") {
			index = fileUrl.lastIndexOf("/");
			fileName = fileUrl.substring(index + 1, fileUrl.length());
		}
		return fileName;
	}
	
	public void setDialogStateListener(DialogStateListener listener){
		mDialogStateListener=listener;
	}
	
	public interface DialogStateListener{
		public void onCallback(boolean isShow);
	}
	
	class ViewHolder{
		public TextView tvHospital;
		public TextView tvStatus;
		public TextView tvDate;
		public TextView tvCode;
		
		public ViewHolder(View view){
			tvHospital=(TextView)view.findViewById(R.id.tv_hospital);
			tvStatus=(TextView)view.findViewById(R.id.tv_status);
			tvDate=(TextView)view.findViewById(R.id.tv_date);
			tvCode=(TextView)view.findViewById(R.id.tv_medical_code);
		}
		
	}

}
