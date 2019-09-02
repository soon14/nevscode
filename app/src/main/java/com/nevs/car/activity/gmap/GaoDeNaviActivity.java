package com.nevs.car.activity.gmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.nevs.car.R;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.MLog;

public class GaoDeNaviActivity extends BaseGaoDeActivity {

    // 继承的是baseactivity
    private double sj;
    private double sw;
    private double ej;
    private double ew;
    private String ssj;
    private String ssw;
    private String sej;
    private String sew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MLog.e("进入导航界面");
        Intent intent = getIntent();
        /* 11/17 记 再次重复低级错误 此处intent接收了未传输的数据导致崩溃 */
        /* 经纬度 纬度在前 */
        try {
            ssj = intent.getStringExtra("qlon");
            sj = Double.parseDouble(ssj);
            ssw = intent.getStringExtra("qlat");
            sw = Double.parseDouble(ssw);
            sej = intent.getStringExtra("zlon");
            ej = Double.parseDouble(sej);
            sew = intent.getStringExtra("zlat");
            ew = Double.parseDouble(sew);
            MLog.e("获取数据完毕");
        }catch (Exception e){
            ActivityUtil.showToast(this,getResources().getString(R.string.toast_calculatefail));
            finish();
        }

        setContentView(R.layout.activity_gao_de_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion,
         * avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *                说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，
         *                如果为true则此策略会算出多条路线。 注意: 不走高速与高速优先不能同时为true
         *                高速优先与避免收费不能同时为true
         */
        // 重置起点
        this.sList.clear();
        this.mStartLatlng = new NaviLatLng(sw, sj);
        this.sList.add(mStartLatlng);
        // 重置了终点
        this.eList.clear();
        this.mEndLatlng = new NaviLatLng(ew, ej);
        this.eList.add(mEndLatlng);

        int strategy = 0;
        try {
            // 再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false,
                    false);
        } catch (Exception e) {
            e.printStackTrace();
          //  MLog.e("步行距离大于100km，不能导航");
        }
     //   mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);//驾车

//        mAMapNavi.calculateRideRoute(new  NaviLatLng(sw, sj), new NaviLatLng(ew, ej));//骑行

        mAMapNavi.calculateWalkRoute(new NaviLatLng(sw,sj), new NaviLatLng(ew, ej));//步行
    }


//    @Override
//    public void onCalculateRouteSuccess() {
//        super.onCalculateRouteSuccess();
//        mAMapNavi.startNavi(NaviType.GPS);
//    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.recovery_dialog_tips))
                .setMessage(getResources().getString(R.string.toast_exitguide))
                .setPositiveButton(getResources().getString(R.string.for_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();


        return super.onKeyDown(keyCode, event);
    }

}
