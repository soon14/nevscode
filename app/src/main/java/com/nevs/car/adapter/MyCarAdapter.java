package com.nevs.car.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nevs.car.R;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/4/18.
 */

public class MyCarAdapter extends BaseQuickAdapter<HashMap<String, Object>> {
    public MyCarAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    /**
     *
     *{
     "isSuccess": "Y",
     "reason": "",
     "data": [{
     "bindingId": "7eb79083fff947748ceb13c405c8be28",
     "isAuthenticated": "False",
     "vin": "LV3SB1411K1000208",
     "iccid": "89860318342003491150",
     "imsi": "460111015938637",
     "msisdn": "14928270146",
     "bleAddress": "6a:6b:6c:6d:6e:6f",
     "relationType": "车主",
     "startTime": "1560145349",
     "endTime": "1560145349",
     "permissions": null
     }, {
     "bindingId": "3741215eaf4f4ca9935d408e2fe183f7",
     "isAuthenticated": "False",
     "vin": "LV3SB1415K1000101",
     "iccid": "89860318342003491564",
     "imsi": "460111015941442",
     "msisdn": "14928270187",
     "bleAddress": "01:a5:a5:a5:a5:a5",
     "relationType": "车主",
     "startTime": "1560145349",
     "endTime": "1560145349",
     "permissions": null
     }, {
     "bindingId": "e9281f3e4bdd4edd8db01fecc15a7d30",
     "isAuthenticated": "False",
     "vin": "LV3SB1414K1000106",
     "iccid": "89860318342003491614",
     "imsi": "460111015942711",
     "msisdn": "14928270192",
     "bleAddress": "01:a5:a5:a5:a5:a5",
     "relationType": "车主",
     "startTime": "1560145349",
     "endTime": "1560145349",
     "permissions": null
     }, {
     "bindingId": "b9c4566efdda48b5a729f315a446ebaa",
     "isAuthenticated": "False",
     "vin": "LV3SB1410K1000104",
     "iccid": "89860318342003490913",
     "imsi": "460111015938613",
     "msisdn": "14928270122",
     "bleAddress": "01:a5:a5:a5:a5:a5",
     "relationType": "授权",
     "startTime": "1558083600",
     "endTime": "1715936400",
     "permissions": ["1", "2", "3", "4", "5", "6"]
     }, {
     "bindingId": "c24b06ceffa74a57a04388409395d61f",
     "isAuthenticated": "True",
     "vin": "LTPSB1413J1000041",
     "iccid": "89860318342003203118",
     "imsi": "460111015940689",
     "msisdn": "14928270063",
     "bleAddress": "01:a5:a5:a5:a5:a5",
     "relationType": "授权",
     "startTime": "1554944400",
     "endTime": "1586566800",
     "permissions": ["1", "2", "3", "4", "5", "6"],
     "carType": "Yes",
     "digitalKey": "",
     "color": "F",
     "groupName": "9-3-滴滴订制",
     "groupEnName": "NISSAN",
     "isDefault": "No",
     "groupCode": "X9-3",
     "licDate": "",
     "licTelecontrol": "",
     "licDoorcontrol": "",
     "licSearchcar": "",
     "licAccontrol": "",
     "nickName": "掉色掉",
     "licenseNo": "京AD88889",
     "invoiceDate": "2019/3/19 0:00:00",
     "custMobile": "13317108921"
     }]
     }
     */
    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        try {
            if(item.get("licenseNo")==null){
                helper.setText(R.id.car_number,(item.get("vin")+"").substring((item.get("vin")+"").length()-6));
            }else {
                helper.setText(R.id.car_number,item.get("licenseNo")+"");
            }
        }catch (Exception e){

        }

        if(item.get("groupName")==null){
            helper.setText(R.id.name,"");
        }else {
            helper.setText(R.id.name,item.get("groupName")+"");
        }

        boolean lang= MyUtils.getLanguage(MyApp.getInstance());
        try {
            if(item.get("isAuthenticated").equals("False")){
                if(lang){
                    helper.setText(R.id.state,"未认证");
                }else {
                    helper.setText(R.id.state, "Uncertified");
                }

            }else {
                if(lang){
                    helper.setText(R.id.state,"已认证");
                }else {
                    helper.setText(R.id.state,"Authentication");
                }

            }
        }catch (Exception e){
            MLog.e("没有carType字段");
            if(lang){
                helper.setText(R.id.state,"未认证");
            }else {
                helper.setText(R.id.state, "Uncertified");
            }
        }

        if(item.get("relationType").equals("车主")){
            if(lang){
                helper.setText(R.id.type,"车辆授权");
            }else {
                helper.setText(R.id.type,"Vehicle Authority");
            }

            helper.setBackgroundRes(R.id.rel_type,R.drawable.color_maintop);
        }else {
            if(lang){
                helper.setText(R.id.type,"被授权");
            }else {
                helper.setText(R.id.type,"Is Authorized");
            }

            helper.setBackgroundRes(R.id.rel_type,R.color.line_mys);
        }
        //通过Glide显示图片
        Glide.with(mContext)
                // .load(item.getImageUrl())
                .load("https://www.baidu.com/img/bdlog0909.png")
                .crossFade()
                .placeholder(R.mipmap.my_xzcl_car)//图片加载失败0时显示的图片
                .into((ImageView) helper.getView(R.id.imagecar));
        helper.setOnClickListener(R.id.state,new OnItemChildClickListener())
                .setOnClickListener(R.id.rel_type,new OnItemChildClickListener());
    }
}
