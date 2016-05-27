package com.bolaa.medical.controller;

import com.bolaa.medical.R;
import com.bolaa.medical.view.LoadingView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadStateController {
	private Context mContext;
	private ViewGroup root;
	private View view;
	
	private RelativeLayout loadFailure;
	private RelativeLayout loadNodata;
	private RelativeLayout Loading;
	private LoadingView mLoadingView;
	private TextView tvLoadNoData;
	private ImageView ivLoadNoData;
	private ImageView ivLoadFailure;
	private TextView btnLoadAgain;
	
	public LoadStateController(Context context,ViewGroup root){
		mContext=context;
		this.root=root;
		initView();
	}
	
	private void initView() {
		
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        view = inflater.inflate(R.layout.load_data_stats, null);
        root.addView(view, params);


        this.loadFailure = (RelativeLayout) view.findViewById(R.id.load_failure);
        this.loadNodata = (RelativeLayout) view.findViewById(R.id.load_no_data);
        this.Loading = (RelativeLayout) view.findViewById(R.id.rl_loading_view);
        this.mLoadingView = (LoadingView) view.findViewById(R.id.loading_view);
        this.tvLoadNoData = (TextView) view.findViewById(R.id.tv_load_no_data);
        this.ivLoadNoData = (ImageView) view.findViewById(R.id.iv_load_no_data);
        this.ivLoadFailure = (ImageView) view.findViewById(R.id.iv_load_error);
        this.btnLoadAgain = (TextView) view.findViewById(R.id.tv_load_error_load_again);

    }
	
	public void setTipText(String loading, String nodata){
		if(loading!=null){
			mLoadingView.setTipText(loading);
		}
		
		if(nodata!=null){
			tvLoadNoData.setText(nodata);
		}
		
	}
	
	/**
	 * 设置页面类型，1搜索、0普通、2物业等不同页面文案不同
	 */
	public void setAppPageType(int type){
		switch (type) {
		case 0:
			
			break;

		case 1:
			setTipText(null, "对不起，没有搜到您要找的内容");
			setLoadStatusImg(R.drawable.load_no_data_search, -1);
			break;
			
		case 2:
			setTipText(null, "您还没有缴费记录");
			setLoadStatusImg(R.drawable.load_no_data, -1);
			break;
		}
	}
	
	/**
	 * <0时不改变，==0时设为空
	 * @param resIdNoData
	 * @param resIdError 
	 */
	public void setLoadStatusImg(int resIdNoData, int resIdError){
		if(resIdNoData>=0){
			ivLoadNoData.setImageResource(resIdNoData);
		}
		if(resIdError>=0){
			ivLoadFailure.setImageResource(resIdError);
		}
	}
	
	
	public void setOnLoadErrorListener(final OnLoadErrorListener listener) {
        this.btnLoadAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAgainRefresh();
            }
        });
    }

    public interface OnLoadErrorListener {

        public void onAgainRefresh();

    }
    
    public void showNodata(){
    	loadFailure.setVisibility(View.GONE);
    	loadNodata.setVisibility(View.VISIBLE);
    	Loading.setVisibility(View.GONE);
    	mLoadingView.isShowLoading(false);
    }
    
    public void showFailture(){
    	loadFailure.setVisibility(View.VISIBLE);
    	loadNodata.setVisibility(View.GONE);
    	Loading.setVisibility(View.GONE);
    	mLoadingView.isShowLoading(false);
    }
    
    public void showSuccess(){
    	loadFailure.setVisibility(View.GONE);
    	loadNodata.setVisibility(View.GONE);
    	Loading.setVisibility(View.GONE);
    	mLoadingView.isShowLoading(false);
    }
    
    public void showLoading(){
    	loadFailure.setVisibility(View.GONE);
    	loadNodata.setVisibility(View.GONE);
    	Loading.setVisibility(View.VISIBLE);
    	mLoadingView.isShowLoading(true);
    }
   

}
