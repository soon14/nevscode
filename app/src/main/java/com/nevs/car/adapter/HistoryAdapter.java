package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.List;

/**
 * Created by mac on 2018/6/20.
 */

public class HistoryAdapter extends BaseQuickAdapter<String>{
    public HistoryAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.history,item);
    }
}
