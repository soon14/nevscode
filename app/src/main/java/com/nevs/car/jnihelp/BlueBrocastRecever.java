package com.nevs.car.jnihelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MainActivity;
import com.nevs.car.z_start.MyApp;

public class BlueBrocastRecever extends BroadcastReceiver {
    public static BlueListener callback;
    public static void setCallback(BlueListener blueListener){
       callback=blueListener;
    }
    public static void toSend(int i,int id,byte[] bytes){
        if(callback!=null) {
            callback.getBlueReceiver(i, id, bytes);
        }
    }
    public static LogBlueLin callback2;
    public static void setLog(LogBlueLin blueListener){
        callback2=blueListener;
    }
    public static void toLog(int i,String str){
        if(callback2!=null) {
            callback2.getLog(i,str);
        }
    }
    private int frameid;//认证发送时回的ACK
//    private boolean shou=false;
//    private boolean shou2=false;
    //F0:ED:F4:B3:78:F2
    private final static Object syncLock = new Object();
    private static BlueBrocastRecever notificationReceiver;

    private BlueBrocastRecever() {
    }
    public static BlueBrocastRecever getInstance(){
        // synchronized同步块处括号中的锁定对象采用的是一个无关的Object类实例。
        // 将它作为锁而不是通常synchronized所用的this，其原因是getInstance方法是一个静态方法，
        // 在它的内部不能使用未静态的或者未实例化的类对象（避免空指针异常）。
        // 同时也没有直接使用instance作为锁定的对象，是因为加锁之时，instance可能还没实例化（同样是为了避免空指针异常）。
        if (notificationReceiver == null) {
            synchronized (syncLock) {
                if(notificationReceiver == null)
                    notificationReceiver = new BlueBrocastRecever();
            }
        }
        return notificationReceiver;
    }

