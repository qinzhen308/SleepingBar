package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bolaa.sleepingbar.R;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pualbeben on 16/6/5.
 */
public class TrendViewV2 extends ImageView{

    private List<Point> list;
    private float good;//优
    private float well;//良
    private float bad;//差
    private float xAxis;//坐标轴
    private float trendHeight;
    private float trendWidth;
    private int width;
    private int height;

    private int contentPadding;

    private Paint trendPaint;
    private Paint pPaint;

    public final static int TYPE_DAY=0;
    public final static int TYPE_WEEK=1;
    public final static int TYPE_MONTH=2;
    public final static int TYPE_YEAR=3;
    private int type=TYPE_DAY;
    private String[] indicatrixLabels={"浅","一般","深"};
    private String[] typeLabels={"日","周","月","年"};
//    private String x_axisLabels[][] ={{"20:00","21:00","22:00","23:00","00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00",}
    private String x_axisLabels[][] ={{"21:00","","23:00","","01:00","","03:00","","05:00","","07:00","08:00"}
            ,{"周一","周二","周三","周四","周五","周六","周日",}
            ,{"1-4","5-8","9-12","13-16","17-20","21-24","25-28","29-30"}
            ,{"1","2","3","4","5","6","7","8","9","10","11","12"}};


    public TrendViewV2(Context context) {
        super(context);
        init();
    }

    public TrendViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrendViewV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        contentPadding=ScreenUtil.dip2px(getContext(),20);
        trendPaint=new Paint();
        trendPaint.setAntiAlias(true);
        trendPaint.setColor(getResources().getColor(R.color.white));
        trendPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(),2));
        pPaint=new Paint();
        pPaint.setAntiAlias(true);
        pPaint.setColor(getResources().getColor(R.color.white));
        pPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(),2));
        if(type==TYPE_MONTH){
            String[] labels=x_axisLabels[TYPE_MONTH];
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            calendar.setTimeInMillis(System.currentTimeMillis());
            int day=calendar.getActualMaximum(Calendar.DATE);
            LogUtil.d("trendview---max day="+day);
            if(day==28){
                labels[labels.length-1]="";
            }else if(day==29){
                labels[labels.length-1]="29";
            }else {
                labels[labels.length-1]="29-"+day;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureDimens();
    }

    private void measureDimens(){
        width=getWidth();
        good=2*contentPadding;
        trendHeight=getHeight()-3*contentPadding;
        float deltaLine=trendHeight/3;
        well=good+deltaLine;
        bad=good+2*deltaLine;
        xAxis=getHeight()-contentPadding;
        trendWidth=getWidth()-2*contentPadding;
        height=getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getHeight()==0){
            measureDimens();
        }
        drawAxis(canvas);
        drawTrend(canvas);
        drawLabel(canvas);
        drawX_axisLabels(canvas);
    }

    private void drawAxis(Canvas canvas){
        Paint paint=new Paint();
        paint.setStrokeWidth(ScreenUtil.dip2px(getContext(),1));
//        paint.setColor(getResources().getColor(R.color.white));
        paint.setColor(getResources().getColor(R.color.purple2));
        //横坐标轴
        canvas.drawLine(contentPadding,xAxis ,width-contentPadding,xAxis,paint);
        canvas.drawLine(contentPadding,bad,width-contentPadding,bad,paint);
        canvas.drawLine(contentPadding,well,width-contentPadding,well,paint);
        canvas.drawLine(contentPadding,good,width-contentPadding,good,paint);
    }

    private void drawLabel(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_big));
        paint.setColor(getResources().getColor(R.color.white));
