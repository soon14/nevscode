package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/5/2.
 */

public class MyOrderAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public MyOrderAdapter(int layoutResId, List<HashMap<String,Object>> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, HashMap<String,Object> item) {
        helper.setText(R.id.citynames,item.get("dealer_name")+"")
                .setText(R.id.times,item.get("booking_time")+"")
                .setText(R.id.carnames,item.get("license_no")+"")
                .setText(R.id.type_servers,"服务类型："+item.get("booking_type_name"));

    }
}