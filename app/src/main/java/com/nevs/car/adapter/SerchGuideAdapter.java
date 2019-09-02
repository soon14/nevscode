package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/14.
 */

public class SerchGuideAdapter extends BaseQuickAdapter<HashMap<String,Object>>  {
    public SerchGuideAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.stop_name,item.get("title")+"")
                .setText(R.id.stop_lacation,item.get("text")+"")
                .setText(R.id.stop_distance,item.get("distants")+"");
    }
}
