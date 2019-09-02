package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/17.
 */

public class ChargeMainAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public ChargeMainAdapter(int layoutResId, List<HashMap<String,Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String,Object> item) {
        String diss = String.valueOf(Double.parseDouble(item.get("distance")+"") / 1000);
        MLog.e("距离：km" + diss);
        DecimalFormat myformat = new DecimalFormat("0.00");
        String str = myformat.format(Double.parseDouble(diss)) + "km";
        helper.setText(R.id.stop_name,item.get("stationName")+"")
                .setText(R.id.stop_distance,str);
        try {
            helper.setText(R.id.stop_lacation, MyUtils.getDisOnePoint((item.get("electricityFee")+"").split(":")[4]));
        }catch (Exception e){
            helper.setText(R.id.stop_lacation,"");
        }
        helper.setOnClickListener(R.id.stop_guide,new OnItemChildClickListener());
    }
}
