package com.nevs.car.fragment.son;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.flyco.animation.Attention.Swing;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.nevs.car.R;
import com.nevs.car.activity.my.FixEnterActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.KeepAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ServiceEalution;
import com.nevs.car.tools.Base.BaseFragment;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mac on 2018/5/2.
 */

public class FixFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    Unbinder unbinder;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private boolean isFresh = true;//是否已被加载过一次，第二次就不再去请求数据了,第一次为true
    private BroadcastReceiver broadcastReceiver;
    private String vinTsp="";
    private List<String> listPing = new ArrayList<>();
    private CustomDialog dialog;
    private String se_score = null;
    private String se_evalution = null;
    private int poss=-1;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_historyorder;

    }

    @Override
    public void init(Bundle savedInstanceState) {
        initVin();
        initRecyclyView();
        initRecie();
    }

    private void initVin() {
        vinTsp=new SharedPHelper(getContext()).get("TSPVIN", "0").toString();
    }

    private void initRecie() {
        IntentFilter filter = new IntentFilter("sendBroadcastviewpager1");
        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
//接收传递过来的值
                String vin = intent.getStringExtra("vin");
                MLog.e("fix收到广播"+vin);
                getHttp(vin);

            }
        };
        getContext().registerReceiver(broadcastReceiver, filter);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible()) {
            if (isFresh) {
                //获取数据
                if(new SharedPHelper(getContext()).get("CHANGETSPVIN","0").equals("0")){
                    getHttp(vinTsp);
                }else {
                    getHttp( new SharedPHelper(getContext()).get("CHANGETSPVIN","")+"");
                }
            }

        }
    }

    private void getHttp(String vin) {
        DialogUtils.loading(getContext(), true);
        list.clear();
        HttpRxUtils.getRepairMaintain(getContext(),
                new String[]{"accessToken", "vin", "type"},
                //vin要动态获取 "LFBJDBB43WJ000118"    new SharedPHelper(getContext()).get("TSPVIN","0")
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                       vin, "repair"},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(getActivity());
                        public404.setVisibility(View.GONE);
                        MLog.e("维修加载");
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if(list.size()==0){
                           // public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(getContext(),getResources().getString(R.string.hint_un5));
                        }
                        //////cc 界面确定再增加
                        myAdapter = new KeepAdapter(R.layout.main_item_layout, list); //设置适配器
                        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                       // isFresh = false;
                        initOnclickListener();
                        initOnclickChildListener();
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(getActivity());
                        public404.setVisibility(View.VISIBLE);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(getContext());
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(getContext());
                                break;
                            default:
                                ActivityUtil.showToast(getContext(), str);
                        }
                    }
                }

        );
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //   MyToast.showToast(getContext(), "点击了" + position);
                MLog.e("ro_no维修:" + list.get(position).get("ro_no").toString());
                Intent i = new Intent(getContext(), FixEnterActivity.class);
//                try {//服务器获取数据为1314.0
//                    i.putExtra("ro_no", list.get(position).get("ro_no").toString()
//                            .substring(0, list.get(position).get("ro_no").toString().indexOf(".")));
//                } catch (Exception e) {
//                    i.putExtra("ro_no", list.get(position).get("ro_no").toString());
//                }
                i.putExtra("ro_no", list.get(position).get("ro_no").toString());
                startActivity(i);
            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                //  MyToast.showToast(getContext(), "长按点击了" + position);
                return true;
            }
        });
    }
    private void initOnclickChildListener() {
        myAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MLog.e("zi点击");
                poss=position;
                if((list.get(position).get("se_score")+"").equals("0")||list.get(position).get("se_score")==null)
                showDialog();//弹框选择评分
                }

        });
    }
    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  //设置RecyclerView的显示模式
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        // myAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);
        //设置静默加载时提前加载的item个数
