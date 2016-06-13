package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragmentActivity;
import com.bolaa.sleepingbar.ui.fragment.MyFriendsFragment;

/**
 * 我的好友
 */
public class MyFriendsActivity extends BaseFragmentActivity{
	


	RadioGroup rgTitle;
	FragmentManager mFm;
	Fragment fragments[]=new Fragment[2];
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
		fragments[0]= MyFriendsFragment.createInstance(MyFriendsFragment.PAGE_TYPE_I_CARE);
		fragments[1]= MyFriendsFragment.createInstance(MyFriendsFragment.PAGE_TYPE_CARE_ME);
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
		setActiviyContextView(R.layout.activity_my_friends, false, true);
		setTitleTextRightText("", "我的好友", "添加", true);
		rgTitle=(RadioGroup)findViewById(R.id.rg_hospital_detail);

	}

	@Override
	public void onRightClick() {
        AddFriendsActivity.invoke(this);
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		mFm=getSupportFragmentManager();
		Intent intent=getIntent();
	}
	

	public static void invoke(Context context){
		Intent intent=new Intent(context,MyFriendsActivity.class);
		context.startActivity(intent);
	}
	
	
	
	

}
