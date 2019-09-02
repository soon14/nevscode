package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/26.
 */

public class FixEnterAdapter extends BaseQuickAdapter<HashMap<String,Object>>{
    public FixEnterAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.enter_name,item.get("name")+"")
                .setText(R.id.enter_money,item.get("amount")+ MyApp.getInstance().getResources().getString(R.string.enter_money));
    }
}
