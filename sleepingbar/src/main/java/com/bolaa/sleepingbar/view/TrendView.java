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

    public final static int TYPE_DAY=0;
    public final static int TYPE_WEEK=1;
    public final static int TYPE_MONTH=2;
    public final static int TYPE_YEAR=3;
    private int type=TYPE_DAY;
    private String[] indicatrixLabels={"浅","一般","深"};
    private String[] typeLabels={"日","周","月","年"};
//    private String x_axisLabels[][] ={{"20:00","21:00","22:00","23:00","00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00",}
    private String x_axisLabels[][] ={{"20:00","","22:00","","00:00","","02:00","","04:00","","06:00","","08:00","","10:00",}
            ,{"周一","周二","周三","周四","周五","周六","周日",}
            ,{"4","8","12","16","20","24","28","30"}
            ,{"1","2","3","4","5","6","7","8","9","10","11","12"}};


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
        trendPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(),2));
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
        drawX_axisLabels(canvas);
    }

    private void drawAxis(Canvas canvas){
        Paint paint=new Paint();
        paint.setStrokeWidth(ScreenUtil.dip2px(getContext(),1));
        paint.setColor(getResources().getColor(R.color.white));
        //横坐标轴
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
//        canvas.drawLine(contentPadding,bad+contentPadding,getWidth()-contentPadding,bad+contentPadding,paint);
        canvas.drawText(type==TYPE_DAY?"睡眠深浅":"睡眠质量",contentPadding+10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        canvas.drawText(typeLabels[type],getWidth()-contentPadding-10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size));
        paint.setColor(getResources().getColor(R.color.purple2));
        if(type==TYPE_DAY){
            canvas.drawText("深",getWidth()-contentPadding-10,good+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
//            canvas.drawText("一般",getWidth()-contentPadding-10,well+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
            canvas.drawText("浅",getWidth()-contentPadding-10,bad-10,paint);
        }else {
            canvas.drawText("优",getWidth()-contentPadding-10,good+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
            canvas.drawText("良",getWidth()-contentPadding-10,well+getResources().getDimensionPixelSize(R.dimen.text_size_big)+10,paint);
        }
    }

    private void drawX_axisLabels(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_mini));
        paint.setColor(getResources().getColor(R.color.purple2));
        Paint pointPaint=new Paint();
        pointPaint.setColor(getResources().getColor(R.color.white));

        String[] labels=x_axisLabels[type];
        float xMax=getWidth()-contentPadding-10;
        float xOri=contentPadding+10;
        float delta=(xMax-xOri)/(labels.length-1);
        float textOffset=-ScreenUtil.dip2px(getContext(),8);
        for(int i=0;i<labels.length;i++){
            canvas.drawText(labels[i],xOri+i*delta+textOffset,bad+contentPadding+getResources().getDimensionPixelSize(R.dimen.text_size_small)+5,paint);
            canvas.drawCircle(xOri+i*delta,bad+contentPadding ,ScreenUtil.dip2px(getContext(),2),pointPaint);
        }
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

    public void setY_Axis(){

    }

    //设置类型， 改变字横线
    public void setType(int type){
        this.type=type;
    }

    public void setX_Axis(){

    }

//    }

    //取值范围0-100
//    public void setData(byte[] src){
//        list=new ArrayList<>();
//        for(int i = 0;i<src.length;i++){
//            Point p=new Point();
//            p.x=contentPadding+trendWidth*i/src.length;
//            p.y=(bad+contentPadding)-src[i]* trendHeight /100f;
//            if(src[i]==0){
//                p.isBreak=true;
//            }else {
//                p.isBreak=false;
//            }
//            list.add(p);
//        }
//        invalidate();

    //取值3个刻度点
    public void setData(byte[] src){
        if(src==null)src=new byte[0];
        list=new ArrayList<>();
        float[] value={bad,bad,well,good};
        float xMax=getWidth()-contentPadding-10;
        float xOri=contentPadding+10;
        float delta=(xMax-xOri)/(src.length-1);
        for(int i = 0;i<src.length;i++){
            Point p=new Point();
            p.x=xOri+i*delta;
            p.y=value[src[i]];
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
