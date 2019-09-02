package com.nevs.car.adapter;

import android.text.TextUtils;

import com.nevs.car.R;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/13.
 */

public class ServiceStopAdapter extends BaseQuickAdapter<HashMap<String,Object>>  {

    public ServiceStopAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        if((item.get("distants")+"").equals("")){
            helper.setText(R.id.stop_distance,"");
        }else {
            helper.setText(R.id.stop_distance,item.get("distants")+"");
        }
        helper.setText(R.id.stop_name,item.get("title")+"")
                .setText(R.id.stop_lacation,item.get("text")+"")
                .setText(R.id.stop_phone, MyApp.getInstance().getResources().getString(R.string.newphone));
        String phone=item.get("like_phone").toString();
        if(TextUtils.isEmpty(phone)){
            helper.setEnabled(R.id.stop_phone,false);
        }else{
            helper.setEnabled(R.id.stop_phone,true);
            helper.setOnClickListener(R.id.stop_phone,new OnItemChildClickListener());
        }


    }

}
