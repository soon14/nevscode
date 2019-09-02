package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/8/27.
 */

public class DataSearchAdapter extends BaseQuickAdapter<HashMap<String, Object>> {
    public DataSearchAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        if((item.get("possion")+"").equals("0")){
            helper.setVisible(R.id.text_enter,false);
            helper.setVisible(R.id.text_title_enter,true);
            helper.setText(R.id.text_time,item.get("time")+"")
                    .setText(R.id.text_center,item.get("data")+"");
        }else {
            helper.setVisible(R.id.text_enter,true);
            helper.setVisible(R.id.text_title_enter,false);
            helper.setText(R.id.text_time,(item.get("time")+"").split("-")[0]+ MyApp.getInstance().getResources().getString(R.string.searchyearpoint)+(item.get("time")+"").split("-")[1]+MyApp.getInstance().getResources().getString(R.string.searchmouthpoint))
                    .setText(R.id.text_center,item.get("data")+"M");
        }

        helper.setOnClickListener(R.id.text_enter,new OnItemChildClickListener());
    }
}
