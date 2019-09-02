package com.nevs.car.tools.view;

import android.widget.TextView;

import com.nevs.car.z_start.MyApp;

/**
 * Created by mac on 2018/8/6.
 */

public class SingleView extends TextView{


    public SingleView() {
        super(MyApp.getInstance());
    }
    
    private static class SingletonInstance {
        private static final SingleView INSTANCE = new SingleView();
    }
    public static SingleView getInstance() {
        return SingletonInstance.INSTANCE;
    }


}
