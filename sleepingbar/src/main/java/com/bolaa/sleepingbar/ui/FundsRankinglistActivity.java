package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragmentActivity;
import com.bolaa.sleepingbar.ui.fragment.FundsRankinglistFragment;

public class FundsRankinglistActivity extends BaseFragmentActivity{
	


	RadioGroup rgTitle;
	FragmentManager mFm;
	Fragment fragments[]=new Fragment[3];
	private int curPosition=0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		initData();
		setListener();
	}
	

	private void initData() {
		// TODO Auto-generated method stub
		fragments[0]= FundsRankinglistFragment.createInstance(FundsRankinglistFragment.PAGE_TYPE_FRIENDS);
		fragments[1]= FundsRankinglistFragment.createInstance(FundsRankinglistFragment.PAGE_TYPE_ALL);
		rgTitle.check(rgTitle.getChildAt(0).getId());
		switchFragment(0);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for(int i=0;i<group.getChildCount();i++){
					if(group.getChildAt(i).getId()==checkedId){
						switchFragment(i);
					}
				}
			}
		});
	}

	//切换fragment
	private void switchFragment(int position){
		FragmentTransaction ft=mFm.beginTransaction();
		//添加fragment到fm里(添进去就不remove)
		if(mFm.findFragmentByTag(""+position)==null){
			ft.add(R.id.content,fragments[position],""+position);
		}
		//控制fragment显示/隐藏
		for(int i=0;i<fragments.length;i++){
			if(position==i){
				ft.show(fragments[i]);
			}else {
				if(fragments[i].isAdded()){
					ft.hide(fragments[i]);
				}
			}
		}
		ft.commit();
		curPosition=position;
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_funds_rankinglist, false, true);
		setTitleText("", "睡眠基金排行榜", 0, true);
		rgTitle=(RadioGroup)findViewById(R.id.rg_hospital_detail);

	}

	private void setExtra() {
		// TODO Auto-generated method stub
		mFm=getSupportFragmentManager();
		Intent intent=getIntent();
	}
	

	public static void invoke(Context context){
		Intent intent=new Intent(context,FundsRankinglistActivity.class);
		context.startActivity(intent);
	}
	
	
	
	

}
