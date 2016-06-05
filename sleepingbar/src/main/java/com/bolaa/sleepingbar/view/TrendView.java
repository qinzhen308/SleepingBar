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

import java.util.List;

/**
 * Created by pualbeben on 16/6/5.
 */
public class TrendView extends ImageView{

    private List<Point> list;
    private int good;//优
    private int well;//良
    private int bad;//差


    public TrendView(Context context) {
        super(context);
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        good=20;
        well=getHeight()/2-20;
        bad=getHeight()-40;
        drawAxis(canvas);
    }

    private void drawAxis(Canvas canvas){
        Paint paint=new Paint();
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawLine(20,bad+20,getWidth()-20,bad+20,paint);
    }

    private void drawTrend(Canvas canvas){
        if(list==null||list.size()==0){
            return;
        }
        int maxX=getWidth()-20*2;
        int firstLocationX=0;
        int lastLocationX=getWidth()-20;
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
