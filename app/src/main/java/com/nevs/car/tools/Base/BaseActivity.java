package com.nevs.car.tools.Base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gyf.barlibrary.ImmersionBar;
import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.BToast;
import com.nevs.car.tools.util.GetLanguageUtil;
import com.nevs.car.tools.util.LanguageUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.UmengUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder mBinder;
    protected ApolloBinder aBinder;
    protected Context mContext;
    private static final int PERMISSON_REQUESTCODE = 0;
    private ImmersionBar mImmersionBar;

    private Method noteStateNotSavedMethod;
    private Object fragmentMgr;
    private String[] activityClassName = {"Activity", "FragmentActivity","AppCompatActivity"};

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式状态栏
       // MyUtils.setWindow(BaseActivity.this);
        setContentView(getContentViewResId());
        //全局绑定
        mBinder = ButterKnife.bind(this);
        aBinder = Apollo.bind(this);
        mContext=this;
       //726 initBaiduPush();  初始化百度推送
        //全局设置状态栏颜色#778777
      //  StatusBarCompat.setStatusBarColor(this, Color.parseColor("#859885"));//79
        mImmersionBar = ImmersionBar.with(this).statusBarDarkFont(true);
        mImmersionBar.init();   //所有子类都将继承这些相同的属性

        getLanguage();//设置APP语言
        //全局初始化
        init(savedInstanceState);

       // getLocalPession();
       // requestCemera();

    }




    private void getLocalPession() {

    }


    public void initBaiduPush() {
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                "eEIMq4VZUja0vYncbGGVaOtB");
    }

    public abstract int getContentViewResId();

    public abstract void init(Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.e("BASEa onResume");
        UmengUtils.onResumeToActivity(mContext);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.e("BASEa onPause");
        UmengUtils.onPauseToActivity(mContext);
    }

    @Override
    protected void onDestroy() {
        // 取消绑定
        mBinder.unbind();
        super.onDestroy();
        aBinder.unbind();
        EventBus.getDefault().unregister(this);
        if (mImmersionBar != null){
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)//接收
    public void messageEventBus(final String event){
        if(event.contains(getResources().getString(R.string.proxy_success))){
            //DialogUtils.controllHint2(mContext,event);//成功显示
//            ActivityUtil.showLongToast(mContext,event);

            if(event.equals(getResources().getString(R.string.hint_bluecd))){
               // ActivityUtil.showUiLongToast(getResources().getString(R.string.opensuccess_air));
                myshowToast(getResources().getString(R.string.opensuccess_air));
            }else {
                myshowToast(event);
                MLog.e("eventshoudao");
            }


            MLog.e("ccc");
            new SharedPHelper(mContext).put(Constant.ISCLICKBLE,"0");//车况按钮是否可以点击 0不可以
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new SharedPHelper(mContext).put(Constant.ISCLICKBLE,"1");
                }
            },Constant.TIMESLUNXUN);
        }else if(event.equals("zh")||event.equals("cn")||event.equals("guide")){

        }else if(event.equals(getResources().getString(R.string.refusecontrol))){
           // ActivityUtil.showUiLongToast(event);
            myshowToast(event);
        }else {
            //DialogUtils.controllHint3(mContext,event);//失败显示
          //  ActivityUtil.showLongToast(mContext,event);
          //cc  ActivityUtil.showUiLongToast(event);
          //  ToastCompat.makeText(this, "show One Toast", Toast.LENGTH_LONG).show();
            myshowToast(event);
            MLog.e("fff");
        }
    }
  //   if (!EventBus.getDefault().isRegistered(this))
//  {
//      EventBus.getDefault().register(this);
//  }
    // EventBus.getDefault().unregister(this);
    // EventBus.getDefault().post("showhint");//发送
//    @Subscribe(threadMode = ThreadMode.MAIN)//接收
//    public void messageEventBus(String event){
//
//    }






    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

