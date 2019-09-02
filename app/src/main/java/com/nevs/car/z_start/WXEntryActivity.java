package com.nevs.car.z_start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.WXShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

//    private IWXAPI api;
//    private WXShare wxShare;
//    private int type=0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wxentry);
//        wxShare = new WXShare(this);
//        type=getIntent().getIntExtra("TYPE",0);
//        wxShare.setListener(new OnResponseListener() {
//            @Override
//            public void onSuccess() {
//                ActivityUtil.showToast(WXEntryActivity.this,getResources().getString(R.string.toast_sharesuc));
//                MLog.e("fscg");
//            }
//            @Override
//            public void onCancel() {
//                ActivityUtil.showToast(WXEntryActivity.this,getResources().getString(R.string.toast_sharecancle));
//            }
//            @Override
//            public void onFail(String message) {
//                ActivityUtil.showToast(WXEntryActivity.this,getResources().getString(R.string.toast_sharefail));
//            }
//        });
//        MLog.e("WXEntryActivity");
//       // WXShare share = new WXShare(this);
//        api = wxShare.getApi();
//       // wxShare.share("这是要分享的文字");
//
//        wxShare.shareu(type);
//
//       // wxShare.shareUrl(0,this,"https://open.weixin.qq.com","微信分享","I am so crazy");
//        //注意：
//        // 第三方开发者如果使用透明界面来实现WXEntryActivity，
//        // 需要判断handleIntent的返回值，如果返回值为false，
//        // 则说明入参不合法未被SDK处理，应finish当前透明界面，避
//        // 免外部通过传递非法参数的Intent导致停留在透明界面，
//        // 引起用户的疑惑
//        try {
//            if (!api.handleIntent(getIntent(), this)) {
//                finish();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        wxShare.register();
//    }
//
//    @Override
//    protected void onDestroy() {
//        wxShare.unregister();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        MLog.e("onNewIntent");
//        setIntent(intent);
//        if (!api.handleIntent(intent, this)) {
//            finish();
//        }
//    }
//    @Override
//    public void onReq(BaseReq baseReq) {
//
//    }
//    @Override
//    public void onResp(BaseResp baseResp) {
//        Intent intent = new Intent(WXShare.ACTION_SHARE_RESPONSE);
//        intent.putExtra(WXShare.EXTRA_RESULT, new WXShare.Response(baseResp));
//        sendBroadcast(intent); finish();
//    }
//
//
//}


    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.e("WXEntryActivity");
        WXShare share = new WXShare(this);
        api = share
                //                .register()
                .getApi();

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            if (!api.handleIntent(getIntent(), this)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        MLog.e("onNewIntent");
        setIntent(intent);
        if (!api.handleIntent(intent, this)) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent(WXShare.ACTION_SHARE_RESPONSE);
        intent.putExtra(WXShare.EXTRA_RESULT, new WXShare.Response(baseResp));
        sendBroadcast(intent);
        finish();
    }

}

