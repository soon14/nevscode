package com.nevs.car.activity.my;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.interfaces.OnResponseListener;
import com.nevs.car.tools.util.BitmapUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.WXShare;

import butterknife.BindView;
import butterknife.OnClick;

public class ShareActivity extends BaseActivity {
    @BindView(R.id.zxing)
    ImageView zxing;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private WXShare wxShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_share;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        wxShare = new WXShare(this);
        wxShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
                // 分享成功
                MLog.e("sd");
            }

            @Override
            public void onCancel() {
                // 分享取消
                MLog.e("sd");
            }

            @Override
            public void onFail(String message) {
                // 分享失败
                MLog.e("sd");
            }
        });

        initLong();

    }

    private void initLong() {
        zxing.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPoalShare(mContext);
                return false;
            }
        });
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

    @OnClick({R.id.back, R.id.id_py, R.id.id_pyquan})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.id_py:
                // startActivity(new Intent(this, WXEntryActivity.class).putExtra("TYPE", SendMessageToWX.Req.WXSceneSession));
                MLog.e("Sdf");
                wxShare.shareu(0, "https://www.nevs.com/", getResources().getString(R.string.nevs_official_website));
                break;
            case R.id.id_pyquan:
                // startActivity(new Intent(this, WXEntryActivity.class).putExtra("TYPE", SendMessageToWX.Req.WXSceneTimeline));
                wxShare.shareu(1, "https://www.nevs.com/", getResources().getString(R.string.nevs_official_website));
                break;
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


}
