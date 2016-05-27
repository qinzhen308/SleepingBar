package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.ui.CommonWebActivity;
import com.bolaa.medical.ui.MyPDFActivity;
import com.bolaa.medical.ui.ReportDetailActivity;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.FileUtils;
import com.bolaa.medical.utils.HttpDownloader;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.GestureDetector.OnDoubleTapListener;
import android.widget.TextView;

public class MedicalReportAdapter extends AbstractListAdapter<Order>{
	DialogStateListener mDialogStateListener;

	public MedicalReportAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_medical_report, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		final Order order=mList.get(i);
		holder.tvHospital.setText(order.hospital_name);
		holder.tvStatus.setText(order.health_statu_str);
		holder.tvDate.setText(order.report_time);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				CommonWebActivity.invoke(mContext, order.report_url);
//				downLoadAndEnterPDF(order.report_url);
				if(1==order.report_type){//pdf
					ReportDetailActivity.invoke(mContext,order);
				}else{
					CommonWebActivity.invoke(mContext, order.report_url);
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
//				MyPDFActivity.invoke(mContext, sdPath);
				ReportDetailActivity.invoke(mContext, sdPath);
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
		
		public ViewHolder(View view){
			tvHospital=(TextView)view.findViewById(R.id.tv_hospital);
			tvStatus=(TextView)view.findViewById(R.id.tv_status);
			tvDate=(TextView)view.findViewById(R.id.tv_date);
		}
		
	}

}
