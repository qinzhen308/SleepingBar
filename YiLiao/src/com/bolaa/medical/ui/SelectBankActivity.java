package com.bolaa.medical.ui;

import java.util.ArrayList;
import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.model.WithdrawPageInfo.Bank;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.develop.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectBankActivity extends BaseActivity{
	
	List<Bank> banks;
	
	ListView mListView;
	BankAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		Bundle data=intent.getBundleExtra("data");
		banks=(List)data.getSerializable("list");
		LogUtil.d("list="+banks.size());
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_select_bank, false, true);
		setTitleText("", "选择开户行", 0, true);
		mListView=(ListView)findViewById(R.id.listView);
		mAdapter=new BankAdapter(this);
		mAdapter.setList(banks);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle data=new Bundle();
				data.putSerializable("bank", (Bank)parent.getItemAtPosition(position));
				intent.putExtra("data", data);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}
	
	
	public static void invoke(Activity context,ArrayList<Bank> banks){
		Intent intent=new Intent(context,SelectBankActivity.class);
		Bundle data=new Bundle();
		data.putSerializable("list", banks);
		intent.putExtra("data", data);
		context.startActivityForResult(intent, 11);
	}
	
	public class BankAdapter extends AbstractListAdapter<Bank>{

		public BankAdapter(Activity context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			if(view==null){
				view=View.inflate(mContext, R.layout.item_bank, null);
				holder=new ViewHolder(view);
				view.setTag(holder);
			}else {
				holder=(ViewHolder)view.getTag();
			}
			
			final Bank bank=mList.get(i);
			Image13Loader.getInstance().loadImageFade(bank.logo, holder.ivPic);
			holder.tvName.setText(bank.name);
			
			return view;
		}
		
	}
	
	class ViewHolder{
		public ImageView ivPic;
		public TextView tvName;
		
		public ViewHolder(View view){
			ivPic=(ImageView)view.findViewById(R.id.iv_pic);
			tvName=(TextView)view.findViewById(R.id.tv_name);
		}
		
	}
	

}
