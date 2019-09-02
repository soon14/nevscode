package com.nevs.car.jnihelp;

import android.app.Activity;
import android.content.Context;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.encrypt.Base64;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sun.misc.BASE64Encoder;

/**
 * Created by mac on 2018/9/6.
 */

public class DigitalUtils {
    public static CSRListener csrListener1;
    public static void setCsrListener(CSRListener csrListener){
        csrListener1=csrListener;
    }
    public static void toSend(){
        if(csrListener1!=null){
            csrListener1.postTo();
        }
    }
    private static List<Object> list=new ArrayList<>();
    public static void getApply(final Context mContext,String csr,String bindingId) {//
        MLog.e("---------------------------后台getApply中");
        TspRxUtils.getApply(mContext,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"csr","bindingId"},
                new Object[]{csr,bindingId},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        String commandId= String.valueOf(obj);
                        getDownload(mContext,commandId);
                    }

                    @Override
                    public void onFial(String str) {
                        if (str.contains("400") || str.contains("无效的请求")) {
                          //  ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")||str.contains("无效的网址")) {
                          //  ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.neterror));
                        } else  if(str.contains("未授权的请求")){
                            MyUtils.exitToLongin(mContext);
                        }else if(str.contains("401")){
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                          //  ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.timeout));
                        }
                        else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );

    }


    //-----BEGIN CERTIFICATE-----MIID8TCCAtmgAwIBAgITcAAAAFxL/wdGu/a3qAAAAAAAXDANBgkqhkiG9w0BAQsFADAPMQ0wCwYDVQQDEwRORVZTMB4XDTE4MTIxMjAyMjA0MFoXDTE5MTIxMjAyMzA0MFowSjELMAkGA1UEBhMCQ04xDTALBgNVBAoTBE5FVlMxDTALBgNVBAsTBE5FVlMxHTAbBgNVBAMTFDg5ODYwMzE4MzQyMDAzMjAzMTE4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqK8cs9/DfhVo9Y96MzmlAfO3iQPFTnCS2MeJntY7t8uR7yHEqMe4V3vKWMqHmmVdVNNSlpiH6BSQJ4EPQRe9yFsgakTkfj+5u32/WijT/7dw4alz61mL6vhfGBbq4Ur6kDqOl47lMHyxo4Yej+k4wrBLlqwmMjMPeMPF/Xt7JK/I1bsQ1fR0tt+vp4IZ0Eft/V7TZ6tBv4bbrccGrsnqLtw4Ukv4ThzoQ6M3jzG5C6mGwtzFJP9FT8F5cqpagxFsZrlTKPUdLHxvRJEMnuiH8vjrOh4sGZ+9TS55Pn2tHny2oSLl30CCysb9qi66tyZ+ZmsbrzWfHEeEqIXMfvT7XQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFCfAg1gkUTU5SQMnIXAI6l+FgqIYMB8GA1UdIwQYMBaAFNgVNKguzPzi25FeE/FYUTqQHx15MDYGA1UdHwQvMC0wK6ApoCeGJWZpbGU6Ly8vL0NBU2VydmVyL0NlcnRFbnJvbGwvTkVWUy5jcmwwSgYIKwYBBQUHAQEEPjA8MDoGCCsGAQUFBzAChi5maWxlOi8vLy9DQVNlcnZlci9DZXJ0RW5yb2xsL0NBU2VydmVyX05FVlMuY3J0MD8GCSsGAQQBgjcUAgQyHjAASQBQAFMARQBDAEkAbgB0AGUAcgBtAGUAZABpAGEAdABlAE8AZgBmAGwAaQBuAGUwDQYJKoZIhvcNAQELBQADggEBAGgXwhdgLsA/tPvl8IaqLn6ReHyBKhSC584UVg9zTeXV9cLBYrmWJ3DWQDL+ylHszmT7f1FSPdVtA1cPjevVadIzIefXqkIRa5S2drTz6XSgrsgaGmmoK5DwbffPrKZZBHk7cOx/Xi9Qo/o3hKTyKNkGKKBlYTGb7ioEk9xmHwR+alc2Id/5oLnDll7hdGNzenxFpzSqIqzVcDgN2oPMWpyMpXtfWbdhLX6zzJKRHto9lmUfZHlA2Lwo5aLiHNjMo7Wz8RSBkOzJbqe54CUM+jbEX5UVY1bq2iNLgBU3wWkjecyPO2XODUTq+aoxn4QVv7GtFQFXiHX5mfsePoIQOP4=-----END CERTIFICATE-----
    public static void getDownload(final Context mContext, String commandId){
        MLog.e("---------------------------后台getDownload中");
        list.clear();
        TspRxUtils.getDownload(mContext,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                commandId,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MLog.e("digikey下载成功");
                        //通知activiti开始连蓝牙
                        toSend();
                        list.addAll((Collection<?>) obj);
//                        1 -----BEGIN CERTIFICATE-----
//                                2 //证书内容略
//                        3 -----END CERTIFICATE-----
                        if(list.size()==0){
                            return;
                        }
                        final String zs="-----BEGIN CERTIFICATE-----\n"+list.get(1)+"\n-----END CERTIFICATE-----";
                        String zs1="-----BEGIN CERTIFICATE-----\n" +
                                "MIIGnDCCBYSgAwIBAgIBDDANBgkqhkiG9w0BAQsFADB7MQswCQYDVQQGEwJDTjEL\n" +
                                "MAkGA1UECAwCR0QxCzAJBgNVBAcMAkhaMQswCQYDVQQKDAJTVjEMMAoGA1UECwwD\n" +
                                "SURBMQ8wDQYDVQQDDAZqYXNwZXIxJjAkBgkqhkiG9w0BCQEWF3poYW5nempAc29s\n" +
                                "b21vLWluZm8uY29tMB4XDTE4MDkyMTEwMjU1NFoXDTE5MDkyMTEwMjU1NFowYjEL\n" +
                                "MAkGA1UEBhMCQ04xCzAJBgNVBAgMAkdEMQswCQYDVQQKDAJTVjEMMAoGA1UECwwD\n" +
                                "SURBMQ8wDQYDVQQDDAZqYXNwZXIxGjAYBgkqhkiG9w0BCQEWCzEyM0AxNjMuY29t\n" +
                                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn8/F1qdWPlmaqSWuyeCN\n" +
                                "klgfgnPmDEJLpZLZSLu11k31bM1HkNtZY/zogsIZeG8PygEtKh3ihdE6dPP5UZxR\n" +
                                "CS2cY5kD46y+33soF65oQmSD9AhImOPKagb7W46S1hhwhwiEUu8LO8LX1FcDGw+o\n" +
                                "JiusRAB0ZeAe0fiLrsYhcNrd9fxc5dgLXeFwTatCTSKGS4wSCSGrjR4mWQwSOsdS\n" +
                                "xF2JHueNQuwKzhfd6CYCVdor1OjXnpqCy4r3ZAglr3IltaJi+FkfM9YBkciWMJHM\n" +
                                "RAcWIHk/PC7e+qJWCFNF4K/2QhpnF8N+VFO57+ZBXEKEvJE+q/Tob0Rt1hhW+i3w\n" +
                                "6QIDAQABo4IDQjCCAz4wJQYIKgMEBQGD/3oEGQwXIGJsZSBtYWMgYWRkclttYWMg\n" +
                                "YWRkcl0wLAYIKgMEBQGD/3sEIAweIHZlaGljbGUgaWRbQUFBQUFBQUFBQTEyMzQ1\n" +
                                "NjddMCIGCCoDBAUBg/98BBYMFCB1c3IgbmFtZVt4aWFvIG1pbmddMC4GCCoDBAUB\n" +
                                "g/99BCIMICBzdGFydCB0aW1lWzIwMTgtMDUtMDcgMTU6MDA6MDBdMCwGCCoDBAUB\n" +
                                "g/9+BCAMHiBlbmQgdGltZVsyMDE5LTA1LTA3IDE1OjAwOjAwXTAfBggqAwQFAYP/\n" +
                                "fwQTDBEgcGluIGNvZGVbMjM0NTY3XTCCAeYGCCoDBAUBhIAABIIB2AyCAdQgdGJv\n" +
                                "eCBwdWJsaWMga2V5Wy0tLS0tQkVHSU4gUFVCTElDIEtFWS0tLS0tCk1JSUJJakFO\n" +
                                "QmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBcjVXMWJ3WGJKRVVn\n" +
                                "UHg1UmZFVkMKd2RtdEo1amJHQ2FPcXFoeXdJQTR5MFF3U0NWeU5GRGNuRFN6TXo3\n" +
                                "cS9vZ2MyZzJSQXZUYmFwbmFOaU85RjNGVApBcXNrVE0vNVBLR3EwcFRNTHVYV3pT\n" +
                                "YzA4VFora1M4ckFwMmhLS0g3RnVqWlNZZWg0QWYvOUtaeC9aeDBNbFBmCmF5NzdH\n" +
                                "T2tHWlQ4RVlCaHEyRHEzcTMzaU5JV09CQjdiWVVKbkZUMjhGaTQ4NkVDa0dzV0dI\n" +
                                "OG5aVCtzSlZDN0kKOElYblY3WFNucmZzVjFTLzFoeklHZ3ZjQlpyTitGK3pOUUQ3\n" +
                                "bnRSVTgrZHk2NjFUdFBTSzNQdi9mVHV4UTMvSgpnVUltRzZ1Y3hsTFlLdzdaVjVk\n" +
                                "SkFqdDk4eHhsY3NmNkJvYVEvQ2xnUHV5cGRkNHU2UDJudFBvMmp5L2dESGtYCjB3\n" +
                                "SURBUUFCCi0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLV0wHgYIKgMEBQGEgAEEEgwQ\n" +
                                "IHVzciByb2xlW293bmVyXTAeBggqAwQFAYSAAgQSDBAgYm9va2luZyBpZFswMDFd\n" +
                                "MBoGCCoDBAUBhIADBA4MDCB1c3IgaWRbMDAxXTANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
                                "d5OjSmJcf7KwmVvGY+RpemUH24xXYdjzleROaqjK3IoLS+duwM1NHlbfrDZ6PUt3\n" +
                                "9ozCPHYz8g7KhhwCPqefvcFaNlZh2lKR8HLTwqgZ3rY6jfVHDe1Zg01XrSENIe4m\n" +
                                "2dsQDv5dpB+KuhehUhxMT05rowh8J2hmAJ7cGfYJ44qLaq4bWpDgNjOWddZDCj3V\n" +
                                "xDL1c+xYJwXrbnmHPnrUvckDEsfnHdXLBNwpx4bDn7B+KMOeQE97D8CL4CUsKt+P\n" +
                                "0y7Qjlabt7wU/BF8oWmOHLIrlH1MUueA/o8V/XiTqowRBa5m+PbdPBPVuX1thJ2e\n" +
                                "rs8i/avVlp8Ec7pD7MxePw==\n" +
                                "-----END CERTIFICATE-----\n";


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    //解密证书
                                    String fileName=Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBZS;
                                    MyUtils.initData(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR,fileName,zs);
                                    String pubKey="-----BEGIN PUBLIC KEY-----\n"+getKeyFromCRT(mContext,MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+fileName)+"\n-----END PUBLIC KEY-----";
                                    MLog.e("pubKey回调"+pubKey);
                                    // 存入文件
                                    MyUtils.initData(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBKey,pubKey);
                                    MyUtils.initData(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS,"-----BEGIN CERTIFICATE-----\n"+list.get(2)+"\n-----END CERTIFICATE-----");
                                    }catch (Exception e){
                                        MLog.e(e+"");
                                    }
                                }
                            }).start();

                            MyUtils.upLogTSO(mContext,"下载DigitalKey",String.valueOf(obj),MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");



                    }
                    @Override
                    public void onFial(String str) {
                        MyUtils.upLogTSO(mContext,"下载DigitalKey",String.valueOf(str),MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");

                        if (str.contains("400") || str.contains("无效的请求")) {
                          //  ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")||str.contains("无效的网址")) {
                          //  ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.neterror));
                        } else  if(str.contains("未授权的请求")){
                           MyUtils.exitToLongin(mContext);
                        }else if(str.contains("401")){
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                           // ActivityUtil.showToast(mContext,mContext.getResources().getString(R.string.timeout));
                        }
                        else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                    }


                }
        );
    }

    /*
  获取公钥key的方法（读取.crt认证文件）
  */
    public static String getKeyFromCRT(Context context,String con){
        String key="";
        CertificateFactory certificatefactory;
        X509Certificate Cert;
        InputStream bais = null;
        PublicKey pk;
        BASE64Encoder bse;
        try{
            //若此处不加参数 "BC" 会报异常：CertificateException - OpenSSLX509CertificateFactory$ParsingException
            certificatefactory=CertificateFactory.getInstance("X.509","BC");
            //读取放在项目中assets文件夹下的.crt文件；你可以读取绝对路径文件下的crt，返回一个InputStream（或其子类）即可。
            // bais = this.getAssets().open("xxx.crt");
            //  bais = context.getAssets().open("zs183.txt");
            // FileInputStream in = new FileInputStream(con);

            // File fin=new File(con);

//            if(Build.VERSION.SDK_INT>=24){
//                try{
//                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
//                    m.invoke(null);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
            //in=new FileInputStream("zx.txt");

//            File file =new File(con);
//            String abpath=file.getAbsolutePath();
//            MLog.e("绝对路径："+abpath);
            //FileInputStream in=context.openFileInput("zx.txt");

            //  FileInputStream in =new FileInputStream(abpath,true);
//            File file=new File(con);
//            FileInputStream inputStream= new FileInputStream(file);

            //   FileInputStream fis=new FileInputStream(con);



//
//            BufferedReader br=new BufferedReader(new InputStreamReader(in));
//            StringBuilder sb=new StringBuilder();
//            String line=null;
//
//            while((line=br.readLine())!=null)
//            {
//                sb.append(line);
//            }
//            MLog.e("读取的内容："+sb.toString());
//            br.close();


//

            File file = new File(con);
            InputStream inStream = new FileInputStream(file);
            Cert = (X509Certificate) certificatefactory.generateCertificate(inStream);
            pk = Cert.getPublicKey();
            bse = new BASE64Encoder();
            key=bse.encode(pk.getEncoded());
            //key = Base64.encode(pk.getEncoded());
//            Log.e("源key-----"+ Cert.getPublicKey());
//            Log.e("加密key-----"+bse.encode(pk.getEncoded()));
            //inStream.close();
        }catch(Exception e){
            MLog.e("yic"+e);
        }
        key=key.replaceAll("\\n", "").trim();//去掉文件中的换行符
        return key;
    }


    public static void getPub(String path){
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
            X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(path));//"my.cer"
            PublicKey publicKey = cert.getPublicKey();
            BASE64Encoder base64Encoder=new BASE64Encoder();
            String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
            MLog.e("-----------------公钥--------------------");
            MLog.e(publicKeyString);
            MLog.e("-----------------公钥--------------------");
        }catch (Exception e){
            MLog.e("读取失败："+e);
        }


    }
