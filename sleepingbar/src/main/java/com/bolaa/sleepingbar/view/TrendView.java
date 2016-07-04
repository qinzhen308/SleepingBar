package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bolaa.sleepingbar.R;
import com.core.framework.app.devInfo.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pualbeben on 16/6/5.
 */
public class TrendView extends ImageView{

    private List<Point> list;
    private float good;//优
    private float well;//良
    private float bad;//差
    private float trendHeight;
    private float trendWidth;

    private int contentPadding;

    private Paint trendPaint;


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
        trendPaint=new Paint();
        trendPaint.setAntiAlias(true);
        trendPaint.setColor(getResources().getColor(R.color.white));
        trendPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(),1));
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        good=2*contentPadding;
        well=getHeight()/2;
        bad=getHeight()-2*contentPadding;
        trendHeight=bad-good+contentPadding;
        trendWidth=getWidth()-2*contentPadding;
        drawAxis(canvas);
        drawTrend(canvas);
        drawLabel(canvas);
    }

    private void drawAxis(Canvas canvas){
        Paint paint=new Paint();
        paint.setStrokeWidth(ScreenUtil.dip2px(getContext(),1));
        paint.setColor(getResources().getColor(R.color.white));
//        canvas.drawLine(contentPadding,bad+contentPadding ,getWidth()-contentPadding,bad+contentPadding,paint);
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
//        canvas.drawLine(contentPadding,bad+contentPadding,getWidth()-contentPadding,bad+contentPadding,paint);
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
        int size=list.size();
        for(int i=1;i<size;i++){
            Point p1=list.get(i-1);
            Point p2=list.get(i);
            if(p1.isBreak) continue;
            canvas.drawLine(p1.x,p1.y,p2.x,p2.y,trendPaint);
        }
    }

    public class Point{
        public float x;
        public float y;
        public boolean isBreak;
    }

    public void setData(byte[] src){
        list=new ArrayList<>();
        for(int i = 0;i<src.length;i++){
            Point p=new Point();
            p.x=contentPadding+trendWidth*i/src.length;
            p.y=(bad+contentPadding)-src[i]* trendHeight /100f;
            if(src[i]==0){
                p.isBreak=true;
            }else {
                p.isBreak=false;
            }
            list.add(p);
        }
        invalidate();
    }


}
