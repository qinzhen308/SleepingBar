package com.bolaa.medical.view;

import com.bolaa.medical.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridItemLayout extends LinearLayout {
	Context context;

	ImageView ivPic;
	TextView tvTitle;
	TextView tvSubTitle;
	TextView tvPrice;
	CheckBox cbCollect;
	boolean isFromHome;

	public GridItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public GridItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GridItemLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		View view = LayoutInflater.from(context).inflate(
				R.layout.layer_grid_item_one, null);
		ivPic = (ImageView) view.findViewById(R.id.iv_goods_pic);
		tvTitle = (TextView) view.findViewById(R.id.tv_goods_title);
		tvSubTitle = (TextView) view.findViewById(R.id.tv_goods_sub_title);
		tvPrice = (TextView) view.findViewById(R.id.tv_goods_price);
		cbCollect = (CheckBox) view.findViewById(R.id.cb_collection);
		addView(view);
		android.view.ViewGroup.LayoutParams lp = ivPic.getLayoutParams();
		// lp.height=(ScreenUtil.WIDTH-ScreenUtil.dip2px(getContext(),
		// 3+3+1))/2;
		// ivPic.setLayoutParams(lp);
	}

	public void fromHome() {
		cbCollect.setVisibility(GONE);
		tvPrice.setVisibility(GONE);
		isFromHome = true;
	}

	public void initView() {
		

	}

}
