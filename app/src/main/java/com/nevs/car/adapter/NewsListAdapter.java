package com.nevs.car.adapter;

import com.nevs.car.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/4/25.
 */

public class NewsListAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public NewsListAdapter(int layoutResId, List<HashMap<String,Object>> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, HashMap<String,Object> item) {
        helper.setText(R.id.title_car,item.get("title")+"")
                .setText(R.id.time_car,item.get("releaseDate")+"")
              .setBackgroundRes(R.id.image_car,R.mipmap.fw_zx_img);
//        //通过Glide显示图片
//        Glide.with(mContext)
//                // .load(item.getImageUrl())
//                .load("https://www.baidu.com/img/bdlogo.png")
//                .crossFade()
//                .into((ImageView) helper.getView(R.id.image_car));
//        // .transform(new GlideCircleTransform(mContext))     .placeholder(R.mipmap.def_head)//图片加载失败0时显示的图片
    }
}