//        xRefreshView1.setPreLoadCount(4);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        xRefreshView.stopRefresh();
//                    }
//                }, 500);
                //"LGWFF4A5XGF153655"
                if(new SharedPHelper(getContext()).get("CHANGETSPVIN","0").equals("0")){
                    getHttp(vinTsp);
                }else {
                    getHttp( new SharedPHelper(getContext()).get("CHANGETSPVIN","")+"");
                }
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        for (int i = 0; i < 6; i++) {
//                            recyclerviewAdapter.insert(new Person("More ", mLoadCount + "21"),
//                                    recyclerviewAdapter.getAdapterItemCount());
//                        }
                        mLoadCount++;
                        if (mLoadCount >= 3) {//模拟没有更多数据的情况
                            xRefreshView.setLoadComplete(true);
                        } else {
                            // 刷新完成必须调用此方法停止加载
                            xRefreshView.stopLoadMore(false);
                            //当数据加载失败 不需要隐藏footerview时，可以调用以下方法，传入false，不传默认为true
                            // 同时在Footerview的onStateFinish(boolean hideFooter)，可以在hideFooter为false时，显示数据加载失败的ui
//                            xRefreshView1.stopLoadMore(false);
                        }
                    }
                }, 1000);
            }
        });
    }

    private void getPing() {
        DialogUtils.loading(getContext(), true);
        listPing.clear();
        HttpRxUtils.getGetServiceEalution(getContext(),
                new String[]{"accessToken", "ro_id", "ro_no"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                        list.get(poss).get("id")+"",
                        list.get(poss).get("ro_no")+""
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(getActivity());
                        listPing.addAll((Collection<? extends String>) s);
                        showDialog();//弹框选择评分
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(getActivity());
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(getContext());
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(getContext());
                                break;
                            default:
                                ActivityUtil.showToast(getContext(), str);
                        }
                    }
                }
        );
    }

    private void showDialog() {
        dialog = new CustomDialog(getContext());
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @OnClick(R.id.refresh)
    public void onViewClicked() {
        if(new SharedPHelper(getContext()).get("CHANGETSPVIN","0").equals("0")){
            getHttp(vinTsp);
        }else {
            getHttp( new SharedPHelper(getContext()).get("CHANGETSPVIN","")+"");
        }

    }


    class CustomDialog extends BottomBaseDialog {
        private RatingBar ratingBar;
        private EditText editText;
        private TextView textView;
        private TextView nevsResetting;
        private String ra = null;

        public CustomDialog(Context context) {
            super(context);
        }

        @Override
        public View onCreateView() {
            widthScale(0.85f);
            showAnim(new Swing());

            // dismissAnim(this, new ZoomOutExit());
            View inflate = View.inflate(getContext(), R.layout.dialog_custom_base, null);
            ratingBar = (RatingBar) inflate.findViewById(R.id.rabar);
            editText = (EditText) inflate.findViewById(R.id.edcontent);
            textView = (TextView) inflate.findViewById(R.id.submit);
            nevsResetting = (TextView) inflate.findViewById(R.id.nevs_resetting);
            inflate.setBackgroundDrawable(
                    CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

            return inflate;
        }

        @Override
        public void setUiBeforShow() {
            if (listPing.size() == 2) {
                editText.setText(listPing.get(1));
                MLog.e("评分:" + listPing.get(0));
                ratingBar.setRating(Float.parseFloat(listPing.get(0)));
                textView.setVisibility(View.INVISIBLE);
                nevsResetting.setVisibility(View.INVISIBLE);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ra == null) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_scored));
                    } else if (editText.getText().toString().trim().length() <= 0) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_speaks));
                    } else if (ra == null && editText.getText().toString().trim().length() <= 0) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_scored));
                    } else {
                        se_score = ra;
                        se_evalution = editText.getText().toString().trim();
                        getSpeak();
                    }
                }
            });
            nevsResetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    ratingBar.setRating(0f);
                }
            });
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ra = Float.toString(rating);//5.0
                }
            });
        }
    }
    private void getSpeak() {
        /**
         * 	ServiceEalution对象
         参数名称	类型	是否必填	说明
         u_code	字符串	是	用户ID
         se_score	字符串	是	用户评分
         se_evalution	字符串	是	用户评价
         ro_id	字符串	是	维修单ID
         order_by	字符串	是	服务经理ID
         order_by_name	字符串	是	服务经理
         ro_no	字符串	是	维修工单号
         list.add(7, jsondata.getString("order_by_name"));
         list.add(8, jsondata.getString("order_by_phone"));
         list.add(9, jsondata.getString("id"));
         list.add(10, jsondata.getString("order_by"));
         * */
        DialogUtils.loading(getContext(), true);
        HttpRxUtils.getServiceEalution(getContext(),
                new String[]{"accessToken", "service_ealution"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                        new ServiceEalution((String) new SharedPHelper(getContext()).get(Constant.LOGINNAME, ""),
                            //    se_score, se_evalution, list.get(poss).get("id")+"", list.get(poss).get("order_by")+"", list.get(poss).get("order_by_name")+"",list.get(poss).get("ro_no")+"")
                                se_score, se_evalution,"","","",list.get(poss).get("ro_no")+"")
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(getActivity());
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_submitsuccess));
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(getActivity());
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_submitfail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(getContext());
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(getContext());
                                break;
                            default:
                                ActivityUtil.showToast(getContext(), str);
                        }
                    }
                }
        );
    }
}