//    /**
//     * 显示提示信息
//     *
//     * @since 2.5.0
//     */
//    private void showMissingPermissionDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.notifyTitle);
//        builder.setMessage(R.string.notifyMsg);
//
//        // 拒绝, 退出应用
//        builder.setNegativeButton(R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//
//        builder.setPositiveButton(R.string.setting,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startAppSettings();
//                    }
//                });
//
//        builder.setCancelable(false);
//
//        builder.show();
//    }



    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    //点击软键盘外面消失
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    private void myshowToast(String str){
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.toast_layout,
//                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText(str);
//
//        Toast toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(layout);
//        toast.show();
        BToast.showToast(mContext,str,true);
    }



    private void getLanguage() {

        //判断用户是否是第一次登陆，第一次登陆使用系统的设置，如果不设置还是跟谁系统，如果设置了以后都用应用自己设置的语言。

        //获取是否第一次进入APP的状态值
        String isFisrst = ShareUtil.readIsFirst(mContext,"Isfirst","counst");
        MLog.e("第"+isFisrst+"次进入APP");
        //获取系统语言
        String able = GetLanguageUtil.getLanguage();

        if(isFisrst.equals("1")){
            //将APP的进入状态改为"2"
            ShareUtil.storeIsFirst(mContext,"2","Isfirst","counst");
            switch (able){//"zh"为中文，"cn"为英文
                case "zh":
                    GetLanguageUtil.zh(mContext);
                    break;
                case "cn":
                    GetLanguageUtil.cn(mContext);
                    break;
            }
        }else if (isFisrst.equals("2")){
            //获取用户设置语言状态,没有更改还是根据系统设置语言，如果改了就设为用户之前设置的语言
            String isSetting=ShareUtil.readSettingLanguage(mContext,"issettings", "issetting");
            //此时设置成哪种语言了需要在APP语言的设置界面写入缓存
            switch (isSetting){//""为未设置,"zh"为中文"cn"为英文
                case "":
                    MLog.e("11");
                    switch (able){//"zh"为中文，"cn"为英文
                        case "zh":
                            MLog.e("5511");
                            GetLanguageUtil.zh(mContext);
                            break;
                        case "cn":
                            MLog.e("6611");
                            GetLanguageUtil.cn(mContext);
                            break;
                    }
                    break;
                case "zh":
                    MLog.e("3311");
                    GetLanguageUtil.zh(mContext);
                    break;
                case "cn":
                    MLog.e("3311");
                    GetLanguageUtil.cn(mContext);
                    break;
            }
        }
    }




    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = languageWork(newBase);
        super.attachBaseContext(context);

    }

    private Context languageWork(Context context) {
        // 8.0及以上使用createConfigurationContext设置configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return updateResources(context);
        } else {
            return context;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = LanguageUtil.getLocale(context);
        if (locale==null) {
            return context;
        }
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }


//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (isFastDoubleClick()) {
//                return true;
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
//
//
//    private long lastClickTime=System.currentTimeMillis();
//    private  boolean isFastDoubleClick() {
//        long time = System.currentTimeMillis();
//        long timeD = time - lastClickTime;
//        if (timeD >= 0 && timeD <= 500) {
//            MLog.e("快速点击");
//            return true;
//        } else {
//            MLog.e("正常点击");
//            lastClickTime = time;
//            return false;
//        }
//    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        invokeFragmentManagerNoteStateNotSaved();
    }

    private void invokeFragmentManagerNoteStateNotSaved() {
        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        try {
            if (noteStateNotSavedMethod != null && fragmentMgr != null) {
                noteStateNotSavedMethod.invoke(fragmentMgr);
                return;
            }
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!(activityClassName[0].equals(cls.getSimpleName())
                    || activityClassName[1].equals(cls.getSimpleName())));

            Field fragmentMgrField = prepareField(cls, "mFragments");
            if (fragmentMgrField != null) {
                fragmentMgr = fragmentMgrField.get(this);
                noteStateNotSavedMethod = getDeclaredMethod(fragmentMgr, "noteStateNotSaved");
                if (noteStateNotSavedMethod != null) {
                    noteStateNotSavedMethod.invoke(fragmentMgr);
                }
            }

        } catch (Exception ex) {
        }
    }

    private Field prepareField(Class<?> c, String fieldName) throws NoSuchFieldException {
        while (c != null) {
            try {
                Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } finally {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    private Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
            }
        }
        return null;
    }

}




