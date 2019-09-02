package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/16.
 */

public class CarAuthorizationAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public CarAuthorizationAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.name,item.get("nickName")+"");
    }
}