//    /*
//获取公钥key的方法（读取.crt认证文件）
//*/
//    private static String getKeyFromCRT(String path){
//        String key="";
//        CertificateFactory certificatefactory;
//        X509Certificate Cert;
//        InputStream bais;
//        PublicKey pk;
//        BASE64Encoder bse;
//        try{
//            //若此处不加参数 "BC" 会报异常：CertificateException - OpenSSLX509CertificateFactory$ParsingException
//            certificatefactory=CertificateFactory.getInstance("X.509","BC");
//            //读取放在项目中assets文件夹下的.crt文件；你可以读取绝对路径文件下的crt，返回一个InputStream（或其子类）即可。
//           // bais = context.getAssets().open("xxx.crt");
//            bais=new FileInputStream(path);
//            Cert = (X509Certificate) certificatefactory.generateCertificate(bais);
//            pk = Cert.getPublicKey();
//            bse = new BASE64Encoder();
//            key=bse.encode(pk.getEncoded());
////            Log.e("源key-----"+ Cert.getPublicKey());
////            Log.e("加密key-----"+bse.encode(pk.getEncoded()));
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        key=key.replaceAll("\\n", "").trim();//去掉文件中的换行符
//        MLog.e("-----------------公钥--------------------");
//        MLog.e(key);
//        MLog.e("-----------------公钥--------------------");
//
//        return key;
//    }
    public static void getP(String path){
        try {
            //java 读取证书(.cer, .crt 其它未验证)
            CertificateFactory certificatefactory=CertificateFactory.getInstance("X.509","BC");
            FileInputStream bais=new FileInputStream(path);
            X509Certificate Cert = (X509Certificate)certificatefactory.generateCertificate(bais);
            PublicKey pk = Cert.getPublicKey();
            BASE64Encoder bse=new BASE64Encoder();
            MLog.e("pk:"+bse.encode(pk.getEncoded()));

        }catch (Exception e){
MLog.e("fdf"+e);
        }

    }
    /**
     * 获取证书公钥
     * @param
     * @return
     */
    public static String getPublicKey(String con){
        String key="";
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
//
           FileInputStream in = new FileInputStream(con);

            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            StringBuilder sb=new StringBuilder();
            String line=null;

            while((line=br.readLine())!=null)
            {
                sb.append(line);
            }
            MLog.e("读取的内容："+sb.toString());
            br.close();




            //生成一个证书对象并使用从输入流 inStream 中读取的数据对它进行初始化。
            Certificate c = cf.generateCertificate(in);
            PublicKey publicKey = c.getPublicKey();
            key = Base64.encode(publicKey.getEncoded());
            MLog.e("pubkey:"+key);
        } catch (Exception e) {
            MLog.e("获取pub异常"+e);
        }
        return key;
    }
