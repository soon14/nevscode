package com.nevs.car.tools.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nevs.car.R;
import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/7/12.
 */

public class MyCircleView extends View {


    private int ScrWidth,ScrHeight;


    private  float Percentage = 0.2f;

    public float getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(float arcWidth) {
        this.arcWidth = arcWidth;
    }

    private float arcWidth = 30.0f;

    //在measure中测量出自定义view的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ScrHeight = this.getMeasuredHeight();
        ScrWidth  = this.getMeasuredWidth();

    }
    public MyCircleView(Context context) {
        super(context);
        MLog.e("onConstructer1"+"------------------->>>>");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        MLog.e("onFinishInflate"+"------------------->>>>");
    }

    public MyCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MLog.e("onConstructer2"+"------------------->>>>");


    }

    public MyCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MLog.e("onConstructer3"+"------------------->>>>");

    }
    public void onDraw(Canvas canvas){
        //画布背景
       //cc MLog.e("ondraw"+"------------------->>>>>");
        //画布颜色
        //canvas.drawColor(Color.WHITE);

        //圆的圆心计算
        float cirX = ScrWidth / 2;
        float cirY = ScrHeight / 2 ;

        //圆的半径计算
        float radius = ScrHeight / 2.5f  ;//150;

        //圆和view四周的距离，这个距离的测量主要是为了创建下面的arcRF0和arcRF1
        float arcLeft = cirX - radius;  //圆弧距离view四周的距离
        float arcTop  = cirY - radius ;
        float arcRight = cirX + radius ;
        float arcBottom = cirY + radius ;

        //通过四个点，创建出一个正方形 arcRF0为最底层
        RectF arcRF0 = new RectF(arcLeft ,arcTop,arcRight,arcBottom);
        //创建出arcRF1，为了覆盖在arcRF0上面-2和+2为了更好的覆盖底层
        RectF arcRF1 = new RectF(arcLeft-2,arcTop-2,arcRight+2,arcBottom+2);
        //画笔初始化
        Paint PaintArc = new Paint();
        PaintArc.setAntiAlias(true);//使平滑
        //初始化画笔颜色
        PaintArc.setColor(getResources().getColor(R.color.n_ECDBC3));
        //PaintArc.setAlpha(1);

        //在一个正方形上面绘制起点为135° 圆饼角度为270，使用画笔为PaintArc
        canvas.drawArc(arcRF0, 135f, 270f, true, PaintArc);

        //画笔颜色调为黄色
        PaintArc.setColor(getResources().getColor(R.color.n_D1B48B));
        //在一个正方形上面绘制起点为135° 圆饼角度为Percentage，使用画笔为PaintArc，
        canvas.drawArc(arcRF1,135f,Percentage,true,PaintArc);


        //以上完成的是饼图的制作，要编程圆环形状，则需要在中间绘制一个白色的圆
        //画笔颜色调整为白色
        PaintArc.setColor(getResources().getColor(R.color.n_A08A6A));
        //圆心x,y,半径，所用画笔
        canvas.drawCircle(cirX,cirY,radius-arcWidth,PaintArc);

    }
    //此函数用于外界调用，传递进入百分比，即0~1的float，通过调用invaliate函数，
    // 让view重新调用ondraw(),实现更新
    public void refresh(float percent){
        if(percent >=1.0f){percent = 1.0f;}
        Percentage = (float)percent*270;
        String str1 = Percentage + "";
       //cc MLog.e("start refresh"+"------------------------->>>>>>>"+ str1);
        this.invalidate();
    }
}
