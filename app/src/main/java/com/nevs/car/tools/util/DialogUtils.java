package com.nevs.car.tools.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.nevs.car.R;
import com.nevs.car.activity.AddBindGetActivity;
import com.nevs.car.activity.BindCarActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.interfaces.DialogHintListener;
import com.nevs.car.tools.interfaces.DialogTwoListener;
import com.nevs.car.z_start.MainActivity;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

/**
 * Created by mac on 2018/4/12.
 */

public class DialogUtils {
    public static BaseAnimatorSet mBasIn=new BounceTopEnter();
    public static BaseAnimatorSet mBasOut=new SlideBottomExit();
    public static Dialog  dialogs = null;
    public static  AnimationDrawable animationDrawable;
    private static android.support.v7.app.AlertDialog alertDialog = null;
//    private void showdialog() {//自定义布局
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.dialog, null);//获取自定义布局
//        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.selectorDialog);
//        builder.setView(view);//设置自定义样式布局到对话框
//        final AlertDialog dialog = builder.create();//获取dialog
//        dialog.show();//显示对话框
//
//        Button xzzp = (Button) view.findViewById(R.id.xzzp);
//        xzzp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                autoObtainStoragePermission();  //相册
//                dialog.dismiss();
//            }
//        });
//
//        Button pz = (Button) view.findViewById(R.id.pz);
//        pz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                autoObtainCameraPermission();//拍照
//                dialog.dismiss();
//            }
//        });
//    }

    public static void alertDialog(final Context context,boolean flag){
        AlertDialog aldialod=new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.notifyTitle))
                .setMessage(context.getResources().getString(R.string.hintbindcar))
                .setPositiveButton(context.getResources().getString(R.string.for_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(context,AddBindGetActivity.class));
                    }
                })
                .setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        aldialod.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
    }

    public static void NormalDialogOneBtn(final Context mContext) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content(mContext.getResources().getString(R.string.toast_hint))//
                .btnNum(1)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(21)//
                .titleTextColor(mContext.getResources().getColor(R.color.android_defalt))
                .title(mContext.getResources().getString(R.string.dialog_title))
                .btnTextColor(mContext.getResources().getColor(R.color.text_default))
                .btnText(mContext.getResources().getString(R.string.comm_comfirm))
                .btnTextSize(18)
                .contentTextColor(mContext.getResources().getColor(R.color.main_textss))
                .contentTextSize(14)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                ActivityManager.exit();
            }
        });
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失时为false
    }

    //双语
    public static void NormalDialogOneBtnHint(final Context mContext, String s, final EditText content, final Activity activity) {
        final NormalDialog dialogone = new NormalDialog(mContext);
        dialogone.content(s)//
                .btnNum(1)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(21)//
                .titleTextColor(mContext.getResources().getColor(R.color.android_defalt))
                .title(mContext.getResources().getString(R.string.dialog_title))
                .btnTextColor(mContext.getResources().getColor(R.color.text_default))
                .btnText(mContext.getResources().getString(R.string.comm_comfirm))
                .btnTextSize(18)
                .contentTextColor(mContext.getResources().getColor(R.color.main_textss))
                .contentTextSize(14)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialogone.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失时为false
        dialogone.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                try {
                   // dialogone.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            content.setText("");
                            activity.finish();
                        }
                    });

                }catch (Exception e){
                    MLog.e("崩溃");
                }


            }
        });

    }

    public static void NormalDialogOneBtnHintno(final Context mContext, String s) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content(s)//
                .btnNum(1)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(21)//
                .titleTextColor(mContext.getResources().getColor(R.color.android_defalt))
                .title(mContext.getResources().getString(R.string.dialog_title))
                .btnTextColor(mContext.getResources().getColor(R.color.text_default))
                .btnText(mContext.getResources().getString(R.string.comm_comfirm))
                .btnTextSize(18)
                .contentTextColor(mContext.getResources().getColor(R.color.main_textss))
                .contentTextSize(14)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();


            }
        });
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失时为false
    }

    public static void NormalDialogOneBtnHintnoEixt(final Context mContext, String s) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content(s)//
                .btnNum(1)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(21)//
                .titleTextColor(mContext.getResources().getColor(R.color.android_defalt))
                .title(mContext.getResources().getString(R.string.dialog_title))
                .btnTextColor(mContext.getResources().getColor(R.color.text_default))
                .btnText(mContext.getResources().getString(R.string.comm_comfirm))
                .btnTextSize(18)
                .contentTextColor(mContext.getResources().getColor(R.color.main_textss))
                .contentTextSize(14)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                ActivityManager.exit();


            }
        });
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失时为false
    }

    public static void NormalDialogStyleTwo(final Context context,boolean flag){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(context.getResources().getString(R.string.main_bind_dialod))//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(18)//
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .title(context.getResources().getString(R.string.dialog_title))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(16)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        new SharedPHelper(context).put(Constant.ISCONFORM,"0");
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                       // context.startActivity(new Intent(context,AddBindGetActivity.class));
                        context.startActivity(new Intent(context,BindCarActivity.class));

                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);

    }

    public static void showPoal(final Context context, final String[] stringItems, final TextView textView) {
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(stringItems[position]);
                textView.setTextColor(context.getResources().getColor(R.color.text_default));
                dialog.dismiss();
            }
        });
    }


    /**
     * 自定义透明的圆形进度条
     */
        /**
         * 得到自定义的progressDialog
         * @param context
         * @param msg
         * @return
         */
        public static Dialog createLoadingDialog(Context context, String msg) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
            // main.xml中的ImageView
           //cc ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
            TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
            // 加载动画
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    context, R.anim.load_animation);
            // 使用ImageView显示动画
           //cc spaceshipImage.startAnimation(hyperspaceJumpAnimation);
            tipTextView.setText(msg);// 设置加载信息
            Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

            loadingDialog.setCancelable(false);// 不可以用“返回键”取消
            loadingDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
            return loadingDialog;

        }
    public static Dialog createLoadingDialog0(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog0, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        //cc ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_animation);
        // 使用ImageView显示动画
        //cc spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog0);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;

    }
    public static void loading(Context context,boolean flag){
            try {
                //文字即为显示的内容
                dialogs = createLoadingDialog(context, context.getResources().getString(R.string.loading));
                dialogs.setCancelable(flag);//true点击BACK允许返回
                dialogs.show();//显示
            }catch (Exception E){

            }

    }
    public static void hidding(Activity context){
        if(dialogs!=null&&!context.isFinishing()&&dialogs.isShowing()){
            dialogs.dismiss();
//            mDialog=null;
//           mDialog.cancel();
        }
    }

    public static void loading0(Context context,boolean flag){
        //文字即为显示的内容
        dialogs = createLoadingDialog0(context, context.getResources().getString(R.string.loading));
        dialogs.setCancelable(flag);//true点击BACK允许返回
        dialogs.show();//显示

    }
    public static void hidding0(Activity context){
        if(dialogs!=null&&!context.isFinishing()){
            dialogs.dismiss();
//            mDialog=null;
//           mDialog.cancel();
        }
    }

    public static void webloading(Context context,boolean flag,Dialog mDialog){
        mDialog.setCancelable(flag);//true点击BACK允许返回
//        if(!mDialog.isShowing()) {
//            mDialog.show();//显示
//        }
        mDialog.show();

    }
    public static void webhidding(Activity context,Dialog mDialog){
        if(mDialog!=null&&!context.isFinishing()){
            mDialog.dismiss();
        }
    }



    public static void showPoals(final Context context) {
        final String[] stringItems = new String[]{context.getResources().getString(R.string.nevs_official),
                context.getResources().getString(R.string.nevs_private),
                context.getResources().getString(R.string.nevs_custom)};
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textGoal.setText(stringItems[position]);
                ActivityUtil.showToast(context,context.getResources().getString(R.string.toast_marksuc));
                dialog.dismiss();
            }
        });
    }

    public static void showPoalsTwo(final Context context, final TextView textView) {
        final String[] stringItems = new String[]{context.getResources().getString(R.string.nevs_official),
                context.getResources().getString(R.string.nevs_private),
                context.getResources().getString(R.string.nevs_custom)};
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(stringItems[position]);
                dialog.dismiss();
            }
        });
    }

    public static void showSex(Context context, final TextView textSex) {
         final String[] mStringItems = {context.getResources().getString(R.string.nevs_boy),
                 context.getResources().getString(R.string.nevs_girl)};
        final NormalListDialog dialog = new NormalListDialog(context, mStringItems);
        dialog.title(context.getResources().getString(R.string.toast_choose))//
                .layoutAnimation(null)
                .titleBgColor(context.getResources().getColor(R.color.text_default))
                .show(R.style.myDialogAnim);
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textSex.setText(mStringItems[position]);
                dialog.dismiss();
            }
        });
    }

    public static void call(final Context context, boolean flag, final String phonenumber){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(phonenumber)//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .btnText(context.getResources().getString(R.string.cancle),context.getResources().getString(R.string.for_confirm))
                .titleTextSize(18)//
                .title(context.getResources().getString(R.string.toast_iscall))
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(18)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +phonenumber));
                        context.startActivity(intent);
                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false

    }

    public static void controllHint(final Context context, final String str){
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setCancelable(false);//点击背景是对话框不会消失
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
        WindowManager.LayoutParams wm = window.getAttributes();
        wm.width = 600;//设置对话框的宽
        wm.height = 500;//设置对话框的高
        wm.alpha = 0.5f;//设置对话框的背景透明度
        wm.dimAmount = 0.6f;//遮罩层亮度
        window.setAttributes(wm);
        final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
        final TextView textView = (TextView) window.findViewById(R.id.text);
        final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
        imageView.setBackground(context.getResources().getDrawable(R.drawable.frame));
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                imageView.setBackground(context.getResources().getDrawable(R.mipmap.finish));
                linearLayout.setBackgroundResource(R.color.main_top);
                textView.setText(str);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, 1100);// 延迟关闭

            }
        }, 5000);
    }


    public static void controllHint1(final Context context,boolean flag,String s){//开启关闭
        try {
            if(flag){
                alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
                alertDialog.show();
                alertDialog.setCancelable(false);//点击背景是对话框不会消失
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
                WindowManager.LayoutParams wm = window.getAttributes();
                wm.width = 500;//设置对话框的宽
                wm.height = 280;//设置对话框的高
                wm.alpha = 0.5f;//设置对话框的背景透明度
                wm.dimAmount = 0.6f;//遮罩层亮度
                window.setAttributes(wm);
                final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
                final TextView textView = (TextView) window.findViewById(R.id.text);
                textView.setTextSize(18);
                textView.setText(s);
                final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.frame));
                 animationDrawable = (AnimationDrawable) imageView.getBackground();
                animationDrawable.start();
            }else {
                animationDrawable.stop();
                alertDialog.dismiss();

            }

        }catch (Exception e){

        }

    }

    public static void controllHint2(final Context context, final String str) {//完成的提示框
        try {
            final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
            alertDialog.show();
            alertDialog.setCancelable(false);//点击背景是对话框不会消失
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
            WindowManager.LayoutParams wm = window.getAttributes();
            wm.width = 500;//设置对话框的宽
            wm.height = 400;//设置对话框的高
            wm.alpha = 0.5f;//设置对话框的背景透明度
            wm.dimAmount = 0.6f;//遮罩层亮度
            window.setAttributes(wm);
            final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
            final TextView textView = (TextView) window.findViewById(R.id.text);
            textView.setTextSize(20);
            final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
            imageView.setBackground(context.getResources().getDrawable(R.mipmap.icon_success));
            linearLayout.setBackgroundResource(R.color.main_top);
            textView.setText(str);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                }
            }, 2000);

        } catch (Exception e) {

        }
    }
    public static void controllHint3(final Context context, final String str) {//失败的提示框
        try {
            final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
            alertDialog.show();
            alertDialog.setCancelable(false);//点击背景是对话框不会消失
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
            WindowManager.LayoutParams wm = window.getAttributes();
            wm.width = 500;//设置对话框的宽
            wm.height = 400;//设置对话框的高
            wm.alpha = 0.5f;//设置对话框的背景透明度
            wm.dimAmount = 0.6f;//遮罩层亮度
            window.setAttributes(wm);
            final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
            final TextView textView = (TextView) window.findViewById(R.id.text);
            textView.setTextSize(20);
            final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
            imageView.setBackground(context.getResources().getDrawable(R.mipmap.icon_error));
            linearLayout.setBackgroundResource(R.color.main_top);
            textView.setText(str);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                }
            }, 2000);

        } catch (Exception e) {

        }
    }


    public static void shareDiolag(Context context, final WXShare wxShare, final String url, final String title) {
       final Dialog dialog = new Dialog(context, R.style.Theme_Light_Dialog);
        final View dialogView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.share_pop_window, null);

        LinearLayout id_wxfrend = (LinearLayout) dialogView.findViewById(R.id.id_wxfrend);
        id_wxfrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wxShare.shareu(SendMessageToWX.Req.WXSceneSession,url,title);

            }
        });

        LinearLayout id_pyquan = (LinearLayout) dialogView.findViewById(R.id.id_pyquan);
        id_pyquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wxShare.shareu(SendMessageToWX.Req.WXSceneTimeline,url,title);

            }
        });
        //取消
        TextView shareback= (TextView) dialogView.findViewById(R.id.shareback);
        shareback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
    }




    public static void NormalDialogOneBtnHintExit(final Context mContext, final Activity activity) {
        final NormalDialog dialogone = new NormalDialog(mContext);
        dialogone.content(mContext.getResources().getString(R.string.hintexit))//
                .btnNum(1)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(21)//
                .titleTextColor(mContext.getResources().getColor(R.color.android_defalt))
                .title(mContext.getResources().getString(R.string.dialog_title))
                .btnTextColor(mContext.getResources().getColor(R.color.text_default))
                .btnText(mContext.getResources().getString(R.string.comm_comfirm))
                .btnTextSize(18)
                .contentTextColor(mContext.getResources().getColor(R.color.main_textss))
                .contentTextSize(14)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialogone.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失时为false
        dialogone.setCancelable(false);
        dialogone.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                try {
                    //dialogone.dismiss();
//                    content.setText("");
//                    activity.finish();
                    MyUtils.exitToLongin2(mContext);
                }catch (Exception e){
                    MLog.e("崩溃");
                }


            }
        });

    }
        public static void hint(final Context context, boolean flag, final DialogHintListener listener){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .btnText(context.getResources().getString(R.string.cancle),context.getResources().getString(R.string.for_confirm))
                .content(context.getResources().getString(R.string.dialog_hint))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(18)
                .titleTextSize(18)//
                .title(context.getResources().getString(R.string.dialog_title))
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        listener.callBack();
                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false

    }
    public static void hint1(final Context context, boolean flag, final String phonenumber,final DialogHintListener listener){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(phonenumber)//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .btnText(context.getResources().getString(R.string.cancle),context.getResources().getString(R.string.for_confirm))
                .titleTextSize(18)//
                .title(context.getResources().getString(R.string.dialog_title))
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(16)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                       listener.callBack();
                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false

    }
    public static void hintPermission(final Context context, boolean flag, final String content,final DialogTwoListener listener){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(content)//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .btnText(context.getResources().getString(R.string.cancle),context.getResources().getString(R.string.for_confirm))
                .titleTextSize(18)//
                .title(context.getResources().getString(R.string.dialog_title))
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(16)
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        listener.cancel();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        listener.confirm();
                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false

    }

}
