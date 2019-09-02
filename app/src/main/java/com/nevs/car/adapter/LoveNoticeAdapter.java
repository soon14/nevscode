package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/14.
 */

public class LoveNoticeAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public LoveNoticeAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
      //  long l= Long.parseLong(item.get("pushTime")+"");
        Object l=item.get("pushTime");
        MLog.e("时间戳："+item.get("pushTime")+"");
        MLog.e("时间戳l："+l);

        String s =String.valueOf(item.get("pushTime"));
        String a[] = s.split("E");
        long i= (long) (Double.parseDouble(a[0])*1000000000);
        MLog.e("时间戳转化后l："+i);
        String timez=HashmapTojson.getDateToString(i*1000,"yyyy/MM/dd HH:mm:ss");


        String description=item.get("description")+"";
        String title="";
        try {
            JSONObject jsonObject=new JSONObject(description);
            title=jsonObject.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

      //712  helper.setText(R.id.title,item.get("title")+"")
        helper.setText(R.id.title,title)
       // .setText(R.id.time, HashmapTojson.getDateToString((Long) item.get("pushTime"),"yyyy-MM-dd HH:mm:ss"));
                .setText(R.id.time,timez);
        if(Boolean.parseBoolean(item.get("isRead")+"")==true){
            helper.setVisible(R.id.is_read,true);
            helper.setBackgroundRes(R.id.is_read,R.mipmap.ydxx);
        }else {
            helper.setVisible(R.id.is_read,true);
            helper.setBackgroundRes(R.id.is_read,R.mipmap.wdxx);
        }


        if(item.get("isCheck").equals("0")){
            helper.setVisible(R.id.image_choose,true);
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.my_rzsz_dot);
        }else if(item.get("isCheck").equals("1")) {
            helper.setVisible(R.id.image_choose,true);
            helper.setBackgroundRes(R.id.image_choose,R.mipmap.cltj_type_true);
        }else if(item.get("isCheck").equals("2")){
            helper.setVisible(R.id.image_choose,false);
        }
    }
}