    public static IntentFilter makeGattUpdateIntentFilterM() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.READ_RSSI);
        intentFilter.addAction(BleService.WRITE);
        intentFilter.addAction(BleService.SER);
        intentFilter.addAction(BleService.TOAST);
        intentFilter.addAction(BleService.ACCEPT);
        intentFilter.addAction(BleService.SETMTU);
        intentFilter.addAction(BleService.BYTEACCEPT);
        intentFilter.addAction(BleService.BYTEACCEPT2);
        intentFilter.addAction(BleService.BYTEACCEPTSS);
        intentFilter.addAction(BleService.NOTIFYRENZ);
        intentFilter.addAction(BleService.SETMTUONSUC);

        intentFilter.addAction(BleService.ACCEPT21);
        intentFilter.addAction(BleService.BYTEACCEPT22);
        return intentFilter;
    }
    public static IntentFilter makeGattUpdateIntentFilterL() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.READ_RSSI);
        intentFilter.addAction(BleService.WRITE);
        intentFilter.addAction(BleService.SER);
        intentFilter.addAction(BleService.TOAST);
        intentFilter.addAction(BleService.ACCEPT21);
        intentFilter.addAction(BleService.SETMTU);
        intentFilter.addAction(BleService.BYTEACCEPT22);
        intentFilter.addAction(BleService.BYTEACCEPTSTART);
        intentFilter.addAction(BleService.BYTESTARTJIE);
        intentFilter.addAction(BleService.BYTEBOOKINGSTART);
        intentFilter.addAction(BleService.BYTESTARTJIEPIN);
        intentFilter.addAction(BleService.ENTERPIN);
        intentFilter.addAction(BleService.BYTEPINEND);
        intentFilter.addAction(BleService.BYTEPINEND2);
        intentFilter.addAction(BleService.BYTEUNLOCKF);
        intentFilter.addAction(BleService.BYTEUNLOCKJ);
        intentFilter.addAction(BleService.BYTEUNLOCKJ2);
        intentFilter.addAction(BleService.SETMTUONSUC);
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();//接收广播
        Log.e("tag", "action:" + action);
        if (BleService.NOTIFYRENZ.equals(action)) {//通知开启
            MLog.e("通知开启 广播里");
            String renIsSucc=new SharedPHelpers(context,"rz"+new SharedPHelper(context).get("TSPVIN", "0")).get(Constant.ISRENZHENG,"0")+"";//1为认证成功
            if(renIsSucc.equals("1")){
                MLog.e("gb此车认证成功不用认证");
                toSend(300,-2,new byte[]{0});//开始认证//重连
            }else {
                toSend(1,-2,new byte[]{0});//开始认证
            }

        } else if (BleService.ACTION_GATT_CONNECTED.equals(action)) {//连接状态
//                connectFlag = CONNECTED;
//                proDialog.dismiss();
//                state.setText("已连接");
//                finis.setText("断开");
//                flag=false;
//                ToastU.showShort(getApplicationContext(), "连接成功");
            //  writeDate(true);
            MLog.e("连接成功");

            // renOnef();//开始认证

          //c1  invalidateOptionsMenu();
        } else if (BleService.ACTION_GATT_DISCONNECTED
                .equals(action)) {//未连接
            //加入是否可以点击
//                proDialog.dismiss();
//                state.setText("未连接");
//                finis.setText("连接");
//                flag=true;
//                ToastU.showShort(getApplicationContext(), "连接失败");
//                Log.e("tag", isClickDisconnect + "isClickDisconnect");
//                if (!isClickDisconnect) {
//                    //    vibrator.vibrate(new long[]{100, 2000, 500, 2500}, -1);
//                    //  mp.start();
//                }
//                closeUi();
            MLog.e("未连接");
        } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) {
           toSend(2,-2,new byte[]{0});
        } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {

        } else if (BleService.READ_RSSI.equals(action)) {
            // Log.e("tag", "rssi:" + rssi);
//                checkRssi(rssi);
            //  iss.setText("信号：" + Math.abs(rssi));
        } else if (BleService.SER.equals(action)) {
//                SUUIDW = intent.getStringExtra("suuid");
//                nonono.append(SUUIDW+"\n");


        } else if (BleService.WRITE.equals(action)) {
//                UUIDW = intent.getStringExtra("uuids");
//                // MLog.e("UUIDs:" + UUIDW);
//                nonono.append(UUIDW+"\n");
        }else if (BleService.TOAST.equals(action)) {
            // nonono.setText("");
//                nonono.append("写入成功"+"\n");
//
//                if(intent.getStringExtra("Hex")==null){
//                    return;
//                }
//                nonono.append(intent.getStringExtra("Hex")+"\n");

            MLog.e("写入成功\"+\"\\n");
            MLog.e(intent.getStringExtra("Hex")+"\n");

        }else if (BleService.ACCEPT.equals(action)) {//开始认证
            //nonono.setText("");
            //  nonono.append("收到数据"+"\n");

            if (intent.getStringExtra("AHex") == null) {
                return;
            }
            //  nonono.append(intent.getStringExtra("AHex")+"\n");
            frameid = Integer.parseInt(intent.getStringExtra("FRAMID"));

            try {
                if ((new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.ISRENZHENG, "0") + "").equals("0")) {
                    toLog(91, "frameid:" + frameid + "\n" + intent.getStringExtra("AHex"));
                }

            } catch (Exception e) {
                MLog.e("yy" + e);
            }

