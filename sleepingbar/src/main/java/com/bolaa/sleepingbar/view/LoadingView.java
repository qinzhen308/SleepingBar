package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qz on 15-12-14.
 */
public class LoadingView extends RelativeLayout {
	private Context mContext;
//  private ImageView ivLoadingPic;
  private TextView tvLoadTip;
  private TextView tvLoadTipPoint;
  private boolean isNeedStopAnimation;
  private boolean isDoingAnimation;
  private String tip="";
  private Timer timer=new Timer();
  private String[] points={".","..","...",""};

  public LoadingView(Context context) {
      super(context);
      mContext = context;
      initView();
  }

  public LoadingView(Context context, AttributeSet attrs) {
      super(context, attrs);
      mContext = context;
      initView();
  }

  private void initView() {
      LayoutInflater.from(getContext()).inflate(R.layout.layer_loading_view, this);

//      ivLoadingPic = (ImageView) findViewById(R.id.iv_load_pic);
      tvLoadTip = (TextView) findViewById(R.id.tv_loading_tip);
      tvLoadTipPoint = (TextView) findViewById(R.id.tv_loading_tip_point);
//      tip=tvLoadTip.getEditableText().toString();
  }
  
  public void setTipText(String loadingTip){
  	tvLoadTip.setText(loadingTip);
  }


  public void isShowLoading(boolean isShow){
      if(isShow){
          isNeedStopAnimation = false;
          if (!isDoingAnimation) {
              isDoingAnimation = true;
              doAnimation();
          }
      }else{
          isNeedStopAnimation = true;
          isDoingAnimation = false;
          if(timer!=null){
          	timer.cancel();
          	timer=null;
          }
      }

  }


  private void doAnimation() {
  	timer=new Timer();
  	timer.scheduleAtFixedRate(new TimerTask() {
  		int i=0;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tvLoadTipPoint.setText(points[i++%4]);
					}
				});
			}
		}, 500, 500);
  }

  public boolean isLoading(){
      return isDoingAnimation;
  }
}
