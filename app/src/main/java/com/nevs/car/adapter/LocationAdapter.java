package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/4/27.
 */

public class LocationAdapter extends BaseQuickAdapter<HashMap<String,Object>> {

        public LocationAdapter(List<HashMap<String,Object>> data) {
            super(R.layout.item_list_region, data);
        }

        @Override
        protected void convert(BaseViewHolder holder,HashMap<String,Object> hashMap) {
            holder.setText(R.id.name, hashMap.get("name")+"");
        }
    }
