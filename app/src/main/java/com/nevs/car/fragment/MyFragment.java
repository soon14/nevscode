package com.nevs.car.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.nevs.car.R;
import com.nevs.car.activity.my.AboutActivity;
import com.nevs.car.activity.my.CarCopyActivity;
import com.nevs.car.activity.my.CustomScanActivity;
import com.nevs.car.activity.my.DatasActivity;
import com.nevs.car.activity.my.MyCarActivity;
import com.nevs.car.activity.my.MyDriveActivity;
import com.nevs.car.activity.my.MyFixActivity;
import com.nevs.car.activity.my.MyNotifyActivity;
import com.nevs.car.activity.my.MyOrderActivity;
import com.nevs.car.activity.my.SettingActivity;
import com.nevs.car.activity.my.ShareActivity;
import com.nevs.car.activity.my.UserActivity;
import com.nevs.car.activity.my.UserSpeakActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseFragment;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.BitmapUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.CircleImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by mac on 2018/4/2.
 */

public class MyFragment extends BaseFragment {
    @BindView(R.id.user_icon)
    CircleImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_phone)
    TextView userPhone;
    Unbinder unbinder;
    Unbinder unbinder1;
    @BindView(R.id.circleis)
    TextView circleis;
    @BindView(R.id.carcopy)
    RelativeLayout carcopy;
    @BindView(R.id.carcopeline)
    LinearLayout carcopeline;
    @BindView(R.id.hotline)
    TextView hotline;
    @BindView(R.id.view_bar)
    LinearLayout viewBar;
    private SharedPHelper sharedPHelper;
    private boolean flagtwo = false;
    private String telephone = "";
    private final int REQUEST_CODE_SCAN = 1001;
    private myreceiver recevier;
    private IntentFilter intentFilter;
    private List<HashMap<String, Object>> lisNotice = new ArrayList<>();
    private List<HashMap<String, Object>> lisNotice2 = new ArrayList<>();
    private List<HashMap<String, Object>> lisNotice3 = new ArrayList<>();
    private int publics = 0;
    private int messages = 0;
    private int loves = 0;

    public static MyFragment newInstance() {
        Bundle args = new Bundle();
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(viewBar,getContext());
        sharedPHelper = new SharedPHelper(getContext());
        //userName.setText(sharedPHelper.get(Constant.NAMES, getContext().getResources().getString(R.string.nevs_user)).toString());
        // userPhone.setText(sharedPHelper.get(Constant.LOGINNAME, getContext().getResources().getString(R.string.re_phonenumber)).toString());
        telephone = String.valueOf(sharedPHelper.get(Constant.LOGINNAME, ""));
        //userPhone.setText(telephone);
        hotline.setText(sharedPHelper.get(Constant.LOGINHOTLINE, "") + "");
        initRecevier();
    }

    public class myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.e("收到广播MyFragment");
            upView();
        }
    }

    private void initRecevier() {
        recevier = new myreceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("MAINACTIVITY.INITENT.USER");
        //当网络发生变化的时候，系统广播会发出值为android.net.conn.CONNECTIVITY_CHANGE这样的一条广播
        getActivity().registerReceiver(recevier, intentFilter);
    }

    private void upView() {
        if (BitmapUtil.getBitmapFromLocal(Constant.ICONBITMAPNAME + sharedPHelper.get(Constant.LOGINNAME, "")) != null) {
            userIcon.setImageBitmap(BitmapUtil.getBitmapFromLocal(Constant.ICONBITMAPNAME + sharedPHelper.get(Constant.LOGINNAME, "")));
        } else {


//            if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("M")) {//男
//                userIcon.setBackgroundResource(R.mipmap.txnan);
//            } else if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("W")) {
//                userIcon.setBackgroundResource(R.mipmap.txn);
//            } else {
//                userIcon.setBackgroundResource(R.mipmap.zx);
//            }
            if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("M")) {//男
                userIcon.setBackgroundResource(R.mipmap.n_my_default);
            } else if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("W")) {
                userIcon.setBackgroundResource(R.mipmap.n_my_default);
            } else {
                userIcon.setBackgroundResource(R.mipmap.n_my_default);
            }

        }
        if (new SharedPHelper(getContext()).get("JPUSHS", "0").equals("1")) {//1you0没有)
            circleis.setVisibility(View.VISIBLE);
        } else {
            circleis.setVisibility(View.GONE);
        }
        if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
            carcopy.setVisibility(View.GONE);
            carcopeline.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MLog.e("onStartmyFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (String.valueOf(sharedPHelper.get(Constant.NAMES, "")).equals("")) {
            userName.setText(MyUtils.dosubtext(telephone, getContext()));
        } else {
            userName.setText(new SharedPHelper(getContext()).get(Constant.NAMES, "")+"");
        }
        upView();
        getList();
        MLog.e("onResumemyFragment");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.e("onHiddenChangedMyFragment");

        try {
            if (hidden) {

            } else {
                if (new SharedPHelper(getContext()).get("JPUSHS", "0").equals("1")) {//1you0没有)
                    circleis.setVisibility(View.VISIBLE);
                } else {
                    circleis.setVisibility(View.GONE);
                }

                if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                    carcopy.setVisibility(View.GONE);
                    carcopeline.setVisibility(View.GONE);
                }else {
                    carcopy.setVisibility(View.VISIBLE);
                    carcopeline.setVisibility(View.VISIBLE);
                }

                getList();

            }

        } catch (Exception e) {
            MLog.e("onHiddenChangedMyFragmen异常");
        }


    }

    @OnClick({R.id.user_imagebutten, R.id.centeruser, R.id.mycar, R.id.mydrive,
            R.id.carcopy, R.id.safe, R.id.datas, R.id.setting, R.id.userspeak, R.id.safe0,
            R.id.share, R.id.about,R.id.n_notify, R.id.orders, R.id.service, R.id.rel_zxing})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.user_imagebutten://右标
                getActivity().startActivityForResult(new Intent(getContext(), UserActivity.class), 1001);
                break;
            case R.id.centeruser://整体点击进入
                getActivity().startActivityForResult(new Intent(getContext(), UserActivity.class), 1001);
                break;
            case R.id.n_notify://我的通知
                startActivity(new Intent(getContext(), MyNotifyActivity.class));
                new SharedPHelper(getContext()).put("JPUSHS", "0");
                break;
            case R.id.orders://我的预约
                if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                    ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_unbind));
                } else {
                    startActivity(new Intent(getContext(), MyOrderActivity.class));
                }

                break;
            case R.id.service://我的维修
                if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                    ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_unbind));
                } else {
                    startActivity(new Intent(getContext(), MyFixActivity.class));
                }

                break;
            case R.id.mycar://我的车辆
                startActivity(new Intent(getContext(), MyCarActivity.class));
                break;
            case R.id.mydrive://我的驾照
                startActivity(new Intent(getContext(), MyDriveActivity.class));
                // startActivity(new Intent(getContext(), UploadPictureActivity.class));
                break;
            case R.id.carcopy://行车记录
