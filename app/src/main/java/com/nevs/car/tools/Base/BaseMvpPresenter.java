package com.nevs.car.tools.Base;

import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/4/10.
 */

public class BaseMvpPresenter<V extends BaseMvpView> implements Presenter<V> {
    private  V mvpView;
    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }
    /**
     * 当页面销毁的时候,需要把View=null,
     * 然后调用 System.gc();//尽管不会马上回收，只是通知jvm可以回收了，等jvm高兴就会回收
     */
    @Override
    public void detachView() {
        MLog.e("View已经被销毁了");
        mvpView = null;
        System.gc();
    }
    /**
     * 判断 view是否为空
     * @return
     */
    public  boolean isAttachView(){
        return mvpView != null;
    }
    /**
     * 返回目标view
     * @return
     */
    public  V getMvpView(){
        return mvpView;
    }
    /**
     * 检查view和presenter是否连接
     */
    public void checkViewAttach(){
        if(! isAttachView()){
            throw  new MvpViewNotAttachedException();
        }
    }
    /**
     * 自定义异常
     */
    public static   class  MvpViewNotAttachedException extends RuntimeException{
        public  MvpViewNotAttachedException(){
            super("请求数据前请先调用 attachView(MvpView) 方法与View建立连接");
        }
    }
}