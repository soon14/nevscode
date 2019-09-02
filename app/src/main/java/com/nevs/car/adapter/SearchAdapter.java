package com.nevs.car.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.model.AddressBean;

import java.util.List;

/**
 * Created by mac on 2018/6/15.
 */

public class SearchAdapter extends BaseAdapter {
    private List<AddressBean> data;
    private LayoutInflater li;
    public SearchAdapter(Context context,List<AddressBean> data){
        this.li = LayoutInflater.from(context);
        this.data=data;
    }

//    /**
//     * 设置数据集
//     * @param data
//     */
//    public void setData(List<AddressBean> data){
//        this.data = data;
//    }

    @Override
    public int getCount() {
        return (data==null)? 0: data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = li.inflate(R.layout.address_item, null);
            vh.title = (TextView) convertView.findViewById(R.id.item_title);
            vh.text = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.title.setText(data.get(position).getTitle());
        vh.text.setText(data.get(position).getText());
        return convertView;
    }

    private class ViewHolder{
        public TextView title;
        public TextView text;
    }
}