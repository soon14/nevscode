package com.nevs.car.mvp;

import com.nevs.car.tools.Base.BaseMvpView;

/**
 * Created by mac on 2018/4/9.
 */

public interface ILoginView extends BaseMvpView{

    /**
     * 获得界面上用户名的值
     * @return
     */
    String getUsername();
    /**
     * 获得界面上密码的值
     * @return
     */
    String getPassword();
    /**
     * 显示登录的结果
     * @param result
     */
    void showLoginResult(String result);
}


