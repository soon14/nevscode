package com.nevs.car.adapter;

import com.nevs.car.model.MainDateDto;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 */
public class MainAdapter extends BaseQuickAdapter<MainDateDto> {
    public MainAdapter(int layoutResId, List<MainDateDto> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, MainDateDto item) {
//        helper.setText(R.id.tv_title,item.getTitle()+"")
//                .setText(R.id.tv_content,item.getInfo()+"");
//        //通过Glide显示图片
//        Glide.with(mContext)
//               // .load(item.getImageUrl())
//                .load("https://www.baidu.com/img/bdlogo.png")
//                .crossFade()
//                .placeholder(R.mipmap.def_head)//图片加载失败0时显示的图片
//                .transform(new GlideCircleTransform(mContext))
//                .into((ImageView) helper.getView(R.id.iv_url));
    }
}
