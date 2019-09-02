package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/8.
 */

public class ProxyNewAdapter extends BaseQuickAdapter<HashMap<String,Object>>{

    public ProxyNewAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.name,item.get("name")+"")
                .setText(R.id.phonenumber,item.get("phone")+"")
                .setText(R.id.think,item.get("vmodel")+"");
    }
}
