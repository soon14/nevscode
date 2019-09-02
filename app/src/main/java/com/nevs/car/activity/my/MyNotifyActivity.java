package com.nevs.car.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ListUnrBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyNotifyActivity extends BaseActivity {

    @BindView(R.id.circleone)
    TextView circleone;
    @BindView(R.id.circletwo)
    TextView circletwo;
    @BindView(R.id.circlethree)
    TextView circlethree;
    @BindView(R.id.tv_do)
    TextView tvDo;
    @BindView(R.id.imageone)
    ImageView imageone;
    @BindView(R.id.imageoneb)
    ImageView imageoneb;
    @BindView(R.id.imagetwo)
    ImageView imagetwo;
    @BindView(R.id.imagetwob)
    ImageView imagetwob;
    @BindView(R.id.imagethree)
    ImageView imagethree;
    @BindView(R.id.imagethreeb)
    ImageView imagethreeb;
    @BindView(R.id.rel_visi)
    RelativeLayout relVisi;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private List<HashMap<String, Object>> lisNotice = new ArrayList<>();
    private List<HashMap<String, Object>> lisNotice2 = new ArrayList<>();
    private List<HashMap<String, Object>> lisNotice3 = new ArrayList<>();
    private int publics = 0;
    private int messages = 0;
    private int loves = 0;
    private boolean isEdit = false;
    private List<ListUnrBean> listUnone = new ArrayList<>();
    private List<ListUnrBean> listUntwo = new ArrayList<>();
    private List<ListUnrBean> listUn = new ArrayList<>();
    private boolean isOne = false;
    private boolean isTwo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_notify;
    }

    @Override
    public void init(Bundle savedInstanceState) {
MyUtils.setPadding(nView,mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();//获取公告通知列表
    }


    @OnClick({R.id.back, R.id.tv_do, R.id.rel_tell, R.id.rel_message, R.id.rel_love, R.id.text_all, R.id.textViewKill})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_do:
//                startActivity(new Intent(MyNotifyActivity.this,MyNotifyEnterActivity.class)
//                        .putExtra("messageone",publics+"")
//                .putExtra("messagetwo",messages+""));
                //          .putExtra("messagethree",lisNotice3.size()+""));

                if (isEdit) {
                    getEditCancle();
                } else {
                    getEdit();
                }

                break;
            case R.id.rel_tell:
                if (isEdit) {
                    if (imageone.getVisibility() == View.VISIBLE) {
                        imageone.setVisibility(View.INVISIBLE);
                        isOne = false;
                    } else {
                        imageone.setVisibility(View.VISIBLE);
                        isOne = true;
                    }
                } else {
                    startActivity(new Intent(MyNotifyActivity.this, PublicNoticeActivity.class));
                }
                break;
            case R.id.rel_message:
                if (isEdit) {
                    if (imagetwo.getVisibility() == View.VISIBLE) {
                        imagetwo.setVisibility(View.INVISIBLE);
                        isTwo = false;
                    } else {
                        imagetwo.setVisibility(View.VISIBLE);
                        isTwo = true;
                    }
                } else {
                    startActivity(new Intent(MyNotifyActivity.this, MessageNoticeActivity.class));
                }
                break;
            case R.id.rel_love:
                if (isEdit) {
                    if (imagethree.getVisibility() == View.VISIBLE) {
                        imagethree.setVisibility(View.INVISIBLE);
                    } else {
                        imagethree.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (new SharedPHelper(mContext).get("TSPVIN", "0").equals("0")) {
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_unbind));
                    } else {
                        startActivity(new Intent(MyNotifyActivity.this, LoveNoticeActivity.class));
                    }
                }

                break;

            case R.id.text_all:
