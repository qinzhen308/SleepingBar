package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.model.tables.RegionTable;
import com.bolaa.sleepingbar.view.RegionSelectLayout;

public class RegionSelectingActivity extends BaseActivity{
	RegionSelectLayout mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_region_select, true, true);
		setTitleText("", "选择地区", 0, true);
		mContent=(RegionSelectLayout)findViewById(R.id.layout_region);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		//中国的parentid=0,  id=1  所以取出的是省
		mContent.setList(RegionTable.getInstance().getListByParentId(1));
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void invokeForResult(Activity context){
		Intent intent =new Intent(context ,RegionSelectingActivity.class);
		context.startActivityForResult(intent, 1);
	}

}
