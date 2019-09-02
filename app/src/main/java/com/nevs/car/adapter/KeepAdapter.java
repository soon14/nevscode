package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/5/2.
 */
//{"isSuccess":"Y","reason":"","data":[{"KeyID":null,"ro_no":1312,"org_name":0,"org_code":"OEM","vin":"LFBJDBB43WJ000118","license_no":"浙A12133","deliver":"好的7987890708967565431ryti","order_date":"2018/3/6 11:21:00","settle_amount":""},{"isSuccess":"Y","reason":"","data":[{"KeyID":null,"ro_no":1312,"org_name":0,"org_code":"OEM","vin":"LFBJDBB43WJ000118","license_no":"浙A12133","deliver":"好的7987890708967565431ryti","order_date":"2018/3/6 11:21:00","settle_amount":""},
public class KeepAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public KeepAdapter(int layoutResId, List<HashMap<String,Object>> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, HashMap<String,Object> item) {
        helper.setText(R.id.cityname,item.get("org_name")+"")
                .setText(R.id.money,MyApp.getInstance().getResources().getString(R.string.fixmoneys)+item.get("c_total_amount")+MyApp.getInstance().getResources().getString(R.string.enter_money))
                .setText(R.id.name,MyApp.getInstance().getResources().getString(R.string.sendman)+item.get("deliver"))
                .setText(R.id.time, MyApp.getInstance().getResources().getString(R.string.toast_sendime)+item.get("order_date"));
        helper.setOnClickListener(R.id.rel_type,new OnItemChildClickListener());

        if((item.get("se_score")+"").equals("0")||item.get("se_score")==null){
            helper.setText(R.id.mark,MyApp.getInstance().getResources().getString(R.string.mark));
        }else {
            helper.setText(R.id.mark,item.get("se_score")+MyApp.getInstance().getResources().getString(R.string.nevs_minutes));
        }
    }

}