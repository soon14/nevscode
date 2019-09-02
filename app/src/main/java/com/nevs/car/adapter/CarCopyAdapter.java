package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/5/30.
 */

public class CarCopyAdapter extends BaseQuickAdapter<HashMap<String,Object>> {
    public CarCopyAdapter(int layoutResId, List<HashMap<String,Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
//        helper.setText(R.id.text_title1,"自定义行程1")
//                .setText(R.id.text_title2,"自定义行程2")
//                .setText(R.id.text_type1,"公务")
//                .setText(R.id.text_type2,"私人")
//                .setText(R.id.content_one1,"02/25 11:45 东丽区XX街道XX社区")
//                .setText(R.id.content_one2,"02/25 11:45 东丽区XX街道XX社区")
//                .setText(R.id.content_two1,"02/25 11:45 东丽区XX街道XX社区")
//                .setText(R.id.content_two2,"02/25 11:45 东丽区XX街道XX社区")
//                .setText(R.id.content_three1,"12KM")
//                .setText(R.id.content_three2,"12KM")
//                .setText(R.id.content_four1,"40min")
//                .setText(R.id.content_four2,"40min")
//                .setText(R.id.content_five1,"55km/h")
//                .setText(R.id.content_five2,"55km/h");


        String totalMileage=item.get("totalMileage")+"";
        String  temp1[]=null;
          temp1=totalMileage.split("\\.");

        String totalDuration=item.get("totalDuration")+"";
        String  temp2[]=null;
        temp2=totalDuration.split("\\.");

        MLog.e(temp1[0]+"temp1[0]");
        MLog.e(temp2[0]+"temp2[0]");

        String totalBattery=item.get("totalBattery")+"" ;
        String  temp3[]=null;
        temp3=totalBattery.split("\\.");
//        String  temp3[]=null;
//        try {
//            sss=String.valueOf((Long.parseLong(temp1[0])/Long.parseLong(temp2[0]))*60);
//        }catch (Exception e){
//            MLog.e("行程记录列表异常CarCopyAdapter");
//        }
//
//        try {
//            temp3=sss.split("\\.");
//        }catch (Exception e){
//            temp3=new String[]{sss};
//        }





                if(item.get("title")==null||item.get("title").equals("null")){
                    helper.setText(R.id.text_title1,"");
                }else {
                    helper.setText(R.id.text_title1,item.get("title")+"");
                }
                if(item.get("category")==null||item.get("category").equals("null")){
                    helper.setText(R.id.text_type1,"");
                }else
                {
                    helper.setText(R.id.text_type1,item.get("category")+"");
                }

                helper.setText(R.id.content_one1, HashmapTojson.getTimez(item.get("beginTime")+"","yyyy-MM-dd HH:mm")+" "+item.get("beginLongitude")+","+item.get("beginLatitude"))
                .setText(R.id.content_two1,HashmapTojson.getTimez(item.get("endTime")+"","yyyy-MM-dd HH:mm")+" "+item.get("endLongitude")+","+item.get("endLatitude"))
                .setText(R.id.content_three1,temp1[0]+"km")
                .setText(R.id.content_four1,temp2[0]+"min");
        helper.setText(R.id.content_five1,temp3[0]+"kW/h");

    }
}
