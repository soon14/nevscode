package com.nevs.car.jnihelp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.jnihelp.modle.BookingCertSNBean;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.encrypt.AES;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MyApp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.nevs.car.tools.util.MyUtils.split_bytes;
import static org.bouncycastle.asn1.gnu.GNUObjectIdentifiers.CRC;

public class BlueMessUtil {
    public static BlueReListener callback;
    public static void setCallback(BlueReListener blueListener){
        callback=blueListener;
    }
    public static void toSend(int i){
        if(callback!=null) {
           switch (i){
               case 1:
                   callback.startBooking();
                   break;
               case 2:
                   callback.unclok();
                   break;
           }
        }
    }
    public static ReDownZSListener reDownZSListener;
    public static void setgotoDown(ReDownZSListener reDownZSListeners){
        reDownZSListener=reDownZSListeners;
    }
    public static void gotoDown(){
        reDownZSListener.reDownZS();
    }
    public interface ReDownZSListener{
        void reDownZS();
    }
    public static List<byte[]> listBytesRenz=new ArrayList();
    public static List<byte[]> listBytesRenzSplit=new ArrayList();//RSA分段加密后保存
    public static List<byte[]> listAccept=new ArrayList<>();

    public static short getUint8(short s){
        return (short) (s & 0x00ff);
    }
    public static void getAck(byte[] value2,BleService mBleService) {
        byte[] ack=new byte[5];
        ack[0]= (byte) 0xFF;
        ack[1]= (byte) 0xA5;
        ack[2]=value2[2];
        ack[3]=0x00;
        ack[4]=value2[2];
        try {
            Thread.sleep(30);
            mBleService.sendOrders(ack,ack[2]);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("发送异常"+e);
        }
    }
    //将后16未去掉,重组后解密
    public static String getAESDe(List<byte[]> listAES) {
        //1获取字节总长度
        int lengs=0;
        for(int n=0;n<listAES.size();n++) {
            lengs+=listAES.get(n).length;
        }
        MLog.e("AES有效数据位总长度:"+lengs);
        //2合并数组成一个新的数组
        byte[] aeshe=new byte[lengs];
        int hh=0;
        for(int i=0;i<listAES.size();i++){
            for(int j=0;j<listAES.get(i).length;j++) {
                aeshe[hh]=listAES.get(i)[j];
                hh++;
            }
        }
        //2去掉后16位
        byte[] aesend=new byte[aeshe.length-16];
        for(int p=0;p<aeshe.length-16;p++){
            aesend[p]=aeshe[p];
        }

        //3AES解密
        AES aes = new AES();
        // 解密方法
        byte[] dec = aes.decrypt(aesend,aes.key0.getBytes());
        String json="";
        try {
            json=new String(dec,"UTF-8");
            Log.e("tag","aes解密后的内容：" + json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json;
    }

    //证书编号认证流程
    public static void renOnef(JniHelper jniHelper, Context mContext, final BleService mBleService) {//认证第一步的发送指令
        /**
         * A,数据源
         */
        new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"21");
        String zsNumber= null;
        try {
            zsNumber = new String(jniHelper.getCsrNumber(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS),"UTF-8");
            MLog.e("zsNumber:"+zsNumber);
           // zsNumber=com.nevs.car.tools.encrypt.Base64.encode(zsNumber.getBytes());
        } catch (Exception e) {
           MLog.e("zsNumber获取异常"+e);
        }
        String jsonr=HashmapTojson.getJson(
                new String[]{"id","t","q"},
                new Object[]{12345678,"AuthReconn",new BookingCertSNBean(zsNumber)});
        MLog.e("renzJSON_JD-->"+jsonr);


        listBytesRenzSplit.clear();
        listAccept.clear();

        /**
         * B,分段加密
         */
        byte[] mRestart= new byte[0];//1 json转化为byte数组
        try {
            mRestart = jsonr.getBytes("UTF-8");
            MLog.e("mRestart长度:"+mRestart.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //按180个分成一组加密
          byte[][] bytesR=null;
        bytesR = split_bytes(mRestart,180);
        MLog.e("分段长度："+bytesR.length);

        for(int i=0;i<bytesR.length;i++){
            listBytesRenzSplit.add(getRsa(bytesR[i],mContext,jniHelper));
        }
        MLog.e("listBytesRenzSplit长度："+listBytesRenzSplit.size());


        /**
         * C,分段加密后合并成新大数组
         */
        int ii=0;
        byte[] bytehe=new byte[(listBytesRenzSplit.size())*256];
        for(int i=0;i<listBytesRenzSplit.size();i++){
            for(int j=0;j<256;j++){
                bytehe[ii]=listBytesRenzSplit.get(i)[j];
                ii++;
            }
        }
        MLog.e("bytehe长度："+bytehe.length);
        MLog.e("---------------------------");


        /**
         * D,按120有效数据位分成新数组
         */

        byte[][] bytesRSplit = split_bytes(bytehe,80);//120分包大小
        MLog.e("bytesRSplit.length:分包个数："+bytesRSplit.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        MLog.e(bytesRSplit[0][0]+"");

        /**
         * E,计算效验和并重新组包后放入集合保存
         */
        initCRCRenOnef(bytesRSplit);

//        Looper.prepare();
//        ToastUtil.showToast(mContext,getResources().getString(R.string.rzstart));
//        Looper.loop();
        /**
         * F,开始分包发送
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //cz时间250
                    Thread.sleep(250);
                   // startrenzheng= MyUtils.timeStampNow();
                    MLog.e("开始时间："+ HashmapTojson.getDateToString(MyUtils.timeStampNow()*1000,"yyyy-MM-dd HH:mm:ss")  );
                    mBleService.sendOrders(listBytesRenz.get(0), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }
        }).start();
    }
    public static byte[] getRsa(byte[] byteSplit,Context mContext,JniHelper jniHelper) {//RSA加密
        String s=MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBKey;
       // String ddd= com.nevs.car.jnihelp.StringUtils.getAssetsCacheFile(mContext,"zxc41.pem");

        Log.e("tag","SD内置路径："+MyUtils.getStoragePath(mContext,false));
       Log.e("tag","pubkey："+s);
        byte ou[]=new byte[256];


        Log.e("tag","结果："+jniHelper.RSA(s,byteSplit,byteSplit.length,ou,256)+"");

        Log.e("tag",ou[0]+"tttttt");

        Log.e("tag",ou[0]+" ouchangde0");
        Log.e("tag","结果0："+ Base64.encodeToString(ou,Base64.NO_WRAP));
        Log.e("tag","结果1："+ com.nevs.car.tools.encrypt.Base64.encode(ou));

        //CV
        String base64= com.nevs.car.tools.util.Base64.encode(ou);
        Log.e("tag","结果2："+  com.nevs.car.jnihelp.StringUtils.bytesToHexString(ou));

        Log.e("tag","地址："+ou);

        return ou;

    }
    public static void initCRCRenOnef(byte[][] bytesRSplit) {

        listBytesRenz.clear();
        byte[] byteSend = new byte[0];
        //vv一
        for (int i=0;i<bytesRSplit.length;i++) {
            byte[] byted = new byte[bytesRSplit[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum=0;
            for (int j=0;j<bytesRSplit[i].length;j++){
                sum+=bytesRSplit[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);

            if(i==bytesRSplit.length-1){
                CRC= (short) ((-1)+bytesRSplit[i].length+sum);
            }

//            MLog.e("crcshort:"+CRC);
//            MLog.e("uint8crc:"+getUint8(CRC));
//            MLog.e("uint8crct:"+getUint8((short) 300));
//            MLog.e("CRC:"+(byte)CRC+"  "+i);
            //^^

            byteSend[0]= (byte) 0xFF;
            byteSend[1]= (byte) 0xA5;
            byteSend[2]= (byte) ((i+1) & 0xFF);

            if(i==bytesRSplit.length-1){
                byteSend[2]= (byte)0xFF;
            }

            MLog.e("byteSend[2]"+byteSend[2]);
            byteSend[3]= (byte) (byted.length & 0xFF);
            for(int k=4;k<byteSend.length-1;k++){
                byteSend[k]= bytesRSplit[i][k-4];
            }
            byteSend[byteSend.length-1]= (byte) (getUint8(CRC)&0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listBytesRenz.add(byteSend);
        }
        //^^一
        MLog.e("包的个数："+listBytesRenz.size());
    }

    public static void getRsaDe(Context mContext,JniHelper jniHelper) {//RSA分段解密
        //将集合里面的有效数据位的数组拿出来组成新数组
        int leng=0;
        for(int e=0;e<listAccept.size();e++){
            leng+=listAccept.get(e).length;
        }
        MLog.e("有效数据位的总长度"+leng);
        int kk=0;
        byte[] Acceptdersaend=new byte[leng];
        for (int i=0;i<listAccept.size();i++){
            for(int j=0;j<listAccept.get(i).length;j++){
                Acceptdersaend[kk]=listAccept.get(i)[j];
                kk++;
            }
        }
        MLog.e("Acceptdersaend字节的长度："+Acceptdersaend.length);
        MLog.e("base64jieguo:"+ com.nevs.car.tools.encrypt.Base64.encode(Acceptdersaend));


        //分割数组
        //按256个分成一组
        byte[][] bytesRSplitren = split_bytes(Acceptdersaend,256);
        MLog.e("bytesRSplit.length:分包个数："+bytesRSplitren.length);

        MLog.e("\r\n\r\n==========================================\r\n");


        //解密后合成
        List<byte[]> list=new ArrayList();
        try {
            for(int k=0;k<bytesRSplitren.length;k++){
                list.add(rsaDe(bytesRSplitren[k],mContext,jniHelper));
            }
        }catch (Exception e){
            MLog.e("jiemfail");
            return;
        }


        int lengs=0;
        for(int z=0;z<list.size();z++){
            lengs+=list.get(z).length;
        }


        int ii=0;
        byte[] bytehej=new byte[lengs];
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).length;j++){
                bytehej[ii]=list.get(i)[j];
                ii++;
            }
        }
        MLog.e("bytehe字节长度："+bytehej.length);
        MLog.e("---------------------------");

        String s= null;
        String ErrInf="";


//        {//TU->MD response booking reconnect when success
//“id”:12345678,//echo of the received one
//“t”:”BookingReconnect”,//echo of the received one
//“r”:{//respond parameters
//“ErrCode”:0,//error code
//“ErrInf”:”Succeed”//error information
//        }
//        }

        //{"id":12345678,"t":"BookingReconn","r":{"ErrCode":0,"ErrInf":"Succeed"}}
        try {
            s = new String(bytehej,"UTF-8");
            MLog.e("认证解密后的数据_JD："+s);
            JSONObject jsonObject = new JSONObject(s);
            JSONObject r = jsonObject.getJSONObject("r");
            ErrInf=r.getString("ErrInf");
            int ErrCode=r.getInt("ErrCode");
            if(ErrCode!=0){//重新下载证书
                ActivityUtil.showUiLongToast(MyApp.getInstance().getResources().getString(R.string.refailrz));
                gotoDown();
            }
            //CZTJ

            MLog.e("ErrInf:"+ErrInf);
           if(ErrInf.equals("Succeed")){
               ToastUtil.showLongToast(mContext,mContext.getResources().getString(R.string.n_renzsucc));
               new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCONNING, "1");//0为进行中


               //重连成功 1
               new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISRECONINGSUC,"1");

               //开始Start Booking/UNLOCK
               if(new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISSTARTBOOKING,"0").equals("0")){//是否走过startbooking 0没有走
                   MLog.e("startbooking()流程");

                 //cc603  toSend(1);
               }else {
                   MLog.e("unlock()流程");
                 //cc603  toSend(2);


               }
           }else {

           }

               //CV  startrenz2(s);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("JD认证解密后的数据异常");
            ActivityUtil.showUiLongToast(MyApp.getInstance().getResources().getString(R.string.refailrz));
//            gotoDown();
//            new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCONNING, "0");//0为进行中


        }

    }
    public static byte[] rsaDe(byte[] bb,Context mContext,JniHelper jniHelper){//RSA解密
        //String ddd="/storage/emulated/0/data/mdPri.pem";
        //String ddd=com.nevs.car.jnihelp.StringUtils.getAssetsCacheFile(mContext,"mdPri.pem");
        String ddd=MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPRIKey;

        Log.e("tag","JM内置路径："+ddd);

        //String base64="EA4gKZ20Zd+JiQ007Ci8+IT2yG6SoHCEJ0o1A3ok8OVnYf0qn8mNemNVx8orO6ChqzgH6ljT5FZii2kxSRZicoikLWArnVbH+KF5xAr35yTuNsp76lbP+t+kfvVyADpUqHDcA2rSM0FslfCRNXrtOS2+ijn/NZ8lL6BWcX6fBHvBJKhXytfdjnawOrWBM1SQHMGlMRfKoThk4m6xOWgVhmarKvQ/fLnzIEQQb769tWh+MGLt9LCUn8Q7KEh1b2YcXr3q5pkOxmckJU25XOHvSc9cWkzWUh1ebbMLSWb4xq+EuqRW/07KmNLvIYAwpX0GRzZAIxBXFovhodSq+rJNzw==";

        // String base64="hvV4/CDgayHENyqSeiqH6euFeEMfaBEuDq/gmBKhu7lW5hT2l/X5EpVnIRptV6rtdpKv/lPJGDx3Zn8jpQBeJ4jTYc7l+bbA0RiDGbOXpxvQ8Dr5RCbxBI8DUgoYvfId4YNKUosvtzqUv31En93CyJIAp0BjHF9RwmQgPXgLnHrQS3JXRnDf2L9NGgt5xoC7Dzj7JZnb83aqKl6XvgWrlkk3X5wVVo7FPctHat2dt1atFS2QczE71GQqpEanOaSPeR/V0ZcLETdVZo9aYjZUP2n49M2F+GMBzIIUFf/qwucMjfgBQApgsRJKGGPBHO4d8orAyz0ZLF2ElSG2Dhs+Jw==";
//        byte[] dede=new byte[256];
//        for(int h=0;h<bb.length;h++){
//            dede[h]=bb[h];
//        }
        int delen = 256;
        byte de[]=new byte[256];
        byte []dejni=null;
        try {
            int aaa=jniHelper.RSADECO(ddd,bb,bb.length,de,delen);
            Log.e("tag","长度："+aaa);
            Log.e("tag",de[0]+"解密0");

            dejni=new byte[aaa];
            for(int i=0;i<aaa;i++){
                dejni[i]=de[i];
            }

            String res = new String(dejni,"UTF-8");
            Log.e("tag","解密结果："+res);
            Log.e("tag","delen"+aaa);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return dejni;
    }

    public static void getCrcRenzOnej(byte[] value2,BleService mBleService,Context mContext,JniHelper jniHelper) {
        //计算效验和vv
        short sum=0;//有效数据位和
        for (int j=4;j<value2.length-1;j++){
            sum+=value2[j];
        }
        short CRCs= (short) (value2[2]+value2[3]+sum);
        byte CRCb=(byte) (getUint8(CRCs)&0xFF);
        MLog.e("crcshort:"+CRC);
        if(CRCb==value2[value2.length-1]){
            MLog.e("效验成功");

            byte []bytey =new byte[value2.length-5];
            int ii=0;
            for(int k=4;k<value2.length-1;k++){
                bytey[ii]=value2[k];
                ii++;
            }
            listAccept.add(bytey);
//            try {
//                byte[] ack=getAck(value2);
//                Thread.sleep(30);
//                mBleService.sendOrders(ack,ack[2]);
//            } catch (Exception e) {
//                e.printStackTrace();
//                MLog.e("发送异常"+e);
//            }
            getAck(value2,mBleService);
            if(value2[2]==-1){
                MLog.e("认证接收的包的个数："+listAccept.size());

                getRsaDe(mContext,jniHelper);//rsa分段解密

            }else {

            }

        }else {
            MLog.e("效验失败");
        }
    }
}
