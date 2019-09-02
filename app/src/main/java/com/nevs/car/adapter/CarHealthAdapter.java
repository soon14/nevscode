package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/8/4.
 */

public class CarHealthAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public CarHealthAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String,Object> item) {
        helper.setText(R.id.number_ones,item.get("number")+"")
                .setText(R.id.names,item.get("name")+"");

        if((item.get("name")+"").contains("正常")){
            helper.setBackgroundRes(R.id.image_right,R.mipmap.cltj_type_true);
        }else {
            helper.setBackgroundRes(R.id.image_right,R.mipmap.cltj_type_false);
        }

    }

}
