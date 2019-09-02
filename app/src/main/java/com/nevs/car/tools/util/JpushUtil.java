package com.nevs.car.tools.util;

import android.content.Context;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by mac on 2018/6/15.
 */

public class JpushUtil {
    public static void setAliasAndTag(Context context, final String usernameAla) {
        TagAliasCallback tagAliasCallback = new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                MLog.e("极光个人回调："+s);
//                Iterator it = tag.iterator();
//                while (it.hasNext()) {
//                   MLog.e("set:"+it.next());
//                }

            }
        };
        //JPushInterface.setAliasAndTags(context, null, tag, tagAliasCallback);
       // JPushInterface.setAlias(context,usernameAla,tagAliasCallback);
        JPushInterface.setAlias(context,0,usernameAla);
    }

    public static void toJpush(Context context,String usernameAla){
        Set<String> set = new HashSet<>();
        //String Tag = resultsBean.getUserid() + "";
        String Tag = usernameAla;
        set.add(Tag);
        JpushUtil.setAliasAndTag(context, usernameAla);

    }


    public static void setTags(Context context){
        Set<String> set = new HashSet<>();
        set.add("all");
        if(new SharedPHelper(context).get(Constant.LOGINISPA,"").equals("Yes")){
            set.add("isPa");
        }
        if(new SharedPHelper(context).get(Constant.LOGINISCAROWNER,"").equals("YES")){
            set.add("isCarOwner");
        }
       // JPushInterface.setTags(context,Integer.parseInt(new SharedPHelper(context).get(Constant.LOGINNAME,"0")+""),set);
        JPushInterface.setTags(context,0,set);
    }
}
