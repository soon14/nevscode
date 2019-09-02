package com.nevs.car.tools.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nevs.car.R;

/**
 * Created by mac on 2018/6/10.
 */

public class SwipeLayout extends FrameLayout {
    /**
     * 滑动状态
     */
    public enum SwipeState {
        OPEN, CLOSE, SWIPING
    }

    private SwipeState swipeState = SwipeState.CLOSE;//默认关闭状态
    private OnSwipeChangeListener onSwipeChangeListener;

    public OnSwipeChangeListener getOnSwipeChangeListener() {
        return onSwipeChangeListener;
    }

    public void setOnSwipeChangeListener(OnSwipeChangeListener onSwipeChangeListener) {
        this.onSwipeChangeListener = onSwipeChangeListener;
    }

    public interface OnSwipeChangeListener {
        void onOpen(SwipeLayout layout);

        void onClose(SwipeLayout layout);

        void onSwiping(SwipeLayout layout);


        // 将要打开     当前是 关闭状态 ----> 拖动
        void onStartOpen(SwipeLayout layout);

        //将要关闭    当前是 打开i状态--->拖动
        void onStartClose(SwipeLayout layout);

    }


    private ViewDragHelper mViewDragHelper;
    private ViewGroup mBackLayout;
    private ViewGroup mFrontLayout;
    private int mWidth;
    private int mHeight;
    private int mRange;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //1 .初始化 ViewDragHelper对象
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, callBack);
    }

    //2. 将touch 事件 转交给 mViewDragHelper
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 交给 mViewDragHelper 决定是否拦截
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.e("Log","onTouchEvent---ACTION_UP");
                if (swipeState == SwipeState.CLOSE) {
                    Log.e("Log","onTouchEvent---ACTION_UP--swipeState");

                    return false;
                }
                break;
        }
        // 让 mViewDragHelper 接收到 触摸事件
        try {
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //测量 会调用很多次
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // 测量完成后   值改变后才会调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //拖动范围
        mRange = mBackLayout.getMeasuredWidth();
    }

    /**
     * 放置 子view
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutInit(false);
    }

    private void layoutInit(boolean isOpen) {
        Rect frontRect = computeFrontRect(isOpen);
        //f放置  mFrontLayout
        mFrontLayout.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
        Rect backRect = computeBackRect(frontRect);

        mBackLayout.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

        //将 控件前置
        bringChildToFront(mFrontLayout);
    }

    /**
     * 计算 mBackLayout 矩形位置
     *
     * @param frontRect
     * @return
     */
    private Rect computeBackRect(Rect frontRect) {
        int left = frontRect.right;
        return new Rect(left, frontRect.top, left + mRange, frontRect.bottom);
    }

    /**
     * 计算 mFrontLayout矩形位置
     *
     * @param isOpen
     * @return
     */
    private Rect computeFrontRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mRange;
        } else {
            left = 0;
        }

        return new Rect(left, 0, left + mWidth, 0 + mHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //添加健壮性 判断
        //1 比如有 两个 或者 两个以上子view
        int childCount = getChildCount();
        if (childCount < 2) {
            throw new IllegalStateException("You must have 2 children at least!! 你得有 至少两个子view！！");
        }
        //2 校验都是viewGroup
        if (getChildAt(0) == null || !(getChildAt(0) instanceof ViewGroup) || getChildAt(1) == null && !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("your child must be instance of ViewGroup! 你的view 必须是 viewgroup 的子类 ");
        }

        //  后边菜单
        mBackLayout = (ViewGroup) findViewById(R.id.layout_back);
        //前置条目
        mFrontLayout = (ViewGroup) findViewById(R.id.layout_front);
    }

    //    3  mViewDragHelper 解析完 touch事件  ----》CallBack
    ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {

        /**
         *返回值决定是否 可以拖动
         * @param child   拖拽的view对象  子view
         * @param pointerId  多指   手指的id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

//            return child == mFrontLayout;
            return true;
        }

        /**
         * 当 view 被捕获的时候调用
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 获取横向  拖拽范围   不决定 能否拖动
         * 做伴随动画 计算执行时长   ，计算敏感度  >0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            //返回实际的拖动范围
            return mRange;
        }

        /**
         *  1.   修正  位置   left  2. 没有 开始真正的移动
         * @param child
         * @param left
         * @param dx
         * @return
         */

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //child  正在拖动的子view
            //left  建议达到的位置
            //dx  deltaX 水平方向的瞬间变化量
//            int currentLeft = mFrontLayout.getLeft();
//            System.out.println( "currentLeft = "+currentLeft+"dx"+dx+" =? "+left);
            if (child == mFrontLayout) {
                left = fixedFrontLeft(left);
            } else if (child == mBackLayout) {
                left = fixedBackLeft(left);
            }

            return left;
        }


        private int fixedFrontLeft(int left) {
            if (left < -mRange) {
                left = -mRange;
            } else if (left > 0) {
                left = 0;
            }
            return left;
        }

        private int fixedBackLeft(int left) {
            if (left < (mWidth - mRange)) {
                left = mWidth - mRange;
            } else if (left > mWidth) {
                left = mWidth;
            }
            return left;
        }

        /**
         * 位置改变的时候调用  1. 伴随动画 2. 状态变化 3.  添加回调
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//            System.out.println("onViewPositionChanged>>>mBackLayout " + mBackLayout.getLeft());
            //changedView 当前正在拖动的子view
            //left  clampViewPositionHorizontal  的返回值
//            top
//            dx   横向的瞬间变化量
            if (changedView == mFrontLayout) {  //拖动mFrontLayout  让 mBackLayout跟着出来
                mBackLayout.offsetLeftAndRight(dx);
            } else if (changedView == mBackLayout) {//mBackLayout 转交 给mFrontLayout
                mFrontLayout.offsetLeftAndRight(dx);
            }


            dispatchEvent();


            //手动 刷新  重新绘制
            invalidate();

        }

//        @Override
//        public int clampViewPositionVertical(View child, int top, int dy) {
//            return top;
//        }

        // 当 拖动的view 释放的时候调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //releasedChild  释放的view
            //xvel   释放时横向的速度 向左  -  +    停止后释放  0
            //yvel 释放时纵向的速度
//            System.out.println(" releasedChild = " + releasedChild + "::xvel = " + xvel);

            // 释放时 位置小于 -mRange 丙炔速度为0
           //cccc if (mFrontLayout.getLeft() < -mRange * 0.5f && xvel == 0) {
            if (mFrontLayout.getLeft() < -mRange * 0.2f && xvel == 0) {
                open();
            } else if (xvel < 0) { // 向左快速滑动
                open();
            } else {
                close();
            }
        }


    };

    /**
     * 1. 更新状态 2.添加回调
     */
    private void dispatchEvent() {
        SwipeState preState = swipeState;
        swipeState = updateState();
        if (onSwipeChangeListener != null) {
            onSwipeChangeListener.onSwiping(this);
            if (swipeState != preState) { //当前状态和上一个状态不一样
                if (swipeState == SwipeState.OPEN) {
                    onSwipeChangeListener.onOpen(this);
                } else if (swipeState == SwipeState.CLOSE) {
                    onSwipeChangeListener.onClose(this);
                } else if (preState == SwipeState.OPEN) {
                    onSwipeChangeListener.onStartClose(this);
                } else if (preState == SwipeState.CLOSE) {
                    onSwipeChangeListener.onStartOpen(this);
                }
            }
        }
    }

    /**
     * 获取当前 最新状态
     *
     * @return
     */
    private SwipeState updateState() {
        if (mFrontLayout.getLeft() == -mRange) { // 打开
            return SwipeState.OPEN;
        } else if (mFrontLayout.getLeft() == 0) {
            return SwipeState.CLOSE;
        }
        return SwipeState.SWIPING;
    }

    //  scroller 执行会调用此方法     computeScroll 会调用很多次
    @Override
    public void computeScroll() {
        super.computeScroll();
        //  是否继续触发动画
        if (mViewDragHelper.continueSettling(true)) {
            //执行动画
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void open(boolean isSmooth) {
        if (isSmooth) {
            int finalLeft = -mRange;// 最终的位置
            //  返回值 决定是否触发动画
            boolean b = mViewDragHelper.smoothSlideViewTo(mFrontLayout, finalLeft, 0);
            if (b) {
                // 执行动画
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            layoutInit(true);
        }
    }

    /**
     * 打开
     */
    public void open() {
        open(true);//默认平滑状态
    }

    public void close(boolean isSmooth) {
        if (isSmooth) {
            int finalLeft = 0;// 最终的位置
            //  返回值 决定是否触发动画
            boolean b = mViewDragHelper.smoothSlideViewTo(mFrontLayout, finalLeft, 0);
            if (b) {
                // 执行动画
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            layoutInit(false);
        }
    }

    /**
     * 关闭
     */
    public void close() {
        close(true);// 默认平滑关闭
    }
}