//                flagtwo = (boolean) sharedPHelper.get("swichtwo", false);
//                if (flagtwo) {
//                    startActivity(new Intent(getContext(), CarCopyActivity.class));
//                } else {
//                    ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_setopen));
//                }

                getTsp22();

                break;
            case R.id.safe0://故障救援d
                callSafe(sharedPHelper.get(Constant.LOGINRESCUE, "") + "");
                break;
            case R.id.safe://客服热线
                callSafe(sharedPHelper.get(Constant.LOGINHOTLINE, "") + "");
                break;
            case R.id.datas://流量查询
                String isaUT=new SharedPHelper(getContext()).get(Constant.isAuthenticated,"")+"";
                if(isaUT.equals("True")){
                    startActivity(new Intent(getContext(), DatasActivity.class));
                }else {
                    ActivityUtil.showToast(getContext(),getResources().getString(R.string.n_unath));
                }

                break;
            case R.id.setting://设置
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.userspeak://用户反馈
                startActivity(new Intent(getContext(), UserSpeakActivity.class));
                break;
            case R.id.share://分享给朋友
                startActivity(new Intent(getContext(), ShareActivity.class));
                break;
            case R.id.about://关于
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            case R.id.rel_zxing://关于
                startZxing();
                break;
        }
    }

    private void getTsp22() {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(getContext(), true);
        TspRxUtils.getSetting(getContext(),
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(getContext()).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(getContext()).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) getActivity());
                        MyUtils.upLogTSO(getContext(), "获取行程记录开启状态", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        boolean enabled = (boolean) obj;
                        if (enabled) {
                            startActivity(new Intent(getContext(), CarCopyActivity.class));
                        } else {
                            ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_setopen));
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) getActivity());
                        MyUtils.upLogTSO(getContext(), "获取行程记录开启状态", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        // ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_network));
                        if (str.contains("400")) {
                            ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_setopen));
                        } else if (str.contains("服务器地址未找到") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(getContext(), getResources().getString(R.string.neterror));
                        }
                    }
                }
        );

    }

    private void startZxing() {
        new IntentIntegrator(getActivity())
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }


    private void callSafe(String ss) {
        DialogUtils.call(getContext(), false, ss);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle(getResources().getString(R.string.cancel))
//                .setMessage(Constant.PHONENUMBER)
//                .setPositiveButton(getResources().getString(R.string.call_enter), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.PHONENUMBER));
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                    }
//                }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=recevier)
        getActivity().unregisterReceiver(recevier);
        unbinder1.unbind();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // 扫描二维码/条码回传
