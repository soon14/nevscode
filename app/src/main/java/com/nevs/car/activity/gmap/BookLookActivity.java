package com.nevs.car.activity.gmap;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BookLookActivity extends BaseActivity {


    @BindView(R.id.imageView)
    SubsamplingScaleImageView imageView;
    @BindView(R.id.activity_choose_book)
    LinearLayout activityChooseBook;
    private Dialog mDialog = null;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_book_look;

    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(activityChooseBook,mContext);
        initProgressDialog();
        initView();

    }

    private void initProgressDialog() {
        mDialog = DialogUtils.createLoadingDialog(mContext, getResources().getString(R.string.loading));
    }

    private void initView() {
        DialogUtils.webloading(mContext, true, mDialog);
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setMinScale(0.5F);//最小显示比例
        imageView.setMaxScale(10.0F);//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）
        //final String testUrl = "http://cache.attach.yuanobao.com/image/2016/10/24/332d6f3e63784695a50b782a38234bb7/da0f06f8358a4c95921c00acfd675b60.jpg";
        final String testUrl = Constant.HTTP.BANNERURL + "web/ElectronicFence.jpg";
        //下载图片保存到本地
        Glide.with(this)
                .load(testUrl).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                // 将保存的图片地址给SubsamplingScaleImageView,这里注意设置ImageViewState设置初始显示比例
                imageView.setImage(ImageSource.uri(Uri.fromFile(resource)), new ImageViewState(0.7F, new PointF(0, 0), 0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.webhidding((Activity) mContext, mDialog);
                    }
                }, 1000);

            }
        });
    }


    @OnClick({R.id.back, R.id.tv_title})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
