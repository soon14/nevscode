package com.nevs.car.tools.util;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.TspInterfaceCall;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.interfaces.UtilListener;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.z_start.LoginActivity;
import com.nevs.car.z_start.MyApp;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2018/7/12.
 */

public class MyUtils {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    public static boolean isSendLog = true;
    public static List<HashMap<String, Object>> list = new ArrayList();
    public static String pess = "";

    /**
     * 通过反射调用获取内置存储和外置sd卡根路径(通用)
     *
     * @param mContext    上下文
     * @param is_removale 是否可移除，false返回内部存储，true返回外置sd卡
     * @return
     */
    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Android 字符串写入文件
    配置好文件路径 文件名 直接调用initData（）；
     */
    public static void initData(String filePath, String fileName, String content) {
        writeTxtToFile(content, filePath, fileName);
        //String filePath="/storage/emulated/0/"
        //String fileName="123.txt";
    }

    //将字符串写入到文本文件中
    public static void writeTxtToFile(String content, String filePath, String fileName) {

        try {
            //生成文件夹之后，再生成文件，不然会出错
            makeFilePath(filePath, fileName);
            String strFilePath = filePath + fileName;
            //每次写入时。都换行写
            //String strContent =content +"\t\n";
            File file = new File(strFilePath);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                MLog.e("Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(content.getBytes("UTF-8"));
            raf.close();
        } catch (Exception e) {
            MLog.e("ERROR ON WRITE FILE:" + e);
        }
    }

    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        try {
            makeRootDiretory(filePath);
            file = new File(filePath + fileName);
            if (!file.exists()) {
                // file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("文件生成异常");
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDiretory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                //file.mkdir();
                file.mkdirs();
            }
        } catch (Exception e) {
            MLog.e("文件夹makeRootDiretory:err" + e);
        }
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        /**
         * 参数解析：
         src：byte源数组
         begin:源数组要复制的起始位置（0位置有效）
         bs：byte目的数组（截取后存放的数组）
         0：截取后存放的数组起始位置（0位置有效）
         count：截取的数据长度
         * */
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /*
     * 拆分byte数组
     *
     * @param bytes
     *            要拆分的数组
     * @param copies
     *            要按几个组成一份
     *            输入要拆分的数组及要按几个拆成一份即可，返回二维数组byte[]
     * @return
     */
    public static byte[][] split_bytes(byte[] bytes, int copies) {

        double split_length = Double.parseDouble(copies + "");

        int array_length = (int) Math.ceil(bytes.length / split_length);
        byte[][] result = new byte[array_length][];

        int from, to;

        for (int i = 0; i < array_length; i++) {

            from = (int) (i * split_length);
            to = (int) (from + split_length);

            if (to > bytes.length)
                to = bytes.length;

            result[i] = Arrays.copyOfRange(bytes, from, to);
        }

        return result;
    }

    /**
     *
     byte[] sum = { 23, 4, 23, 42, 34, 2, 34, 2, 34, 2, 54, 3, 4, 56, 4, 7, 56, 7, 8, 5, 15, 2, 34, 2, 41, 2, 32, 3,
     3, 3, 33 };
     //按四个分成一组
     byte[][] bytes = split_bytes(sum, 4);

     System.out.println(bytes.length);

     System.out.print("\r\n\r\n==========================================\r\n");

     for (int i = 0; i < bytes.length; i++) {
     for (int j = 0; j < bytes[i].length; j++) {
     System.out.print(bytes[i][j] + " ");
     }
     System.out.println("");
     }

     结果
     8
     ==========================================
     23 4 23 42
     34 2 34 2
     34 2 54 3
     4 56 4 7
     56 7 8 5
     15 2 34 2
     41 2 32 3
     3 3 33
     * */


    /**
     * 剩余百分比 保留两位小数的float
     *
     * a=(x*1.0)/(y*1.0);
     double a = (double)x/(double)y;
     * Java float保留两位小数或多位小数
     方法1:用Math.round计算,这里返回的数字格式的.

     float price=89.89;
     int itemNum=3;
     float totalPrice=price*itemNum;
     float num=(float)(Math.round(totalPrice*100)/100);//如果要求精确4位就*10000然后/10000
     方法2:用DecimalFormat 返回的是String格式的.该类对十进制进行全面的封装.像%号,千分位,小数精度.科学计算.

     float price=1.2;
     DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
     String p=decimalFomat.format(price);//format 返回的是字符串
     个人觉得在前台显示金额方面的还是用第二种方式.理由很简单是字符串格式的.
     * */
    public static float getTwoPoint(double a, double b) {
        float aa = 0;
        aa = (float) (a / b);
        MLog.e("aa:" + aa);
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(aa);//format 返回的是字符串
        aa = Float.parseFloat(p);
        return aa;
    }


//    public static List<HashMap<String,Object>> xJson(String json, List<HashMap<String,Object>> list){
//        list.clear();
//
//        Gson gson=new Gson();
//        JsonParser parser=new JsonParser();
//        JsonObject jsonObj=parser.parse(json).getAsJsonObject();
//        JsonObject data= (JsonObject) jsonObj.get("data");
//        if(data.has("cars")){
//            JsonArray jsonArray=data.getAsJsonArray("cars");
//            Type type = new TypeToken<HashMap<String,Object>>() {}.getType();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                JsonElement el = jsonArray.get(i);
//                HashMap<String, Object> tmp = gson.fromJson(el, type);
//                list.add(tmp);
//                // MLog.e("标题：" + list.get(i).get("newsID"));
//            }
//        }
//        return list;
//    }

    public static List<HashMap<String, Object>> xJson(String json, List<HashMap<String, Object>> list) {
        list.clear();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        if (jsonObj.has("data")) {
            JsonArray jsonArray = jsonObj.getAsJsonArray("data");
            Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement el = jsonArray.get(i);
                HashMap<String, Object> tmp = gson.fromJson(el, type);
                list.add(tmp);
                // MLog.e("标题：" + list.get(i).get("newsID"));
            }
        }
        return list;
    }


