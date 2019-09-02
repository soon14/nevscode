package com.nevs.car.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.superfileview.LoadFileModel;
import com.nevs.car.tools.superfileview.SuperFileView2;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 加载网络pdf链接
 */
public class CarDescripActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    @BindView(R.id.mSuperFileView2)
    SuperFileView2 mSuperFileView2;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String filePath;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    private String name = "";
    private String abfile = "";
    private String url = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_descrip2;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initPFD();
        try {
            isShowPdf();
        } catch (Exception e) {
            MLog.e(e + "yiccc");
        }
    }

    private void isShowPdf() throws Exception {
        abfile = Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.MYNEVSCARPDF + getFileName(url);
        File file = new File(abfile);
        if (file.exists()) {
            showNewpdf(abfile);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.hidding((Activity) mContext);
                }
            }, 500);

        }else {
            ActivityUtil.showLongToast(mContext,getResources().getString(R.string.n_manual));
        }
    }

    private void initPFD() {
        DialogUtils.loading(mContext, true);
//        mSuperFileView.setOnGetFilePathListener(new SuperFileView2.OnGetFilePathListener() {
//            @Override
//            public void onGetFilePath(SuperFileView2 mSuperFileView2) {
//                getFilePathAndShowFile(mSuperFileView2);
//            }
//        });

        Intent intent = this.getIntent();
        url = intent.getStringExtra("url");
        String names = intent.getStringExtra("name");
        if (names != null) {
            name = names;
        }

        if (!TextUtils.isEmpty(url)) {
            MLog.e("文件path:" + url);
            setFilePath(url);
        }
        getFilePathAndShowFile();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.e("FileDisplayActivity-->onDestroy");
//        if (mSuperFileView != null) {
//            mSuperFileView.onStopDisplay();
//        }
    }

    @OnClick({R.id.back, R.id.tv_title})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
        }
    }

    private void getFilePathAndShowFile() {


        if (getFilePath().contains("http")) {//网络地址要先下载

            downLoadFromNet(getFilePath(), mSuperFileView2);

        } else {
            mSuperFileView2.displayFile(new File(getFilePath()));
        }
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url, final SuperFileView2 mSuperFileView2) {

        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                MLog.e("删除空文件！！");
                cacheFile.delete();
                return;
            }
        }


        LoadFileModel.loadPdfFile(url, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MLog.e("下载文件-->onResponse");
                boolean flag;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody responseBody = response.body();
                    is = responseBody.byteStream();
                    long total = responseBody.contentLength();

                    File file1 = getCacheDir(url);
                    if (!file1.exists()) {
                        file1.mkdirs();
                        MLog.e("创建缓存目录： " + file1.toString());
                    }


                    //fileN : /storage/emulated/0/pdf/kauibao20170821040512.pdf
                    File fileN = getCacheFile(url);//new File(getCacheDir(url), getFileName(url))

                    MLog.e("创建缓存文件： " + fileN.toString());
                    if (!fileN.exists()) {
                        boolean mkdir = fileN.createNewFile();
                    }
                    fos = new FileOutputStream(fileN);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        MLog.e("写入缓存文件" + fileN.getName() + "进度: " + progress);
//                        if (progress == 1) {
//                            ActivityUtil.showToast(mContext, getResources().getString(R.string.downpdfhint));
//                        }
                    }
                    fos.flush();
                    MLog.e("文件下载成功,准备展示文件。");
                    DialogUtils.hidding(CarDescripActivity.this);//ccccc
                    showNewpdf(abfile);


                    //2.ACache记录文件的有效期
                    mSuperFileView2.displayFile(fileN);
                } catch (Exception e) {
                    MLog.e("文件下载异常 = " + e.toString());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                MLog.e("文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    MLog.e("删除下载失败文件");
                    file.delete();
                }
            }
        });


    }

    /***
     * 获取缓存目录
     *
     * @param url
     * @return
     */
    private File getCacheDir(String url) {

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.MYNEVSCARPDF);

    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    //"/007/"
    private File getCacheFile(String url) {
        File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.MYNEVSCARPDF
                + getFileName(url));
        MLog.e("缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
//cc        String fileName = Md5Tool.hashKey(url) + "." + getFileType(url);
//        return fileName;
        String fileName = name + "." + getFileType(url);
        return fileName;
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            MLog.e("paramString---->null");
            return str;
        }
        MLog.e("paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            MLog.e("i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        MLog.e("paramString.substring(i + 1)------>" + str);
        return str;
    }

    //   "/storage/emulated/0/Android/data/com.nevs.car/pdfs/yhscpdfch.pdf"
    private void showNewpdf(String abfilePath) {
        pdfView.fromFile(new File(abfilePath))
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .load();

//        pdfView.fromFile(new File(abfilePath))   //横滚
//                .swipeHorizontal(true)
//                .pageSnap(true)
//                .autoSpacing(true)
//                .pageFling(true)
//                .load();
    }

    private void showNewpdf0(String abfilePath) {
        pdfView.fromFile(new File(abfilePath))
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }
}
