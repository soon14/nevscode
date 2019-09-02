package com.nevs.car.adapter;

import android.content.Context;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/1.
 */

public class PublicNoticeAdapter extends BaseQuickAdapter<HashMap<String,Object>>{
    private Context context= MyApp.getInstance();
    public PublicNoticeAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    /**
     * {"U_CODE":"13476059095","title":"预约提醒！","content":"尊敬的车主您好！您预约的维修服务，时间：2018-10-01。武汉汉阳新能源有限公司，欢迎您准时进厂维修！","pub_time":"2018/7/9 17:24:25","type":"90111004","is_read":"NO","is_delete":"NO","un_id":"","n_id":"189"}
     * 	type
     //个人代理退出申请结果通知	90111006
     //          个人代理申请结果通知	90111005
     //                       预约提醒	90111004
     //                       公告通知	90111003
     //                       年检提醒	90111002
     //                       通知消息	90111001
     * */
    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.title,item.get("title")+"")
                .setText(R.id.time,item.get("pub_time")+"");
        if((item.get("is_read")+"").equals("YES")) {
            helper.setBackgroundRes(R.id.is_read,R.mipmap.ydxx);
        }else {
            helper.setBackgroundRes(R.id.is_read,R.mipmap.wdxx);
        }


        if((item.get("isCheck")+"").equals("0")){
            helper.setVisible(R.id.image_choose,true);
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.my_rzsz_dot);
        }else if((item.get("isCheck")+"").equals("1")) {
            helper.setVisible(R.id.image_choose,true);
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.cltj_type_true);
        }else if((item.get("isCheck")+"").equals("2")){
            helper.setVisible(R.id.image_choose,false);
        }


               // .setText(R.id.publicer,item.get("publicer").toString());
//        switch (item.get("type").toString()){
//            case "90061001":
//            helper.setText(R.id.type,context.getResources().getString(R.string.nevs_carowner));
//                break;
//            case "90061002":
//                helper.setText(R.id.type,context.getResources().getString(R.string.nevs_uncarowner));
//                break;
//            case "90061003":
//                helper.setText(R.id.type,context.getResources().getString(R.string.servce_one));
//                break;
//            default:
//                helper.setText(R.id.type,context.getResources().getString(R.string.nevs_unknown));
//
//        }
    }
}