/**
     　　* 将一个字符串转化为输入流
     　　*/
    public static InputStream getStringStream(String sInputString){
if (sInputString != null && !sInputString.trim().equals("")){
try{
ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes("UTF-8"));
return tInputStringStream;
}catch (Exception ex){
ex.printStackTrace();
}
}
return null;
    }
//    public static InputStream getInPut(){
//        try{
//            InputStream myIn=new ByteArrayInputStream(txt.getBytes());
////将System.in转化为面向字符的流
//            InputStreamReader ir = new InputStreamReader(myIn);
//
//            in = new BufferedReader(ir);//为输入流提供缓冲区
//
//            while ((s = in.readLine()) != "bye")
//                System.out.println("Read: " + s);
//
//        }
//        catch(IOException e)
//        {System.out.println("Error! ");}
//    }
//    }

//    public static InputStream getIn(String txt){
//        try{
//            InputStream myIn=new ByteArrayInputStream(txt.getBytes());
////将System.in转化为面向字符的流
//            InputStreamReader ir = new InputStreamReader(myIn);
//            BufferedReader in = new BufferedReader(ir);//为输入流提供缓冲区
//            while ((s = in.readLine()) != "bye")
//                System.out.println("Read: " + s);
//        }
//        catch(IOException e)
//        {System.out.println("Error! ");}
//
//    }
    public static String getPublic(String con){
        String res = "";
//       try {
//           InputStream inStream = new FileInputStream(con);
//           ByteArrayOutputStream out = new ByteArrayOutputStream();
//           int ch;
//
//           while ((ch = inStream.read()) != -1)
//           {
//               out.write(ch);
//           }
//           byte[] result = out.toByteArray();
//// res = Base64.byteArrayToBase64(result);
//           res = Base64.encode(result);
//       }catch (Exception e){
//           MLog.e("获取公钥异常"+e);
//       }


        try {
//           URL url = DigitalUtils.class.getClassLoader().getResource(con);   //证书路径
//          MLog.e("公钥所在路径:"+url.getFile());
//           CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
//           X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(url.getFile()));
//           PublicKey publicKey = cert.getPublicKey();
//           BASE64Encoder base64Encoder=new BASE64Encoder();
//           String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
//           System.out.println("-----------------公钥--------------------");
//           System.out.println(publicKeyString);
//           System.out.println("-----------------公钥--------------------");



//           CertificateFactory cf=CertificateFactory.getInstance("X.509");
//           File certFile = new File(con);
//           KeyStore ks = KeyStore.getInstance("JKS");
//           ks.load(new FileInputStream(certFile),pass.toCharArray());
//           Enumeration<String> aliases = ks.aliases();





        }catch (Exception e){
            MLog.e("获取公钥异常"+e);
        }



        return res;
    }