//        canvas.drawLine(contentPadding,bad+contentPadding,getWidth()-contentPadding,bad+contentPadding,paint);
        canvas.drawText(type==TYPE_DAY?"睡眠深浅":"睡眠质量",contentPadding+10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        canvas.drawText(typeLabels[type],width-contentPadding-10,good-getResources().getDimensionPixelSize(R.dimen.text_size_big)-10,paint);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size));
        paint.setColor(getResources().getColor(R.color.purple2));
        if(type==TYPE_DAY){
            canvas.drawText("深",width-contentPadding-10,good+getResources().getDimensionPixelSize(R.dimen.text_size_big)+2,paint);
            canvas.drawText("浅",width-contentPadding-10,bad+getResources().getDimensionPixelSize(R.dimen.text_size_big)+2,paint);
            paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_small));
            canvas.drawText("一般",width-contentPadding-15,well+getResources().getDimensionPixelSize(R.dimen.text_size_small)+2,paint);
        }else {
            canvas.drawText("优",width-contentPadding-10,good+getResources().getDimensionPixelSize(R.dimen.text_size_big)+2,paint);
            canvas.drawText("良",width-contentPadding-10,well+getResources().getDimensionPixelSize(R.dimen.text_size_big)+2,paint);
            canvas.drawText("差",width-contentPadding-10,bad+getResources().getDimensionPixelSize(R.dimen.text_size_big)+2,paint);
        }
    }

    private void drawX_axisLabels(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_mini));
        paint.setColor(getResources().getColor(R.color.purple2));
        Paint pointPaint=new Paint();
        pointPaint.setColor(getResources().getColor(R.color.purple2));

        String[] labels=x_axisLabels[type];
        float xMax=width-contentPadding-10;
        float xOri=contentPadding+10;
        float delta=(xMax-xOri)/(labels.length-1);
        float textOffset=-ScreenUtil.dip2px(getContext(),8);
        for(int i=0;i<labels.length;i++){
            canvas.drawText(labels[i],xOri+i*delta+textOffset,xAxis+getResources().getDimensionPixelSize(R.dimen.text_size_small)+5,paint);
            canvas.drawCircle(xOri+i*delta,xAxis ,ScreenUtil.dip2px(getContext(),2),pointPaint);
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
        //画点
        for(int i=0;i<size;i++){
            Point p=list.get(i);
            if(p.isBreak) continue;
            canvas.drawCircle(p.x,p.y,5,pPaint);
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
        if(type==TYPE_MONTH){
            String[] labels=x_axisLabels[TYPE_MONTH];
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            calendar.setTimeInMillis(System.currentTimeMillis());
            int day=calendar.getActualMaximum(Calendar.DATE);
            LogUtil.d("trendview---max day="+day);
            if(day==28){
                labels[labels.length-1]="";
            }else if(day==29){
                labels[labels.length-1]="29";
            }else {
                labels[labels.length-1]="29-"+day;
            }
        }
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

    //取值3个刻度点   (草泥马，变4个刻度了)
    /*public void setData(byte[] src){
        if(src==null)src=new byte[0];
        list=new ArrayList<>();
        float[] value={bad,bad,well,good};
        float xMax=width-contentPadding-10;
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
        postInvalidate();
    }*/

    //取值3个刻度点   (草泥马，变4个刻度了)
    public void setData(byte[] src){
        if(src==null)src=new byte[0];
        list=new ArrayList<>();
        float[] value={xAxis,bad,well,good};
        float xMax=width-contentPadding-10;
        float xOri=contentPadding+10;
        float delta=(xMax-xOri)/(src.length-1);
        if(type==TYPE_YEAR){
            delta=(xMax-xOri)/11;
        }
        for(int i = 0;i<src.length;i++){
            Point p=new Point();
            p.x=xOri+i*delta;
            p.y=value[src[i]];
//            if(src[i]==0){
//                p.isBreak=true;
//            }else {
//                p.isBreak=false;
//            }
            list.add(p);
        }
        if(type==TYPE_YEAR){
            list.get(list.size()-1).isBreak=true;
            for(int i=list.size();i<12;i++){
                Point p=new Point();
                p.x=xOri+i*delta;
                p.y=value[0];
                p.isBreak=true;
                list.add(p);
            }
        }
        postInvalidate();
    }

}