//                textOne.setVisibility(View.INVISIBLE);
//                textTwo.setVisibility(View.INVISIBLE);
//                textThree.setVisibility(View.INVISIBLE);
                getIsAll();
                break;
            case R.id.textViewKill:
                // linAll.setVisibility(View.GONE);
                getKill();
                break;
        }
    }

    private void getEditCancle() {
        tvDo.setText(getResources().getString(R.string.editor));
        isEdit = false;
        relVisi.setVisibility(View.GONE);
        imageone.setVisibility(View.GONE);
        imageoneb.setVisibility(View.GONE);
        imagetwo.setVisibility(View.GONE);
        imagetwob.setVisibility(View.GONE);
        imagethree.setVisibility(View.GONE);
        imagethreeb.setVisibility(View.GONE);
    }

    private void getEdit() {
        tvDo.setText(getResources().getString(R.string.cancle));
        isEdit = true;
        relVisi.setVisibility(View.VISIBLE);
        imageone.setVisibility(View.INVISIBLE);
        imageoneb.setVisibility(View.VISIBLE);
        imagetwo.setVisibility(View.INVISIBLE);
        imagetwob.setVisibility(View.VISIBLE);
        imagethree.setVisibility(View.INVISIBLE);
        imagethreeb.setVisibility(View.VISIBLE);
    }

    private void getIsAll() {
        listUn.clear();
        listUnone.clear();
        listUntwo.clear();
        if (isTwo == true && isOne == true) {
            for (int i = 0; i < lisNotice.size(); i++) {
                listUn.add(new ListUnrBean(String.valueOf(lisNotice.get(i).get("un_id")), "YES", "NO", String.valueOf(lisNotice.get(i).get("n_id"))));
            }
            for (int j = 0; j < lisNotice2.size(); j++) {
                listUn.add(new ListUnrBean(String.valueOf(lisNotice2.get(j).get("un_id")), "YES", "NO", String.valueOf(lisNotice2.get(j).get("n_id"))));
            }
            getIsAlls(listUn, 1);
        } else if (isOne == true) {
            for (int i = 0; i < lisNotice.size(); i++) {
                listUnone.add(new ListUnrBean(String.valueOf(lisNotice.get(i).get("un_id")), "YES", "NO", String.valueOf(lisNotice.get(i).get("n_id"))));
            }
            getIsAlls(listUnone, 2);
        } else if (isTwo == true) {
            for (int i = 0; i < lisNotice2.size(); i++) {
                listUntwo.add(new ListUnrBean(String.valueOf(lisNotice2.get(i).get("un_id")), "YES", "NO", String.valueOf(lisNotice2.get(i).get("n_id"))));
            }
            getIsAlls(listUntwo, 3);
        }

    }

    private void getIsAlls(List<ListUnrBean> lis, final int id) {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(MyNotifyActivity.this).get(Constant.ACCESSTOKEN, ""),
                        lis
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (id) {
                            case 1:
                                circleone.setVisibility(View.GONE);
                                circletwo.setVisibility(View.GONE);
                                break;
                            case 2:
                                circleone.setVisibility(View.GONE);
                                break;
                            case 3:
                                circletwo.setVisibility(View.GONE);
                                break;
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void getKill() {
        listUn.clear();
        listUnone.clear();
        listUntwo.clear();
        if (isTwo == true && isOne == true) {
            for (int i = 0; i < lisNotice.size(); i++) {
                listUn.add(new ListUnrBean(String.valueOf(lisNotice.get(i).get("un_id")), "YES", "YES", String.valueOf(lisNotice.get(i).get("n_id"))));
            }
            for (int j = 0; j < lisNotice2.size(); j++) {
                listUn.add(new ListUnrBean(String.valueOf(lisNotice2.get(j).get("un_id")), "YES", "YES", String.valueOf(lisNotice2.get(j).get("n_id"))));
            }
            getKills(listUn, 1);
        } else if (isOne == true) {
            for (int i = 0; i < lisNotice.size(); i++) {
                listUnone.add(new ListUnrBean(String.valueOf(lisNotice.get(i).get("un_id")), "YES", "YES", String.valueOf(lisNotice.get(i).get("n_id"))));
            }
            getKills(listUnone, 2);
        } else if (isTwo == true) {
            for (int i = 0; i < lisNotice2.size(); i++) {
                listUntwo.add(new ListUnrBean(String.valueOf(lisNotice2.get(i).get("un_id")), "YES", "YES", String.valueOf(lisNotice2.get(i).get("n_id"))));
            }
            getKills(listUntwo, 3);
        }

    }

    private void getKills(List<ListUnrBean> lis, final int id) {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(MyNotifyActivity.this).get(Constant.ACCESSTOKEN, ""),
                        lis
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (id) {
                            case 1:
                                circleone.setVisibility(View.GONE);
                                circletwo.setVisibility(View.GONE);
                                break;
                            case 2:
                                circleone.setVisibility(View.GONE);
                                break;
                            case 3:
                                circletwo.setVisibility(View.GONE);
                                break;
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }


    private void getList() {
        DialogUtils.loading(this, true);
        lisNotice.clear();
        publics = 0;
        HttpRxUtils.getAnnouncement(
                MyNotifyActivity.this,
                new String[]{"accessToken", "type"},
                new Object[]{new SharedPHelper(MyNotifyActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "90111003"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding(MyNotifyActivity.this);
                        lisNotice.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (lisNotice.size() == 0) {

                        } else {
                            int a = 0;
                            for (int i = 0; i < lisNotice.size(); i++) {
                                if (lisNotice.get(i).get("is_read").equals("NO")) {
                                    a++;
                                }
                            }
                            if (a != 0) {
                                circleone.setVisibility(View.VISIBLE);
                                circleone.setText(a + "");
                                publics = a;
                            } else {
                                circleone.setVisibility(View.GONE);
                            }
                        }
                        getList2();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(MyNotifyActivity.this);
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
                MyNotifyActivity.this,
                new String[]{"accessToken", "type"},
                new Object[]{new SharedPHelper(MyNotifyActivity.this).get(Constant.ACCESSTOKEN, ""),
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
                                    circletwo.setVisibility(View.VISIBLE);
                                    circletwo.setText( "");
//                                    circletwo.setText(b + "");
                                    messages = b;
                                } else {
                                    circletwo.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                MLog.e("处于后台");
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
        TspRxUtils.getNohistory(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(MyNotifyActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"beginTime", "endTime", "pageIndex", "pageSize"},
                new Object[]{HashmapTojson.getTime() - 24 * 3600 * 180, HashmapTojson.getTime(), 0, 20},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        MyUtils.upLogTSO(mContext, "爱车通知列表", String.valueOf(list), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        lisNotice3.addAll((Collection<? extends HashMap<String, Object>>) list);
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
                                    circlethree.setVisibility(View.VISIBLE);
                                    circlethree.setText(c + "");
                                    loves = c;
                                } else {
                                    circlethree.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                MLog.e("处于后台");
                            }

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        MyUtils.upLogTSO(mContext, "爱车通知列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }
}
