package com.nevs.car.tools.radioself;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.nevs.car.R;

/**
 * Created by mac on 2018/6/1.
 */

public class MyRadio extends Button implements View.OnTouchListener{

    private boolean isTouched = false;//是否被按下

    private int touch = 1;//按钮被按下的次数

    public MyRadio(Context context){
        super(context);
        init();
    }

    public MyRadio(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public MyRadio(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        init();
    }

    protected void init(){
        setOnTouchListener(this);
    }

    public void setTouch(int touch){
        this.touch = touch;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(0 == touch%2){
            this.setBackgroundResource(R.drawable.myradio_active);
        }else {
            this.setBackgroundResource(R.drawable.myradio_inactive);
        }
        invalidate();
    }

    public void setTouched(boolean isTouched){
        this.isTouched = isTouched;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                onValueChangedListner.OnValueChanged(this.getText().toString());
                isTouched = true;
                touch ++;
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                break;
        }
        return true;
    }

    public interface OnValueChangedListner{
        void OnValueChanged(String value);
    }

    //实现接口，方便将当前按钮的值回调
    OnValueChangedListner onValueChangedListner;

    public void setOnValueChangedListner(OnValueChangedListner onValueChangedListner){
        this.onValueChangedListner = onValueChangedListner;
    }
}
