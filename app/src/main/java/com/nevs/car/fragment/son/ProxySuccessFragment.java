package com.nevs.car.fragment.son;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nevs.car.R;
import com.nevs.car.activity.service.ProxyLookActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.ProxyNewAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ProxyallBean;
import com.nevs.car.tools.Base.BaseFragment;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mac on 2018/6/8.
 */

public class ProxySuccessFragment extends BaseFragment {
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

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_proxy_success;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initRecyclyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible()) {
            if (isFresh) {
                getHttp();
                ;//获取数据
            }

        }
    }

    private void getHttp() {
        list.clear();
        DialogUtils.loading(getContext(), true);
        HttpRxUtils.getPaProyQuery(getContext(),
                new String[]{"accessToken", "status", "create_by"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, ""),
                        "30251004",new SharedPHelper(getContext()).get(Constant.LOGINNAME,"")},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        xRefreshView.stopRefresh();
                        public404.setVisibility(View.GONE);
                        DialogUtils.hidding(getActivity());
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if(list.size()==0){
                          //  public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(getContext(),getContext().getResources().getString(R.string.toast_null));
                        }
                        //////cc 界面确定再增加
                        myAdapter = new ProxyNewAdapter(R.layout.item_proxy, list); //设置适配器
                        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                        isFresh = false;
                        initOnclickListener();
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
                Intent i = new Intent(getContext(), ProxyLookActivity.class);
                List<ProxyallBean> listProxy=new ArrayList<ProxyallBean>();
                listProxy.add(new ProxyallBean(3,
                        list.get(position).get("name").toString(),
                        list.get(position).get("phone").toString(),
                        list.get(position).get("sex").toString(),
                        list.get(position).get("province_name").toString(),
                        list.get(position).get("city_name").toString(),
                        list.get(position).get("vmodel").toString(),
                        list.get(position).get("address").toString(),
                        list.get(position).get("purpose").toString(),
                        list.get(position).get("budget").toString()
                ));
                i.putExtra("proxyall", (Serializable) listProxy);
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
                getHttp();
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

    @OnClick(R.id.refresh)
    public void onViewClicked() {
        getHttp();
    }
}
