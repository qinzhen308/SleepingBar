package com.bolaa.sleepingbar.view.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.GridView;

import com.bolaa.sleepingbar.R;


/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 4/7/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PullGridView extends PullToRefreshAdapterViewBase<GridView> {

    public PullGridView(Context context) {
        super(context);
    }

    public PullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public PullGridView(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected final GridView createRefreshableView(Context context, AttributeSet attrs) {
        GridView gridView = new GridView(context, attrs);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setId(R.id.gridview);

        return gridView;
    }

    /*@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }*/

 

}
