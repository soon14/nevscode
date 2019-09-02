package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/8/27.
 */

public class DataSearchEnterAdapter extends BaseQuickAdapter<HashMap<String, Object>> {
    public DataSearchEnterAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        try {
            if((item.get("possion")+"").equals("0")){
                helper.setText(R.id.time,item.get("time")+"")
                        .setText(R.id.datas,item.get("data")+"");
            }else {
                String aa=(item.get("time")+"").substring(0,10);
                helper.setText(R.id.time,aa.split("-")[1]+ "-"+aa.split("-")[2])
                        .setText(R.id.datas,item.get("data")+"M");
            }
        }catch (Exception e){

        }

    }
}
