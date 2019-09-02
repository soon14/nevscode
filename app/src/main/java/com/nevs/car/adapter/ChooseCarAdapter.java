package com.nevs.car.adapter;

import com.nevs.car.R;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MyApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/6/5.
 */

public class ChooseCarAdapter extends BaseQuickAdapter<HashMap<String, Object>> {

    private List<HashMap<String,Object>> list=new ArrayList<>();
    public ChooseCarAdapter(int layoutResId, List<HashMap<String, Object>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, Object> item) {
//        helper.setText(R.id.type,item.getContent()+"");
//        if(item.getContent().toString().equals("我的车辆")){
//            helper.setVisible(R.id.btn_choose,false);
//            helper.setVisible(R.id.image_choose,true);
//        }

        //LOGINJSON
        MyUtils.xJson(new SharedPHelper(MyApp.getInstance()).get("LOGINJSONSSCAR","")+"",list);
        helper.setText(R.id.types,item.get("relationType")+"");
        boolean lang= MyUtils.getLanguage(MyApp.getInstance());
        try {
            if((item.get("relationType")+"").equals("车主")) {
                if(lang){
                    helper.setText(R.id.types,"车主");
                }else {
                    helper.setText(R.id.types,"Owner");
                }

            }else {
                if(lang){
                    helper.setText(R.id.types,"授权");
                }else {
                    helper.setText(R.id.types,"Authorization");
                }
            }
        }catch (Exception e){


        }


//                if(item.get("relationType").toString().equals("车主")){
//                    helper.setText(R.id.type,"我的车辆");
//           helper.setVisible(R.id.btn_choose,true);
//            helper.setVisible(R.id.image_choose,false);
//                    if(new SharedPHelper(mContext).get("TSPVIN","0").toString().equals(item.get("vin").toString())){
//                        helper.setVisible(R.id.btn_choose,false);
//                        helper.setVisible(R.id.image_choose,true);
//                    }
//        }


//        for(int i=0;i<list.size();i++){
//
//            if(item.get("vin").equals(list.get(i).get("vin"))){
//                if(list.get(i).get("licenseNo")==null){
//                    helper.setText(R.id.type,"");
//                }else {
//                    helper.setText(R.id.type,list.get(i).get("licenseNo")+"");
//                }
//
//            }
//        }



        try {
            if(item.get("licenseNo")==null){
                helper.setText(R.id.type,(item.get("vin")+"").substring((item.get("vin")+"").length()-6));
            }else {
                helper.setText(R.id.type,item.get("licenseNo")+"");
            }
        }catch (Exception e){

        }


        helper.setVisible(R.id.btn_choose,true);
        helper.setVisible(R.id.image_choose,false);
        if(new SharedPHelper(mContext).get("TSPVIN","0").toString().equals(item.get("vin")+"")){
            helper.setVisible(R.id.btn_choose,false);
            helper.setVisible(R.id.image_choose,true);
        }

    }
}
