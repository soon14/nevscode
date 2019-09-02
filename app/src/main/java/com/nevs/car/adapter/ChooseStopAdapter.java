package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/21.
 */

public class ChooseStopAdapter extends BaseQuickAdapter<HashMap<String, Object>> {
    public ChooseStopAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.stop_name,item.get("name")+"")
                .setText(R.id.stop_address,item.get("address")+"");
                if((item.get("dis")+"").equals("0")){
                    helper.setText(R.id.stop_dis,"");

                }else {
                    helper.setText(R.id.stop_dis,item.get("dis")+"");

                }

        switch (item.get("type")+""){
            case "30031001":
                helper.setText(R.id.stop_type,"销售展厅");
                break;
            case "30031002":
                helper.setText(R.id.stop_type,"售后服务中心");
                break;
            case "30031003":
                helper.setText(R.id.stop_type,"一体店(2S)");
                break;
            case "30031004":
                helper.setText(R.id.stop_type,"一体店(4S)");
                break;
            case "30031005":
                helper.setText(R.id.stop_type,"移动服务站");
                break;
            case "30031006":
                helper.setText(R.id.stop_type,"线上服务站");
                break;

        }


    }
}
