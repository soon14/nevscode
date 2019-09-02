package com.nevs.car.tools.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.nevs.car.R;
import com.nevs.car.adapter.xrefreshview.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2018/7/3.
 */

public class BitmapUtil {
    //文件保存的路径
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/comcn.cn.img/pics201807030909";

    /**
     * 向本地SD卡写网络图片
     *
     * @param bitmap
     */
    public static void saveBitmapToLocal(String fileName, Bitmap bitmap) {
        try {
            // 创建文件流，指向该路径，文件名叫做fileName
            File file = new File(FILE_PATH, fileName);
            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }
            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地SD卡获取缓存的bitmap
     */
    public static Bitmap getBitmapFromLocal(String fileName) {
        try {
            File file = new File(FILE_PATH, fileName);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
                        file));
                //    String ss= String.valueOf(bitmap.getConfig());
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 字符串保存到手机内存设备中
     *
     * @param str
     */
    public static void saveFile(String str, String fileName) {
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            // 如果文件不存在
            if (file.exists()) {
                // 创建新的空文件
                file.delete();
            }
            file.createNewFile();
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(str.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除已存储的文件
     */
    public static void deletefile(String fileName) {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(FILE_PATH, fileName);
            file.delete();


//
////获取SD卡路径
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/cache/pics201807030909";
//                File file = new File(path);
//            MLog.e("0");
//                if (file.exists()) {//如果路径存在
//                    MLog.e("1");
//                    if (file.isDirectory()) {//如果是文件夹
//                        File[] childFiles = file.listFiles();//获取文件夹下所有文件
//                        if (childFiles == null || childFiles.length == 0) {//如果为空文件夹
//                            file.delete();//删除文件夹
//                            MLog.e("2");
//                            return;
//                        }
//                        for (int i = 0; i < childFiles.length; i++) {//删除文件夹下所有文件
//                            childFiles[i].delete();
//                        }
//                        file.delete();//删除文件夹
//                        MLog.e("3");
//                    }
//                }

        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("异常");
        }
    }

    /**
     * 读取文件里面的内容
     *
     * @return
     */
    public static String getFile(String fileName) {
        try {
            // 创建文件
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            // 创建FileInputStream对象
            FileInputStream fis = new FileInputStream(file);
            // 创建字节数组 每次缓冲1M
            byte[] b = new byte[1024];
            int len = 0;// 一次读取1024字节大小，没有数据后返回-1.
            // 创建ByteArrayOutputStream对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 一次读取1024个字节，然后往字符输出流中写读取的字节数
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            // 将读取的字节总数生成字节数组
            byte[] data = baos.toByteArray();
            // 关闭字节输出流
            baos.close();
            // 关闭文件输入流
            fis.close();
            // 返回字符串对象
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        如果想保存数据，调用方法：
//
//        String save_data = "this is saved data!";
//        // 保存数据到txt文件中
//        FileUtil.saveFile(save_data, "save.txt");// 保存为了一个txt文本
//        1
//        2
//        3
//        如果想读取数据，调用方法：
//
//// 读取保存的文件数据
//        String data = FileUtil.getFile("save.txt");
//        1
//        2
//        如果想清除缓存，或者注销登录后删除保存的文件，调用方法:
//
//// 删除保存的文件
//        FileUtil.deletefile("save.txt");
    }


//    /**
//     * @param bmp 获取的bitmap数据
//     */
//    public static void saveImageToGallery(Context context, Bitmap bmp) {
//        long time = System.currentTimeMillis();
//        String fileName = time + ".jpg";
//        for (long key : tmpMap.keySet()) {
//            if (time - key > 3 * 1000) {
//                tmpMap.remove(key);
//            }
//        }
//
//        saveImageToGallery(context, bmp, fileName);
//        MLog.e("===>1");
//        //系统相册目录
//        String galleryPath = Environment.getExternalStorageDirectory()
//                + File.separator + Environment.DIRECTORY_DCIM
//                + File.separator + "Camera" + File.separator;
//        File dir = new File(galleryPath);
//        if (!dir.exists() && !dir.isDirectory()) {
//            MLog.e(dir.exists() + "==>" + dir.isDirectory());
//            return;
//        }
//        delFile(galleryPath);
//        MLog.e("===>2");
//        // 声明文件对象
//        File file = null;
//        // 声明输出流
//        FileOutputStream outStream = null;
//        try {
//            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
//            file = new File(galleryPath, fileName);
//            // 获得文件相对路径
//            fileName = file.toString();
//            // 获得输出流，如果文件中有内容，追加内容
//            outStream = new FileOutputStream(fileName);
//            MLog.e("===>3");
//            if (null != outStream) {
//                MLog.e("===>4");
//                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
//            }
//        } catch (Exception e) {
//            e.getStackTrace();
//        } finally {
//            try {
//                if (outStream != null) {
//                    outStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        context.sendBroadcast(intent);
//        tmpMap.put(time, fileName);
//
//
//    }


    /**
     * 保存资源文件中的图片到本地相册,并实时刷新的实现方法!
     * Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_public_no);
     */
    private static Map<Long, String> tmpMap = new LinkedHashMap<>();
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        long time = System.currentTimeMillis();
        String fileName = time + ".jpg";
        for (long key : tmpMap.keySet()) {
            if (time - key > 3 * 1000) {
                tmpMap.remove(key);
            }
        }

        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        delFile(Environment.getExternalStorageDirectory() + File.separator + "Boohee");
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            sendAlbum(context,file);
            tmpMap.put(time, fileName);
            ActivityUtil.showToast(context, context.getResources().getString(R.string.save_album));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendAlbum(Context ctx, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        ctx.sendBroadcast(intent);
    }

    private static boolean delFile(String fileDir) {
        boolean flag = false;
        File appDir = new File(fileDir);
        if (!appDir.exists() || !appDir.isDirectory()) {
            return flag;
        }
        for (Long key : tmpMap.keySet()) {
            String name = tmpMap.get(key);
            File temp = new File(fileDir + File.separator + name);
            if (temp.isFile()) temp.delete();
        }

//        String[] imgs = appDir.list();
//        List<String> tmp= new ArrayList<>();
//        long filelng= Utils.toLong(fileName.split("\\.")[0]);
//        for(int i=0 ;i<imgs.length;i++){
//            long fileTime= Utils.toLong(imgs[i]);
//            if(filelng-fileTime<3*1000L){
//                tmp.add(imgs[i]);
//            }
//        }
//        for (int i = 0; i < tmp.size(); i++) {
//            File temp = new File(fileDir + File.separator + tmp.get(i));
//            if (temp.isFile())  temp.delete();
//
//
//        }
        return flag;
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
