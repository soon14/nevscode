package com.nevs.car.tools.util;


import com.nevs.car.tools.Base.Presenter;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-11
 * Time: 14:26
 * 类描述：
 *
 * @version :
 */
public interface PresenterFactory<P extends Presenter>{
      P crate();//创建presenter
}
