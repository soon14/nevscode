package com.nevs.car.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.model.JsonRootBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2018/8/6.
 *
 Android中RecyclerView点击item展开列表详细内容
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private Context context;
    /**
     * 消息列表数据
     */
    private List<JsonRootBean.Items> lists;

    /**
     * 标记展开的item
     */
    private int opened = -1;

    public MsgAdapter(Context context) {
        this.context = context;
        lists = new ArrayList<>();
    }

    /**
     * 设置列表数据
     * @param lists
     */
    public void setLists(List<JsonRootBean.Items> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_changeenter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindView(position,lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView up;
        private LinearLayout visiOnes;
        private RelativeLayout relchild;
        private TextView itemOne;
        private TextView itemTwo;
        private TextView itemThree;
        private TextView itemFour;
        private TextView itemFive;
        private TextView itemSix;
        private TextView itemSeven;
        private TextView itemEight;
        private TextView itemName;

        public ViewHolder(View itemView) {
            super(itemView);
            up= (ImageView) itemView.findViewById(R.id.up);
            visiOnes= (LinearLayout) itemView.findViewById(R.id.lin_visis);
            relchild= (RelativeLayout) itemView.findViewById(R.id.rel_child);
            itemOne= (TextView) itemView.findViewById(R.id.item_one);
            itemTwo= (TextView) itemView.findViewById(R.id.item_two);
            itemThree= (TextView) itemView.findViewById(R.id.item_three);
            itemFour= (TextView) itemView.findViewById(R.id.item_four);
            itemFive= (TextView) itemView.findViewById(R.id.item_five);
            itemSix= (TextView) itemView.findViewById(R.id.item_six);
            itemSeven= (TextView) itemView.findViewById(R.id.item_seven);
            itemEight= (TextView) itemView.findViewById(R.id.item_eight);
            itemName= (TextView) itemView.findViewById(R.id.item_name);
            relchild.setOnClickListener(this);
        }

        /**
         * 此方法实现列表数据的绑定和item的展开/关闭
         */
        void bindView(int pos, JsonRootBean.Items bean) {
//            msgTime.setText(bean.created);
//            msgContent.setText(bean.content);
//            msgContentMore.setText(bean.contentMore);
            itemName.setText(bean.getConnectors().get(0).getConnectorName()+"");
            itemOne.setText(bean.getConnectors().get(0).getConnectorID()+"");
            itemTwo.setText(bean.getConnectors().get(0).getParkNo()+"");
            itemThree.setText(bean.getConnectors().get(0).getParkStatus()+"");
            itemFour.setText(bean.getConnectors().get(0).getConnectorType()+"");
            itemFive.setText(bean.getConnectors().get(0).getVoltageLowerLimits()+"-"+bean.getConnectors().get(0).getVoltageUpperLimits()+"V");
            itemSix.setText(bean.getConnectors().get(0).getCurrent()+"A");
            itemSeven.setText(bean.getConnectors().get(0).getPower()+"KW");


            if((bean.getConnectors().get(0).getNationalStandard()+"").equals("1")){
                itemEight.setText("2011");
            }else if ((bean.getConnectors().get(0).getNationalStandard()+"").equals("2")){
                itemEight.setText("2015");
            }
            if (pos == opened){
                visiOnes.setVisibility(View.VISIBLE);
                up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
            } else{
                visiOnes.setVisibility(View.GONE);
                up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
            }

        }
        /**
         * item的点击事件
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (opened == getAdapterPosition()) {
                //当点击的item已经被展开了, 就关闭.
                opened = -1;
                notifyItemChanged(getAdapterPosition());
            } else {
                int oldOpened = opened;
                opened = getAdapterPosition();
                notifyItemChanged(oldOpened);
                notifyItemChanged(opened);
            }
        }
    }


    /**
     * Activity中使用:
     使用和 RecyclerView 平常的使用一样, 这里贴上设置 RecyclerView 在发生变化的时候的动画设置

     rlv.getItemAnimator().setChangeDuration(300);
     rlv.getItemAnimator().setMoveDuration(300);
     * */
}