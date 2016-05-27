package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ReListView extends ListView {

	public ReListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ReListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReListView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