    public static String getZ(String ss) {
        String aa = "";
        String temp1[] = null;
        try {
            temp1 = ss.split("\\.");
            aa = temp1[0];
        } catch (Exception e) {
            temp1 = new String[]{ss};
            aa = temp1[0];
        }


        return aa;
    }


    public static int stringToInt(String string) {
        int intgeo;
        try {
            String str = string.substring(0, string.indexOf(".")) + string.substring(string.indexOf(".") + 1);

            intgeo = Integer.parseInt(str);
        } catch (Exception e) {
            intgeo = Integer.parseInt(string);
        }


        return intgeo;
    }


    public static boolean isCEnglish(String str) {
        boolean aa = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i)) == true) {
                aa = true;
                MLog.e("包含字母");
            }
        }
        return aa;
    }


    //获得当天24点时间
    public static long getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (long) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 日期格式字符串转换成时间戳
     ** *@param date 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static long timeStampNow() {//得到秒数 10
        long timeStampSec = System.currentTimeMillis() / 1000;
        // long timeStampSec=new Date().getTime()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        return Long.parseLong(timestamp);
    }

    public static String getTimeNow() {//获取当前格式化时间

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }


    public static void exitToLongin(Context mcontext) {//重复登录处理
        DialogUtils.NormalDialogOneBtnHintExit(mcontext, (Activity) mcontext);
        //ActivityUtil.showToast(mcontext,mcontext.getResources().getString(R.string.losttoken));

    }

    public static void exitToLongin2(Context mcontext) {//重复登录处理
//        Intent intent = new Intent(mcontext, LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        mcontext.startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(mcontext, LoginActivity.class);
        mcontext.startActivity(intent);
        //ActivityUtil.showToast(mcontext,mcontext.getResources().getString(R.string.losttoken));
        ShareUtil.storett(mcontext, "0", Constant.LONGINTTS, Constant.LONGINTT);
    }

    public static void exitToLongin401(Context mcontext) {//TSP TOKEN失效处理
//        Intent intent = new Intent(mcontext, LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        mcontext.startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(mcontext, LoginActivity.class);
        mcontext.startActivity(intent);
        ActivityUtil.showToast(mcontext, mcontext.getResources().getString(R.string.losttoken));
    }

    // 上传TSP日志
    public static void upLogTSO(Context context, String InterfaceName, String reason, String callTime, String resultTime, String result, String args) {
        if (isSendLog) {
            try {
                final SharedPHelpers sharedPHelpersss = new SharedPHelpers(context, Constant.UPLOGFILENAME);
                HttpRxUtils.getUpLogTSP(context,
                        new String[]{"deviceID", "InterfaceName", "accessToken", "appType", "TspInterfaceCall"},
                        new Object[]{
                                DeviceUtils.getUniqueId(context),
                                InterfaceName,
                                new SharedPHelper(context).get(Constant.ACCESSTOKEN, ""),
                                "Android",
                                new TspInterfaceCall(InterfaceName, "Y", callTime, resultTime, sharedPHelpersss.get(Constant.GETPAMAS, "NULL") + "", "Android", sharedPHelpersss.get(Constant.UPPAMAS, "NULL") + "")

                        });
            } catch (Exception e) {

            }

        }
    }


    public static String getDisTwoPoint(String s) {
        String diss = String.valueOf(Double.parseDouble(s) / 1000);
        MLog.e("距离：km" + diss);
        DecimalFormat myformat = new DecimalFormat("0.00");
        String str = myformat.format(Double.parseDouble(diss)) + "km";
        return str;
    }

    public static String getDisOnePoint(String s) {//充电站
        String diss = String.valueOf(Double.parseDouble(s));
        MLog.e("距离：km" + diss);
        DecimalFormat myformat = new DecimalFormat("0.0");
        String str = myformat.format(Double.parseDouble(diss)) + MyApp.getInstance().getResources().getString(R.string.chargeone);
        return str;
    }

    public static void clearGlide(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                GlideCacheUtil.getInstance().clearImageAllCache(context);
            }
        }).start();
    }

    public static void setWindow(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//透明导航栏
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

    }

    //预约大于当天（晚上24时）的时间
    public static boolean setTime24(String time, Context mContext) {
        boolean flag = false;
        String selectedDate = time.substring(0, time.length() - 6);
        long cc = HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd");
        MLog.e("选择日期的时间戳：" + HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd"));
        MLog.e("当天0点的时间戳：" + (MyUtils.getTimesnight() - 24 * 3600));
        MLog.e("当前的时间戳：" + MyUtils.timeStampNow());
        MLog.e("当天24点的时间戳：" + MyUtils.getTimesnight());
        long nowZero = MyUtils.getTimesnight() - 24 * 3600;
        if (cc > nowZero) {//大于当天晚上24时
            flag = true;
        } else {
            flag = false;
            ActivityUtil.showToast(mContext, mContext.getResources().getString(R.string.pleasebigtiomes));
        }

        return flag;
    }


    public static boolean getLanguage(Context context) {//中文返回TRUE

        boolean langu = true;
        //判断用户是否是第一次登陆，第一次登陆使用系统的设置，如果不设置还是跟谁系统，如果设置了以后都用应用自己设置的语言。

        //获取是否第一次进入APP的状态值
        String isFisrst = ShareUtil.readIsFirst(context, "Isfirst", "counst");
        MLog.e("第" + isFisrst + "次进入APP");
        //获取系统语言
        String able = GetLanguageUtil.getLanguage();

        if (isFisrst.equals("1")) {
            //将APP的进入状态改为"2"
            ShareUtil.storeIsFirst(context, "2", "Isfirst", "counst");
            switch (able) {//"zh"为中文，"cn"为英文
                case "zh":
                    langu = true;
                    break;
                case "cn":
                    langu = false;
                    break;
            }
        } else if (isFisrst.equals("2")) {
            //获取用户设置语言状态,没有更改还是根据系统设置语言，如果改了就设为用户之前设置的语言
            String isSetting = ShareUtil.readSettingLanguage(context, "issettings", "issetting");
            //此时设置成哪种语言了需要在APP语言的设置界面写入缓存
            switch (isSetting) {//""为未设置,"zh"为中文"cn"为英文
                case "":
                    switch (able) {//"zh"为中文，"cn"为英文
                        case "zh":
                            langu = true;
                            break;
                        case "cn":
                            langu = false;
                            break;
                    }
                    break;
                case "zh":
                    langu = true;
                    break;
                case "cn":
                    langu = false;
                    break;
            }
        }

        return langu;
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     * 方法一
     */
    public static boolean isForeground0(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;

    }

    /**
     * 判断某个activity是否在前台显示  方法二
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台,返回true，为显示,否则不是
     * 调用：
     *  if (isForeground(activity)){
     //在前台显示，做逻辑
     }

     */
    public static boolean isForeground(Activity context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }


    //Android根据图片的名字获取对应的资源ID    ''drawable"  "mipmap"
    //方案一：
    //利用getResources().getIdentifier(String name,String defType,String defPackage) 获取

    public static int getResourceID(String imageName, Context context) {
        Context ctx = context;
        int resId = context.getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

//    方案二：
//    使用反射机制获取

    public static int getResourceID1(String imageName) {
        Class mipmap = R.mipmap.class;
        try {
            Field field = mipmap.getField(imageName);
            int resId = field.getInt(imageName);
            return resId;
        } catch (NoSuchFieldException e) {
            //如果没有在"mipmap"下找到imageName,将会返回0
            return 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    public static String dosubtext(String str, Context context) {
        String cc = "";
        try {
            //字符串截取
            String bb = str.substring(3, 7);
            //字符串替换
            cc = str.replace(bb, "****");
        } catch (Exception e) {
            MLog.e("字符串截取yic");
            cc = context.getResources().getString(R.string.nevs_user);
        }

        return cc;
    }

    public static boolean isChina(Context context) {
        boolean isChaina = true;
        //获取系统语言
        String able = GetLanguageUtil.getLanguage();
        //获取用户设置语言状态,没有更改还是根据系统设置语言，如果改了就设为用户之前设置的语言
        String isSetting = ShareUtil.readSettingLanguage(context, "issettings", "issetting");
        //此时设置成哪种语言了需要在APP语言的设置界面写入缓存
        switch (isSetting) {//""为未设置,"zh"为中文"cn"为英文
            case "":
                switch (able) {//"zh"为中文，"cn"为英文
                    case "zh":
                        isChaina = true;
                        break;
                    case "cn":
                        isChaina = false;
                        break;
                }
                break;
            case "zh":
                isChaina = true;
                break;
            case "cn":
                isChaina = false;
                break;
        }
        return isChaina;
    }


    public static void clearPdf() {
        try {
            File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.MYNEVSCARPDF + Constant.MYNEVSCARPDFCH + ".pdf");
            File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.MYNEVSCARPDF + Constant.MYNEVSCARPDFEN + ".pdf");
            if (file1.exists()) {
                file1.delete();
            }
            if (file2.exists()) {
                file2.delete();
            }
        } catch (Exception e) {

        }

    }


    public static boolean isCarowern(Context context) {
        String is = new SharedPHelper(context).get(Constant.TSPISCAROWER, "0").toString();
        MLog.e("is" + is);
        if (is.equals("YES")) {
            return true;
        } else {
            return false;
        }
    }

    public static String toBacks(String adrress) {
        String add = "";
        try {
            String[] all = adrress.split(":");
            add = all[5] + ":" + all[4] + ":" + all[3] + ":" + all[2] + ":" + all[1] + ":" + all[0];
        } catch (Exception e) {
            MLog.e("蓝牙地址错误");
        }
        return add;
    }


    /**
     * （1）四舍五入把double转化int整型，0.5进一，小于0.5不进一
     * @param number
     * @return
     */
    public static int getInt(double number) {
        BigDecimal bd = new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }


    /**
     * （2）四舍五入把double转化为int类型整数,0.5也舍去,0.51进一
     * @param dou
     * @return
     */
    public static int DoubleFormatInt(Double dou) {
        DecimalFormat df = new DecimalFormat("######0"); //四色五入转换成整数
        return Integer.parseInt(df.format(dou));
    }


    /**
     * （3）去掉小数凑整:不管小数是多少，都进一
     * @param number
     * @return
     */
    public static int ceilInt(double number) {
        return (int) Math.ceil(number);
    }

    public static void savaPermissions(List<String> list, Context context) {
        try {
            if (list == null) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "1", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "2", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "3", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "4", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "5", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "6", "0");//1有权限，0无权限
                return;
            }
            if (list.size() == 0) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "1", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "2", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "3", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "4", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "5", "0");//1有权限，0无权限
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "6", "0");//1有权限，0无权限
                return;
            }


            String lists = "";
            for (int k = 0; k < list.size(); k++) {
                lists += list.get(k);
            }
            if (lists.contains("1")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "1", "1");//1有权限，0无权限
                MLog.e("权限" + 1);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "1", "0");//1有权限，0无权限
                MLog.e("无权限" + 1);
            }

            if (lists.contains("2")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "2", "1");//1有权限，0无权限
                MLog.e("权限" + 2);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "2", "0");//1有权限，0无权限
                MLog.e("无权限" + 2);
            }

            if (lists.contains("3")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "3", "1");//1有权限，0无权限
                MLog.e("权限" + 3);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "3", "0");//1有权限，0无权限
                MLog.e("无权限" + 3);
            }

            if (lists.contains("4")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "4", "1");//1有权限，0无权限
                MLog.e("权限" + 4);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "4", "0");//1有权限，0无权限
                MLog.e("无权限" + 4);
            }

            if (lists.contains("5")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "5", "1");//1有权限，0无权限
                MLog.e("权限" + 5);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "5", "0");//1有权限，0无权限
                MLog.e("无权限" + 5);
            }

            if (lists.contains("6")) {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "6", "1");//1有权限，0无权限
                MLog.e("权限" + 6);
            } else {
                new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).put(Constant.PEEMISSIONPARAMA + "6", "0");//1有权限，0无权限
                MLog.e("无权限" + 6);
            }
        } catch (Exception e) {

        }

    }

    public static boolean getPermissions(String param, Context context) {
        boolean flag = false;
        try {
            String pa = new SharedPHelpers(context, Constant.PEEMISSIONFILENAME).get(Constant.PEEMISSIONPARAMA + param, "0") + "";//1有权限，0无权限
            MLog.e("pa:" + pa);
            if (pa.equals("1")) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {

        }
        MLog.e(param + "是否有权限" + flag);
        return flag;
    }

    public static void getTsp6(final Context mContext) {
        try {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.clear();
            HttpRxUtils.getCarList(mContext,
                    new String[]{"appType", "accessToken", "nevsAccessToken"},
                    new Object[]{"Android", new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                    new HttpRxListener() {
                        @Override
                        public void onSucc(Object obj) {
                            list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                            if (list.size() != 0) {
                                MLog.e("长度：" + list.size() + ": " + list.get(0).get("vin"));
                                for (int i = 0; i < list.size(); i++) {
                                    if ((new SharedPHelper(mContext).get("TSPVIN", "0") + "").equals(list.get(i).get("vin"))) {
                                        savaPermissions((List<String>) list.get(i).get("permissions"), mContext);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFial(String str) {

                        }
                    }
            );

        } catch (Exception e) {

        }

    }


    public static boolean getResult(String word, Context context) {
        if (getBig(word) && getSmoll(word) && isContainsNum(word)) {
            return true;
        }
        if (!getBig(word)) {
            ActivityUtil.showToast(context, context.getResources().getString(R.string.hint_must_big));
            return false;
        } else if (!getSmoll(word)) {
            ActivityUtil.showToast(context, context.getResources().getString(R.string.hint_must_smoll));
            return false;
        } else if (!isContainsNum(word)) {
            ActivityUtil.showToast(context, context.getResources().getString(R.string.hint_must_number));
            return false;
        }
        return false;

    }

    public static boolean getBig(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!Character.isLowerCase(c)) {
                MLog.e("包含大写");
                return true;
            }

        }
        return false;

    }

    public static boolean getSmoll(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLowerCase(c)) {
                MLog.e("包含小写");
                return true;
            }

        }

        return false;
    }

    /** 判断字符串中是否包含数字 **/
    public static boolean isContainsNum(String input) {
        int len = input.length();
        for (int i = 0; i < len; i++) {
            if (Character.isDigit(input.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void showHint(Context context) {
        try {
            if (new SharedPHelpers(context, "hintrefrehfilename").get("hintrefrehpa", "0").toString().equals("0")) {
                ActivityUtil.showLongToast(context, context.getResources().getString(R.string.hint_below_refresh));
                new SharedPHelpers(context, "hintrefrehfilename").put("hintrefrehpa", "1");
            }
        } catch (Exception e) {

        }

    }


    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int height = 40;
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        } catch (Exception e) {
        }
        return height;
    }

    public static void setPadding(View view, Context context) {
        try {
            view.setPadding(0, getStatusBarHeight(context), 0, 0);//int left, int top, int right, int bottom
        } catch (Exception e) {

        }
    }


    public static void getLatlon(final String cityName, Context context, final UtilListener utilListener) {

        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null &&
                            geocodeResult.getGeocodeAddressList().size() > 0) {

                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
//                        double latitude = geocodeAddress.getLatLonPoint().getLatitude();//纬度
//                        double longititude = geocodeAddress.getLatLonPoint().getLongitude();//经度
                        String adcode = geocodeAddress.getAdcode();//区域编码


                        MLog.e(cityName + "地理编码:" + geocodeAddress.getAdcode() + "");
                        utilListener.callBack(0, adcode);

                    } else {
                        // ToastUtils.show(context,"地址名出错");
                    }
                }
            }
        });

        GeocodeQuery geocodeQuery = new GeocodeQuery(cityName.trim(), "29");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);


    }


    public static void gotoNotificationSetting(Activity activity) {
        ApplicationInfo appInfo = activity.getApplicationInfo();
        String pkg = activity.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.putExtra("app_package", pkg);
                intent.putExtra("app_uid", uid);
                activity.startActivityForResult(intent, 111);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + MyApp.getInstance().getPackageName()));
                activity.startActivityForResult(intent, 111);
            } else {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivityForResult(intent, 111);
            }
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(intent, 111);

        }
    }


    /**
     * 获取当前app version code
     */
    public static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            MLog.e(e.getMessage());
        }
        return appVersionCode;
    }

    /**
     * 获取当前app version name
     */
    public static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            MLog.e(e.getMessage());
        }
        return appVersionName;
    }


}
