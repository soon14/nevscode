package com.nevs.car.activity.my;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataBuyActivity extends BaseActivity implements Animation.AnimationListener {


    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.tv_pmd)
    LinearLayout tvPmd;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_data_buy;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
//        tvPmd.init(getWindowManager());// 初始化必要参数
//        tvPmd.setSpeed(2.0);// 设置滚动速度
//        tvPmd.startScroll();// 开始滚动
        tvPmd.setLayoutAnimationListener(this);
        TranslateAnimation translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.translatepro);
        //tvPmd.setAnimation(translateAnimation);


        translateAnimation.setDuration(2000);
        translateAnimation.setStartTime(0);
        translateAnimation.setRepeatCount(Integer.MAX_VALUE);
        translateAnimation.setRepeatMode(Animation.REVERSE);

        // Animation translateAnimation = new TranslateAnimation(0,100,0,100);//平移动画  从0,0,平移到100,100
        //translateAnimation.setDuration(1500);//动画持续的时间为1.5s
        translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
        translateAnimation.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点


        tvPmd.setAnimation(translateAnimation);//给imageView添加的动画效果
        translateAnimation.startNow();//动画开始执行 放在最后即可


        // init1();
        // showdialog();
    }

    private void init1() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.animalone);
        LinearLayout animationTopRightView = (LinearLayout) this.findViewById(R.id.animation_top_right);
        animationTopRightView.startAnimation(anim);
        Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.animaltwo);
        LinearLayout animationTopLeftView = (LinearLayout) this.findViewById(R.id.animation_top_left);
        animationTopLeftView.startAnimation(anim2);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void startTranslateAnimation() {
        /**
         * TranslateAnimation第一种构造
         *
         * @param fromXDelta X方向开始的位置，取值类型是float，单位是px像素
         * @param toXDelta X方向结束的位置，取值类型是float，单位是px像素
         * @param fromYDelta Y方向开始的位置，同上
         * @param toYDelta Y方向结束的位置，同上
         */
        TranslateAnimation translateAnimation = new TranslateAnimation(0, tvPmd.getWidth() * 2, 0, tvPmd.getHeight());
        /**
         * TranslateAnimation第二种构造 在第一种构造的基础上增加了，移动距离的取值方式，通过Type来约束
         *
         * @param fromXType 用来约束pivotXValue的取值。取值有三种：Animation.ABSOLUTE，Animation.RELATIVE_TO_SELF，Animation.RELATIVE_TO_PARENT
         * Type：Animation.ABSOLUTE：绝对，如果设置这种类型，后面pivotXValue取值就必须是像素点；比如：在X方向上移动自己宽度的距离，fromXValue的取值是mIvTranslate.getWidth()
         *            Animation.RELATIVE_TO_SELF：相对于控件自己，设置这种类型，后面pivotXValue取值就会去拿这个取值是乘上控件本身的宽度；比如：在X方向上移动自己宽度的距离，fromXValue的取值是1.0f
         *            Animation.RELATIVE_TO_PARENT：相对于它父容器（这个父容器是指包括这个这个做动画控件的外一层控件）， 原理同上，
         * @param fromXValue 配合fromXType使用，原理在上面
         * @param toXType 原理同上
         * @param toXValue 原理同上
         * @param fromYType 原理同上
         * @param fromYValue 原理同上
         * @param toYType 原理同上
         * @param toYValue 原理同上
         */
        TranslateAnimation translateAnimation1 = new TranslateAnimation(TranslateAnimation.ABSOLUTE, tvPmd.getWidth(), TranslateAnimation.ABSOLUTE, tvPmd
                .getWidth() * 2f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0);
        //设置动画持续时长
        translateAnimation1.setDuration(3000);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        translateAnimation1.setFillAfter(true);
        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
        translateAnimation1.setFillBefore(true);
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        translateAnimation1.setRepeatMode(ScaleAnimation.REVERSE);
        //设置动画播放次数
        translateAnimation1.setRepeatCount(ScaleAnimation.INFINITE);
        //开始动画
        tvPmd.startAnimation(translateAnimation1);
//        //清除动画
//        tvPmd.clearAnimation();
//        //同样cancel（）也能取消掉动画
//        translateAnimation1.cancel();
    }

    @OnClick({R.id.back, R.id.tv_pmd})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_pmd:
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        FrameLayout.LayoutParams ll = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        ll.setMargins(0, 0, 0, 0);
        tvPmd.setLayoutParams(ll);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    private void showdialog() {//自定义布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_iosedit, null);//获取自定义布局
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);//设置自定义样式布局到对话框
        final AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
        dialog.setCancelable(false);

        final EditText editText = (EditText) view.findViewById(R.id.dialog_tv_contentedit);
        TextView left = (TextView) view.findViewById(R.id.dialog_left_tv);//右边
        TextView right = (TextView) view.findViewById(R.id.dialog_right_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//右边

                dialog.dismiss();
                MLog.e("dsd" + editText.getText().toString());
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }
}
