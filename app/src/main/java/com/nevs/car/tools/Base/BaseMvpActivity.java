package com.nevs.car.tools.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-11
 * Time: 14:32
 * 类描述：
 *
 * @version :
 */
public class BaseMvpActivity<P extends Presenter<V>,V extends BaseMvpView> extends BaseActivity implements BaseMvpView,LoaderManager.LoaderCallbacks<P>{
    private final int BASE_LODER_ID = 1000;//loader的id值
    protected  P presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(BASE_LODER_ID,null,this);//初始化loader
    }

    @Override
    public int getContentViewResId() {
        return 0;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView((V)this);//presenter与view断开连接
    }

    @Override
    public void showLoding(String msg) {
    }

    @Override
    public void hideLoding() {
    }

    @Override
    public void showErr(String err) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Loader<P> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<P> loader, P data) {
        presenter = data;
    }

    @Override
    public void onLoaderReset(Loader<P> loader) {
      presenter = null;
    }

    @Override
    protected void onDestroy() {
        //通知销毁页面，并设置presenter为null
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
            System.gc();
        }
        super.onDestroy();

    }
}
