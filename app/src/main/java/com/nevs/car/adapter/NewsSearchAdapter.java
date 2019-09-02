package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/6.
 */

public class NewsSearchAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public NewsSearchAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.title,item.get("title")+"")
                .setText(R.id.text,item.get("releaseDate")+"");
    }
}