public  static String getFile(String con){
    FileInputStream in = null;
    String conn="";
    try {
        in = new FileInputStream(con);
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        StringBuilder sb=new StringBuilder();
        String line=null;

        while((line=br.readLine())!=null)
        {
            sb.append(line);
        }
        MLog.e("读取的内容："+sb.toString());
        conn=sb.toString();
        br.close();
        in.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

  return conn;

}




    /**
          * 把原始字符串分割成指定长度的字符串列表
          * 
          * @param inputString
          *            原始字符串
          * @param length
          *            指定长度
          * @return
          */
 public static List<String> getStrList(String inputString, int length) {
 int size = inputString.length() / length;
 if (inputString.length() % length != 0) {
 size += 1;
 }
 return getStrList(inputString, length, size);
}


 /**
      * 把原始字符串分割成指定长度的字符串列表
      * 
      * @param inputString
      *            原始字符串
      * @param length
      *            指定长度
      * @param size
      *            指定列表大小
      * @return
      */
         public static List<String> getStrList(String inputString, int length,
 int size) {
 List<String> list = new ArrayList<String>();
for (int index = 0; index < size; index++) {
 String childStr = substring(inputString, index * length,
                    (index + 1) * length);
list.add(childStr);
 }
 return list;
 }


 /**
      * 分割字符串，如果开始位置大于字符串长度，返回空
      * 
      * @param str
      *            原始字符串
      * @param f
      *            开始位置
      * @param t
      *            结束位置
      * @return
      */
       public static String substring(String str, int f, int t) {
if (f > str.length())
 return null;
if (t > str.length()) {
return str.substring(f, str.length());
 } else {
 return str.substring(f, t);
 }
 }


 public static String getPem(String str){
           String pems="";
     List<String> list =  getStrList(str,64);
     for(int i=0;i<list.size();i++) {
         pems+=list.get(i)+"\n";
     }
           return pems;
 }/**
     * 从sd card文件中读取数据
     * @param filename 待读取的sd card
     * @return
     * @throws IOException
     */
    public static String readExternal(Context context, String filename) throws Exception {
        StringBuilder sb = new StringBuilder("");
       //73 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
          //73  filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + filename;
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len));

                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
       //73 }
        return sb.toString();
    }




}
