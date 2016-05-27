package com.bolaa.sleepingbar.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.LoadStateController;

public abstract class BaseFragment extends Fragment{
	protected FrameLayout baseLayout;
	protected LoadStateController mLoadStateController;//加载状态的控制器
	protected boolean hasLoadingState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
	};
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		
		if(!hidden){
			heavyBuz();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	
	
	//业务和视图分步加载，初始化一个轻量级的界面，再在这里处理业务逻辑
	public abstract void heavyBuz();
	
	
	/**
	 * 初始化布局
	 * 
	 * @param paramInt
	 */
	protected void setView(LayoutInflater inflater,int paramInt,boolean hasLoadingState) {
		this.hasLoadingState=hasLoadingState;
		if(baseLayout==null){
			baseLayout=(FrameLayout)inflater.inflate(R.layout.fragment_base, null);
		}else{
			ViewGroup parent=(ViewGroup)baseLayout.getParent();
			if(parent!=null){
				parent.removeView(baseLayout);
			}
		}
		inflater.inflate(paramInt, baseLayout);
		if(hasLoadingState){
			mLoadStateController=new LoadStateController(getActivity(), baseLayout);
		}
		
	}
	
	public void showNodata(){
    	if(hasLoadingState){
    		mLoadStateController.showNodata();
    	}
    }
    
    public void showFailture(){
    	if(hasLoadingState){
    		mLoadStateController.showFailture();
    	}
    }
    
    public void showSuccess(){
    	if(hasLoadingState){
    		mLoadStateController.showSuccess();
    	}
    }
    
    public void showLoading(){
    	if(hasLoadingState){
    		mLoadStateController.showLoading();
    	}
    }

}
