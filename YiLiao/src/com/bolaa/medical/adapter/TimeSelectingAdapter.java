package com.bolaa.medical.adapter;

import com.bolaa.medical.R;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.BookInDay.Book;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeSelectingAdapter extends AbstractListAdapter<Book>{
	OnAppointmentListener mOnAppointmentListener;

	public TimeSelectingAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_time_selecting, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}
		
		final Book book=mList.get(i);
//		String time=book.start_time_name+" "+book.start_time+"-"+(book.start_time_name.equals(book.end_time_name)?"":(book.end_time_name+" "))+book.end_time;
		String time=book.start_time+" - "+book.end_time;
		holder.tvTime.setText(time);
		if(book.can_do==1){
			holder.tvStatus.setText("预约");
			holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.main_red));
			holder.tvStatus.setEnabled(true);
			view.setEnabled(true);
		}else if (book.can_do==2){
			holder.tvStatus.setText("已预约");
			holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_grey_french1));
			holder.tvStatus.setEnabled(false);
			view.setEnabled(false);
		}else if(book.can_do==3){
			holder.tvStatus.setText("预约时间已过");
			holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_grey_french1));
			holder.tvStatus.setEnabled(false);
			view.setEnabled(false);
		}else {
			holder.tvStatus.setText("预约已满");
			holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_grey_french1));
			holder.tvStatus.setEnabled(false);
			view.setEnabled(false);
		}
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mOnAppointmentListener!=null){
					mOnAppointmentListener.onClick(book);
				}
			}
		});
		
		return view;
	}
	
	
	class ViewHolder{
		public TextView tvStatus;
		public TextView tvTime;
		
		public ViewHolder(View view){
			tvTime=(TextView)view.findViewById(R.id.tv_time);
			tvStatus=(TextView)view.findViewById(R.id.tv_status);
		}
		
	}
	
	public void setOnAppointmentListener(OnAppointmentListener onAppointmentListener){
		mOnAppointmentListener=onAppointmentListener;
	}
	
	public interface OnAppointmentListener{
		public void onClick(Book book);
	}

}
