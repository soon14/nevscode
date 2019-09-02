package com.nevs.car.activity.my;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhotoUtils;
import com.nevs.car.tools.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tamic.novate.config.ConfigLoader.getContext;

public class MyDriveActivity extends BaseActivity {

    @BindView(R.id.imagez)
    ImageView imagez;
    @BindView(R.id.imagef)
    ImageView imagef;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    @BindView(R.id.imagez_one)
    ImageView imagezOne;
    @BindView(R.id.imagez_two)
    TextView imagezTwo;
    @BindView(R.id.imagef_one)
    ImageView imagefOne;
    @BindView(R.id.imagef_two)
    TextView imagefTwo;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private int show = 0;
    private List<File> listFile = new ArrayList<>();
    private File file1, file2;
    private List<String> listDown = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_drive;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        getHttpDown();
    }

    @OnClick({R.id.back, R.id.imagez, R.id.imagef, R.id.confirm, R.id.imagez_one,
            R.id.imagez_two, R.id.imagef_one, R.id.imagef_two
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.imagez:
                show = 1;
                showDialog();
                break;
            case R.id.imagez_one:
                show = 1;
                showDialog();
                break;
            case R.id.imagez_two:
                show = 1;
                showDialog();
                break;
            case R.id.imagef:
                if (listFile.size() != 0) {
                    show = 2;
                    showDialog();
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_zheng));
                }

                break;
            case R.id.imagef_one:
                if (listFile.size() != 0) {
                    show = 2;
                    showDialog();
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_zheng));
                }

                break;
            case R.id.imagef_two:
                if (listFile.size() != 0) {
                    show = 2;
                    showDialog();
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_zheng));
                }

                break;
            case R.id.confirm:
                MLog.e("listFile长度：" + listFile.size());
                if (listFile.size() == 2) {
                    getHttp();
                } else {
                    ActivityUtil.showToast(MyDriveActivity.this, getResources().getString(R.string.toast_twopictures));
                }
                break;
        }
    }

    private void showDialog() {//动画效果
        final String[] stringItems = {getResources().getString(R.string.photo),
                getResources().getString(R.string.choose_pictures)
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.isTitleShow(false)
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                    case 0:
                        //拍照
                        MLog.e("拍照");
                        autoObtainCameraPermission();
                        break;
                    case 1:
                        //从相册中选择
                        MLog.e("从相册中选择");
                        autoObtainStoragePermission();
                        break;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, getResources().getString(R.string.toast_rejected));
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    imageUri = FileProvider.getUriForFile(MyDriveActivity.this, "com.nevs.car.fileprovider", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, getResources().getString(R.string.toast_nosd));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            imageUri = FileProvider.getUriForFile(MyDriveActivity.this, "com.nevs.car.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(this, getResources().getString(R.string.toast_nosd));
                    }
                } else {

                    ToastUtils.showShort(this, getResources().getString(R.string.toast_opencamera));
                }
                break;


            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {

                    ToastUtils.showShort(this, getResources().getString(R.string.toast_opensd));
                }
                break;
            default:
        }
    }

    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照完成回调
                case CODE_CAMERA_REQUEST:
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    break;
                //访问相册完成回调
                case CODE_GALLERY_REQUEST:
                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            newUri = FileProvider.getUriForFile(this, "com.nevs.car.fileprovider", new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    } else {
                        ToastUtils.showShort(this, getResources().getString(R.string.toast_nosd));
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                    if (bitmap != null) {
                        showImages(bitmap, uri);
                    }
                    break;
                default:
            }
        }
    }


    /**
     * 自动获取sdk权限
     */

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }

    private void showImages(Bitmap bitmap, Uri uri) {
        switch (show) {
            case 1:
                imagez.setImageBitmap(bitmap);
                file1 = new File(getRealFilePath(MyDriveActivity.this, uri));
                listFile.add(0, file1);
                MLog.e("1:" + uri.toString());
                break;
            case 2:
                imagef.setImageBitmap(bitmap);
                file2 = new File(getRealFilePath(MyDriveActivity.this, uri));
                listFile.add(1, file2);
                MLog.e("2:" + uri.toString());
                break;
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void getHttpDown() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getDriverManageQuery(getContext(),
                new String[]{"accessToken", "ucode"},
                new Object[]{new SharedPHelper(this).get(Constant.ACCESSTOKEN, ""),
                        new SharedPHelper(this).get(Constant.LOGINNAME, "")
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding((Activity) mContext);
                        listDown.addAll((Collection<? extends String>) list);
                        if (listDown.size() != 0) {
                            imagefOne.setVisibility(View.GONE);
                            imagefTwo.setVisibility(View.GONE);
                            imagezOne.setVisibility(View.GONE);
                            imagezTwo.setVisibility(View.GONE);
                            imagez.setClickable(true);
                            imagef.setClickable(true);
                        }
                        Glide.with(getContext())
                                .load(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listDown.get(0))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                                .skipMemoryCache(true)//跳过内存缓存
                                .into(imagez);
                        Glide.with(getContext())
                                .load(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listDown.get(1))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
                                .skipMemoryCache(true)//跳过内存缓存
                                .into(imagef);

                    }

                    @Override
                    public void onFial(String str) {
                        imagez.setClickable(false);
                        imagef.setClickable(false);
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void getHttp() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getDriverManage(MyDriveActivity.this,
                new String[]{"accessToken", "ucode"},
                new Object[]{new SharedPHelper(this).get(Constant.ACCESSTOKEN, ""),
                        new SharedPHelper(this).get(Constant.LOGINNAME, "")
                },
                listFile,
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(MyDriveActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        finish();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_submitfail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }

                    }
                }
        );

    }

    public String getRealFilePath(final Context context, final Uri uri) {//uri转路径
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
