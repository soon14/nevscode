package com.nevs.car.tools.Base;

/**
 * Created by mac on 2018/4/10.
 */

public interface Presenter<V extends BaseMvpView> {
    /**
     * presenter和对应的view绑定
     * @param mvpView  目标view
     */
    void attachView(V mvpView);
    /**
     * presenter与view解绑
     */
    void detachView();
}