//            if(shou){
//
//                MLog.e("接收流程");
//
//            }else {
//                if(frameid==-1){
//                    MLog.e("认证结束时间 耗时："+ (MyUtils.timeStampNow()-MainActivity.startrenzheng)+"s");
//                    shou=true;
//                }else {
//                    try {
//                        //控制异常  40
//                        Thread.sleep(40);
//                        toSend(3,frameid,new byte[]{0});
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
            //    }

            if (frameid == -1) {
                MLog.e("认证结束时间 耗时：" + (MyUtils.timeStampNow() - MainActivity.startrenzheng) + "s");
            } else {
                try {
                    //控制异常  40
                    Thread.sleep(40);//100
                    toSend(3, frameid, new byte[]{0});
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


//                if(frameid==37){
//                    MLog.e("收到37帧ID");
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
//                }else if(frameid==38){
//                    MLog.e("收到38帧ID");
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
//                }else {
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
//                }

        }else if (BleService.ACCEPT21.equals(action)) {//开始重连
                //nonono.setText("");
                //  nonono.append("收到数据"+"\n");

                if(intent.getStringExtra("AHex21")==null){
                    return;
                }
                //  nonono.append(intent.getStringExtra("AHex")+"\n");
                frameid= Integer.parseInt(intent.getStringExtra("FRAMID21"));


                if(frameid==-1){
                    MLog.e("认证结束时间 耗时："+ (MyUtils.timeStampNow()- MainActivity.startrenzheng)+"s");
                }else {
                    try {
                        //控制异常  40
                        Thread.sleep(40);//100
                        toSend(33,frameid,new byte[]{0});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


        }else if (BleService.SETMTU.equals(action)) {
            //   btnOpen.setText("");
            if(intent.getStringExtra("MTU")==null){
                return;
            }
            // renOnef();//开始认证
            //  btnOpen.append(intent.getStringExtra("MTU")+"\n");
        }else if(BleService.BYTEACCEPT.equals(action)){//认证接收
            byte[] acc=intent.getByteArrayExtra("byteacept2");
            toSend(4,-2,acc);
            try {
                if((new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.ISRENZHENG,"0")+"").equals("0")){
                    toLog(92,"认证接收："+StringUtils.bytesToHexString(acc));
                }

            }catch (Exception e){
                MLog.e("yy"+e);
            }

        }else if(BleService.BYTEACCEPT22.equals(action)){//重连接收
            byte[] acc=intent.getByteArrayExtra("byteacept22");
            toSend(34,-2,acc);

        }else if(BleService.BYTEACCEPTSS.equals(action)){//认证接收后AES发送
            byte[] acc=intent.getByteArrayExtra("byteacept3");

            try {
                if((new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.ISRENZHENG,"0")+"").equals("0")){
                    toLog(93,"认证接收后AES发送:"+ StringUtils.bytesToHexString(acc));
                }

            }catch (Exception e){
                MLog.e("yy"+e);
            }




//            if(shou2){
//                MLog.e("renz2接收流程");
//            }else {
//                if(acc[2]==-1){
//                    MLog.e("rezAES发送结束时间："+MyUtils.getTimeNow());
//                    shou=true;
//
//                }else {
//                    if(acc[2]==0){
//                        MLog.e("收到心跳接收的广播里面");
//                    }else {
//                        try {
//                            Thread.sleep(40);
//                            // mBleService.sendOrders(listrenzSplit.get(acc[2]), acc[2]);
//                            toSend(5,-2,new byte[]{acc[2]});
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            MLog.e("rezAES发送结束时间后异常:"+e);
//                        }
//                    }
//
//
//                }

                if(acc[2]==-1){
                    MLog.e("rezAES发送结束时间："+MyUtils.getTimeNow());


                }else {
                    if(acc[2]==0){
                        MLog.e("收到心跳接收的广播里面");
                    }else {
                        try {
                            Thread.sleep(40);
                            // mBleService.sendOrders(listrenzSplit.get(acc[2]), acc[2]);
                            toSend(5,-2,new byte[]{acc[2]});

                        } catch (Exception e) {
                            e.printStackTrace();
                            MLog.e("rezAES发送结束时间后异常:"+e);
                        }
                    }



            }

        }else if(BleService.BYTEACCEPT2.equals(action)){//认证2接收
            byte[] acc=intent.getByteArrayExtra("byteacept4");
            toSend(6,-2,acc);
            try {
                if((new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.ISRENZHENG,"0")+"").equals("0")){
                    toLog(94,"认证2接收:"+ StringUtils.bytesToHexString(acc));
                }

            }catch (Exception e){
                MLog.e("yy"+e);
            }


        }else if(BleService.BYTEACCEPTSTART.equals(action)){//START发送
            byte[] acc=intent.getByteArrayExtra("byteacept5");


         //   toLog(95,"START发送:"+ StringUtils.bytesToHexString(acc));

            if(acc[2]==-1){
                MLog.e("start发送结束时间："+MyUtils.getTimeNow());


            }else {
                try {
                    Thread.sleep(40);
                    toSend(21,-2,new byte[acc[2]]);
                    //mBleService.sendOrders(listStartbooking.get(acc[2]), acc[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }


        }else if(BleService.BYTESTARTJIE.equals(action)){//startbooking接收
            byte[] acc=intent.getByteArrayExtra("byteacept6");
            if(acc!=null) {
                toSend(22,-2,acc);
               // getCrcStartbookingj(acc);
            }
        }else if(BleService.BYTEBOOKINGSTART.equals(action)){//STARTED ok发送
            byte[] acc=intent.getByteArrayExtra("byteacept7");


            if(acc[2]==-1){
                MLog.e("startED ok发送结束时间："+MyUtils.getTimeNow());

                //     new SharedPHelper(MainControllActivity.this).put("step8","1");

            }else {
                MLog.e("ok第二次发送");
                try {
                    Thread.sleep(40);
                    toSend(23,-2,new byte[acc[2]]);
                   // mBleService.sendOrders(listbookingstarted.get(acc[2]), acc[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }

        }else if(BleService.BYTESTARTJIEPIN.equals(action)){//接收手动发ENDPIN的指令
            byte[] acc=intent.getByteArrayExtra("byteacept8");
            if(acc!=null) {
                toSend(24,-2,acc);
                //getCrc8(acc);
            }
        }else if(BleService.ENTERPIN.equals(action)){//STARTED发送
            byte[] acc=intent.getByteArrayExtra("byteacept9");

            if(acc[2]==-1){
                MLog.e("PIN发送结束时间："+MyUtils.getTimeNow());

            }else {
                try {
                    Thread.sleep(40);
                    toSend(25,-2,new byte[acc[2]]);
                   // mBleService.sendOrders(listbookingstartedpin.get(acc[2]), acc[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }

        }else if(BleService.BYTEPINEND.equals(action)){//接收手动发ENDPIN的指令
            byte[] acc=intent.getByteArrayExtra("byteacept10");
            if(acc!=null) {
                toSend(26,-2,acc);
               // getCrcPINEND(acc);
            }
        }else if(BleService.BYTEPINEND2.equals(action)){//接收手动发ENDPIN的指令
            byte[] acc=intent.getByteArrayExtra("byteacept20");
            if(acc!=null) {
                toSend(27,-2,acc);
               // getCrcPINEND2(acc);
            }
        }

        else if(BleService.BYTEUNLOCKF.equals(action)){//UNLOCK发送
            byte[] acc=intent.getByteArrayExtra("byteacept11");


            if(acc[2]==-1){
                MLog.e("UNLOCK发送结束时间："+MyUtils.getTimeNow());


            }else {
                try {
                    Thread.sleep(40);
                    toSend(28,-2,new byte[acc[2]]);
//                    mBleService.sendOrders(listByteUnLock.get(acc[2]), acc[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }


        }else if(BleService.BYTEUNLOCKJ.equals(action)){//接收UNLOCK
            byte[] acc=intent.getByteArrayExtra("byteacept12");
            if(acc!=null) {
                toSend(29,-2,acc);
              //  getCrcUNLOCK(acc);
            }
        }else if(BleService.BYTEUNLOCKJ2.equals(action)){//接收UNLOCK2
            byte[] acc=intent.getByteArrayExtra("byteacept13");
            if(acc!=null) {
                toSend(30,-2,acc);
               // getCrcUNLOCK2(acc);
            }
        }
    }
}
