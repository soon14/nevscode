package com.nevs.car.adapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/8/5.
 */

public class ChargeEnterAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public ChargeEnterAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {


      //  helper.setOnClickListener(R.id.rel_child,new OnItemChildClickListener());

        //helper.setText(R.id.item_one,item.get("connectors").getClass().)
    }
}
