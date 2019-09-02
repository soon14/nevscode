package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/26.
 */

public class FavoriteAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public FavoriteAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
        helper.setText(R.id.poi_title,item.get("poiName")+"");//"poiName"
       // helper.setOnClickListener(R.id.poi_delete,new OnItemChildClickListener());
        if((item.get("isCheck")+"").equals("0")){
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.my_rzsz_dot);
        }else {
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.my_rzsz_dots);
        }
        helper.setOnClickListener(R.id.rel_choose,new OnItemChildClickListener());
    }
}
