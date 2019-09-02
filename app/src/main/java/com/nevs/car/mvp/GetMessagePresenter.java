package com.nevs.car.mvp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.nevs.car.tools.Base.BaseMvpPresenter;
import com.nevs.car.tools.util.MLog;


/**
 * Created by mac on 2018/4/9.
 */

public class GetMessagePresenter extends BaseMvpPresenter<ILoginView> {
    //private ILoginView iLoginView;
    private RequestBiz requestBiz;
    private Handler handler;
    private Context context;
    private static GetMessagePresenter sInstance;
    //,ILoginView iLoginView
    public GetMessagePresenter(Context context){
        this.context=context;
        //this.iLoginView=iLoginView;
    }

    public static synchronized GetMessagePresenter getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new GetMessagePresenter(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onClic(String tojson){
        requestBiz=RequestBizIml.getInstance();
        handler = new Handler(Looper.getMainLooper());
        checkViewAttach();//检查是否绑定
        final ILoginView iLoginView=getMvpView();//获得LoginView
        iLoginView.showLoding("正在登录中...");//loginView的ui逻辑处理
        requestBiz.requestForData(context,tojson,new OnRequestListener() {
            @Override
            public void onSuccess(final String data) {
                //由于请求开启了新线程，所以用handler去更新界面
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iLoginView == null) {
                            MLog.e("页面已经销毁，不在进行任何操作");
                            return;
                        }
                        iLoginView.hideLoding();
                        iLoginView.showLoginResult(data);
                    }
                });
            }
            @Override
            public void onFailed(String error) {
                if (iLoginView == null) {
                    MLog.e("页面已经销毁，不在进行任何操作");
                    return;
                }
                iLoginView.hideLoding();
                iLoginView.showLoginResult("error"+error);

            }
        });
    }
}
