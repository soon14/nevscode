package com.nevs.car.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class LVAdaper<T>extends BaseAdapter {

    private List<T> data;
    private LayoutInflater mInflater;
    public LVAdaper(Context context, List<T> data){
        this.mInflater = LayoutInflater.from(context);
        this.data=data;
    }

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

    public void notifyData(List<T> mlist){
        data.clear();
        notifyDataSetChanged();
        if(null!= mlist){
            data.addAll(mlist);
            notifyDataSetChanged();
        }
    }

}
