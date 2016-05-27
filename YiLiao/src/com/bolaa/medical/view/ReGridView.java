package com.bolaa.medical.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ReGridView extends GridView {

	public ReGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ReGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
