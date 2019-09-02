package com.nevs.car.tools.interfaces;

import android.view.View;

/**
 * Created by mac on 2018/6/10.
 */

public interface OnCheckedChangeListener{
    void OnCheckedChangeListener(View v, Object o);
}
//    Object o每一次都需要转化,很麻烦也可以这样使用,
//public interface OnRecycleItemListener <T>{
//    void OnRecycleItemClick(View v,T o);
//}