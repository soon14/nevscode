package com.nevs.car.adapter;

import android.content.Context;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/13.
 */

public class CarExamineAdapter extends BaseQuickAdapter<HashMap<String,Object>>  {
    private Context context= MyApp.getInstance();
    public CarExamineAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.name,item.get("group_name")+"");
//        if((item.get("factory_price")+"").length()==0){
//            helper.setText(R.id.money,"￥"+context.getResources().getString(R.string.toast_19wan));
//        }else {
//            helper.setText(R.id.money,"￥"+item.get("factory_price"));
//        }
        helper.setText(R.id.money,"￥"+item.get("sales_price")+context.getResources().getString(R.string.toast_19wan));
    }
}
