package com.nevs.car.activity.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.BitmapUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.WXShare;
import com.nevs.car.z_start.Web2Activity;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.zxing)
    ImageView zxing;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    @BindView(R.id.ver_code)
    TextView verCode;
    private WXShare wxShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_about;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView, mContext);
        intVerCode();
        longSave();
        wxShare = new WXShare(this);
    }

    private void intVerCode() {
        if(Constant.isDebug){
            verCode.setText(getResources().getString(R.string.n_vercode)+MyUtils.getAppVersionName(mContext)+"beta");
        }else{
            verCode.setText(getResources().getString(R.string.n_vercode)+MyUtils.getAppVersionName(mContext));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        wxShare.register();
    }

    @Override
    protected void onDestroy() {
        wxShare.unregister();
        super.onDestroy();
    }

    private void longSave() {
        zxing.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPoalShare(mContext);
                return false;
            }
        });
    }

    @OnClick({R.id.back, R.id.nevs})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.nevs:
                startActivity(new Intent(this, Web2Activity.class).putExtra("URL", "https://www.nevs.com/").putExtra("TITLE", getResources().getString(R.string.nevs_official_website)));
                break;
        }
    }

    public void showPoalShare(final Context context) {
        final String[] stringItems = new String[]{
                context.getResources().getString(R.string.sharezing),
                context.getResources().getString(R.string.sharesave),
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).cancelText(getResources().getString(R.string.cancle)).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textGoal.setText(stringItems[position]);
                dialog.dismiss();
                switch (position) {
                    case 0:
                        showPoalShare2(mContext);
                        break;
                    case 1:
                        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.zxing_nevs);
                        BitmapUtil.saveImageToGallery(context, bmp);
                        break;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    public void showPoalShare2(final Context context) {
        final String[] stringItems = new String[]{
                context.getResources().getString(R.string.nevs_weixingfriend),
                context.getResources().getString(R.string.nevs_weixingfriends),
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).cancelText(getResources().getString(R.string.cancle)).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textGoal.setText(stringItems[position]);
                dialog.dismiss();
                switch (position) {
                    case 0:
                        wxShare.sharePicture(mContext, 0);
                        break;
                    case 1:
                        wxShare.sharePicture(mContext, 1);
                        break;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

}