//        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
//            if (data != null) {
//
//                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描结果为：" + content);
//            }
//        }
//    }

    private void getList() {
        //DialogUtils.loading(getContext(), true);
        lisNotice.clear();
        publics = 0;
        HttpRxUtils.getAnnouncement(
                getContext(),
                new String[]{"accessToken", "type"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                        "90111003"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        // DialogUtils.hidding(getActivity());
                        lisNotice.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (lisNotice.size() == 0) {

                        } else {
                            int a = 0;
                            for (int i = 0; i < lisNotice.size(); i++) {
                                if (lisNotice.get(i).get("is_read").equals("NO")) {
                                    a++;
                                }
                            }
                            try {
                                if (a != 0) {
                                    circleis.setVisibility(View.VISIBLE);
                                    publics = a;
                                } else {
                                    circleis.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                MLog.e("publics处于后台");
                            }
                        }
                        getList2();
                    }

                    @Override
                    public void onFial(String str) {
                        // DialogUtils.hidding(getActivity());
                        getList2();
//                        switch (str) {
//                            case Constant.HTTP.HTTPFAIL:
//                                ActivityUtil.showToast(PublicNoticeActivity.this, getResources().getString(R.string.toast_network));
//                                break;
//                            default:
//                                ActivityUtil.showToast(PublicNoticeActivity.this, str);
//                        }
                    }
                });
    }

    private void getList2() {
        lisNotice2.clear();
        messages = 0;
        HttpRxUtils.getAnnouncement(
                getContext(),
                new String[]{"accessToken", "type"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                        ""
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        lisNotice2.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (lisNotice2.size() == 0) {
                        } else {
                            int b = 0;
                            for (int i = 0; i < lisNotice2.size(); i++) {
                                if (lisNotice2.get(i).get("is_read").equals("NO")) {
                                    b++;
                                }
                            }
                            try {
                                if (b != 0) {
                                    circleis.setVisibility(View.VISIBLE);
                                    messages = b;
                                } else {
                                    if (publics == 0) {
                                        circleis.setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception e) {
                                MLog.e("messages处于后台");
                            }

                        }
                        getList3();
                    }

                    @Override
                    public void onFial(String str) {
                        getList3();
//                        public404.setVisibility(View.VISIBLE);
//                        xRefreshView.stopRefresh();
//                        switch (str) {
//                            case Constant.HTTP.HTTPFAIL:
//                                ActivityUtil.showToast(MyNotifyActivity.this, getResources().getString(R.string.toast_network));
//                                break;
//                            default:
//                                ActivityUtil.showToast(MyNotifyActivity.this, str);
//                        }
                    }
                });
    }

    private void getList3() {
        lisNotice3.clear();
        loves = 0;
        TspRxUtils.getNohistory(getContext(),
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(getContext()).get(Constant.ACCESSTOKENS, "")},
                new String[]{"beginTime", "endTime", "pageIndex", "pageSize"},
                new Object[]{HashmapTojson.getTime() - 24 * 3600 * 180, HashmapTojson.getTime(), 0, 20},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        lisNotice3.addAll((Collection<? extends HashMap<String, Object>>) list);
                        MyUtils.upLogTSO(getContext(), "爱车通知列表", String.valueOf(list), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        if (lisNotice3.size() == 0) {

                        } else {
                            int c = 0;
                            for (int i = 0; i < lisNotice3.size(); i++) {
                                if (Boolean.parseBoolean(lisNotice3.get(i).get("isRead") + "") == false) {
                                    c++;
                                }
                            }
                            try {
                                if (c != 0) {
                                    circleis.setVisibility(View.VISIBLE);
                                } else {
                                    if (publics == 0 && messages == 0) {
                                        circleis.setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception e) {
                                MLog.e("loves处于后台");
                            }

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        MyUtils.upLogTSO(getContext(), "爱车通知列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }
}
