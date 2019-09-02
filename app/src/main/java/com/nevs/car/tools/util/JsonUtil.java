package com.nevs.car.tools.util;

import android.content.Context;

import com.google.gson.Gson;
import com.nevs.car.model.ProCityBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtil {
    public static String getJson(String fileName, Context context) {
        StringBuilder newstringBuilder = new StringBuilder();
        InputStream inputStream = null;
        String dd="";
        try {
            inputStream = context.getResources().getAssets().open(fileName);//"news.json""
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                newstringBuilder.append(jsonLine);
            }
            dd=newstringBuilder.toString();
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dd;
    }

    public static String  getAdCode(Context context,String cityName){
        String adcode=cityName;
        try {
            ProCityBean proCityBean = new Gson().fromJson(getJson("adcode.json",context), ProCityBean.class);
            for(int i=0;i<proCityBean.getList().size();i++){
                for(int j=0;j<proCityBean.getList().get(i).getCity().size();j++){
                    if(cityName.contains(proCityBean.getList().get(i).getCity().get(j).getName())){
                        adcode=proCityBean.getList().get(i).getCity().get(j).getAdcode();
                        break;
                    }
                }
            }
        }catch (Exception e){

        }

        return adcode;
    }

    public static String  getCityName(Context context,String adcode){
        String cityName=adcode;
        try {
            ProCityBean proCityBean = new Gson().fromJson(getJson("adcode.json",context), ProCityBean.class);
            for(int i=0;i<proCityBean.getList().size();i++){
                for(int j=0;j<proCityBean.getList().get(i).getCity().size();j++){
                    if(adcode.equals(proCityBean.getList().get(i).getCity().get(j).getAdcode())){
                        cityName=proCityBean.getList().get(i).getCity().get(j).getName();
                        break;
                    }
                }
            }
        }catch (Exception e){

        }

        return cityName;
    }
}
