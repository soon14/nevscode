package com.nevs.car.tools.view;

/**
 * Created by mac on 2018/6/10.
 */

public class SlidingMenu  {

//    private static final float radio = 0.3f;//菜单占屏幕宽度比
//    private final int mScreenWidth;
//    private final int mMenuWidth;
//    private boolean once = true;
//
//    public SlidingMenu(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mScreenWidth = ScreenUtil.getScreenWidth(context);
//        mMenuWidth = (int) (mScreenWidth * radio);
//        setOverScrollMode(View.OVER_SCROLL_NEVER);
//        setHorizontalScrollBarEnabled(false);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (once) {
//            LinearLayout wrapper = (LinearLayout) getChildAt(0);
//            wrapper.getChildAt(0).getLayoutParams().width = mScreenWidth;
//            wrapper.getChildAt(1).getLayoutParams().width = mMenuWidth;
//            once = false;
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        case MotionEvent.ACTION_UP:
//        int scrollX = getScrollX();
//        if (Math.abs(scrollX) > mMenuWidth / 2) {
//            this.smoothScrollTo(mMenuWidth, 0);
//        } else {
//            this.smoothScrollTo(0, 0);
//        }
//        return true;
//    }
//    return super.onTouchEvent(ev);
//}ev
}