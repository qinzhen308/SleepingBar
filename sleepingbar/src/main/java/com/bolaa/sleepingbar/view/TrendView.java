package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.model.Sleep;
import com.core.framework.app.devInfo.ScreenUtil;

import java.util.List;

/**
 * Created by pualbeben on 16/6/5.
 */
public class TrendView extends ImageView{

    private List<Point> list;
    private int good;//优
    private int well;//良
    private int bad;//差

    private int contentPadding;


    public TrendView(Context context) {
        super(context);
        init();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        contentPadding=ScreenUtil.dip2px(getContext(),20);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        good=2*contentPadding;
        well=getHeight()/2;
        bad=getHeight()-2*contentPadding;
        drawAxis(canvas);
        drawTrend(canvas);
        drawLabel(canvas);
    }

    private void drawAxis(Canvas canvas){
        Paint paint=new Paint();
        paint.setStrokeWidth(ScreenUtil.dip2px(getContext(),1));
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawLine(contentPadding,bad+contentPadding ,getWidth()-contentPadding,bad+contentPadding,paint);
        paint.setColor(getResources().getColor(R.color.purple2));
        canvas.drawLine(contentPadding,bad,getWidth()-contentPadding,bad,paint);
        canvas.drawLine(contentPadding,well,getWidth()-contentPadding,well,paint);
        canvas.drawLine(contentPadding,good,getWidth()-contentPadding,good,paint);
    }

    private void drawLabel(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_big));
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawLine(contentPadding,bad+contentPadding,getWidth()-contentPadding,bad+contentPadding,paint);
        canvas.drawText("睡眠质量",contentPadding+10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        canvas.drawText("日",getWidth()-contentPadding-10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size));
        paint.setColor(getResources().getColor(R.color.purple2));
        canvas.drawText("优",getWidth()-contentPadding-10,good+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
        canvas.drawText("良",getWidth()-contentPadding-10,well+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
    }


    private void drawTrend(Canvas canvas){
        if(list==null||list.size()==0){
            return;
        }
        int maxX=getWidth()-contentPadding*2;
        int firstLocationX=0;
        int lastLocationX=getWidth()-contentPadding;
        int size=list.size();
        for(int i=0;i<size;i++){
            Point point=new Point();
            point.x=0;
            point.y=0;
            point.isBreak=true;
        }
    }

    public class Point{

        public int x;
        public int y;
        public boolean isBreak;

    }


}
