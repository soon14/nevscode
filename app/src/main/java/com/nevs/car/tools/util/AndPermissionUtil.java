package com.nevs.car.tools.util;

import android.Manifest;
import android.content.Context;

import com.nevs.car.R;
import com.nevs.car.tools.interfaces.DialogTwoListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


public class AndPermissionUtil implements Rationale {
    /**调用AndPermission的activity的launchMode使用singleInstance无法调起弹窗。调试项目时发现的，做个记录
     * 特别注意：你在申请权限之前不需要判断版本和是否拥有某权限。
     */

    public  void applyPermisson(final Context context){
        AndPermission.with(context)
                .runtime()
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
//                        Manifest.permission.BLUETOOTH,
//                        Manifest.permission.BLUETOOTH_ADMIN
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .rationale(this)//添加拒绝权限回调
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // data.get(0);
                        MLog.e("permission"+data.get(0));

//                        Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
//                        startActivityForResult(intent, 110);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        /**
                         * 当用户没有允许该权限时，回调该方法
                         */
                        //Toast.makeText(context, "没有获取该权限，相应功能无法使用", Toast.LENGTH_SHORT).show();
                        MLog.e("没有获取该权限，相应功能无法使用1111");
                        /**
                         * 判断用户是否点击了禁止后不再询问，AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)
                         */
                        if (AndPermission.hasAlwaysDeniedPermission(context, data)) {
                            //true，弹窗再次向用户索取权限
                           // showSettingDialog(MainActivity.this, data);
                            MLog.e("没有获取该权限，相应功能无法使用2222");
                        }
                    }
                }).start();
    }

    @Override
    public void showRationale(Context context, Object data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, String.valueOf(data));
        String message = "请授权该下的权限" + "\n" + permissionNames;

//        new android.app.AlertDialog.Builder(context)
//                .setCancelable(false)
//                .setTitle("提示")
//                .setMessage(message)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        executor.execute();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        executor.cancel();
//                    }
//                })
//                .show();
        DialogUtils.hintPermission(context,false,context.getResources().getString(R.string.hintpermission),new DialogTwoListener() {
            @Override
            public void confirm() {
                executor.execute();
            }

            @Override
            public void cancel() {
                executor.cancel();
            }
        });

    }


/**
 * AndPermission.with(this)
 .requestCode(101)//请求权限码
 .permission(Manifest.permission.CAMERA)//权限
 .rationale { requestCode, rationale ->
 AlertDialog.newBuilder(this@LoginActivity)
 .setTitle("权限申请提醒")
 .setMessage("这里需要相机记录你的生活圈")
 .setPositiveButton("确定"){ dialog,_->
 dialog.cancel()
 rationale.resume()
 }
 .setNegativeButton("拒绝"){ dialog,_->
 dialog.cancel()
 rationale.cancel()
 }
 .show()
 }//请求重试
 .callback(this) //请求回调
 .start() //请求

 @PermissionYes(101)
 fun getCameraYes(list:List<String>){
 Toast.makeText(this,"权限请求成功",Toast.LENGTH_LONG).show()
 }

 @PermissionNo(101)
 fun getCameraNo(list:List<String>){
 //跳转系统设置去给权限
 AndPermission.defaultSettingDialog(this, 400)
 .setTitle("权限申请失败")
 .setMessage("您拒绝了我们必要的一些权限，已经没法愉快的玩耍了，请在设置中授权！")
 .setPositiveButton("好，去设置")
 .show();
 }
 * */





public  void applyPermissonLocation(final Context context, final DialogTwoListener dialogTwoListener){
    AndPermission.with(context)
            .runtime()
            .permission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE
            )
            .rationale(this)//添加拒绝权限回调
            .onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    // data.get(0);
                    MLog.e("permission"+data.get(0));
                    dialogTwoListener.confirm();
//                        Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
//                        startActivityForResult(intent, 110);
                }
            })
            .onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    /**
                     * 当用户没有允许该权限时，回调该方法
                     */
                    //Toast.makeText(context, "没有获取该权限，相应功能无法使用", Toast.LENGTH_SHORT).show();
                    MLog.e("没有获取该权限，相应功能无法使用1111");
                    dialogTwoListener.cancel();
                    /**
                     * 判断用户是否点击了禁止后不再询问，AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)
                     */
                    if (AndPermission.hasAlwaysDeniedPermission(context, data)) {
                        //true，弹窗再次向用户索取权限
                        // showSettingDialog(MainActivity.this, data);
                        MLog.e("没有获取该权限，相应功能无法使用2222");
                    }
                }
            }).start();
}
}
