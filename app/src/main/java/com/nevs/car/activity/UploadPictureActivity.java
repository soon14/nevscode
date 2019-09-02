package com.nevs.car.activity;

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

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.PaPxoyBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhotoUtils;
import com.nevs.car.tools.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UploadPictureActivity extends BaseActivity {


    @BindView(R.id.imagez)
    ImageView imagez;
    @BindView(R.id.imagef)
    ImageView imagef;
    @BindView(R.id.imageg)
    ImageView imageg;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private int show = 0;
    private List<PaPxoyBean> list = new ArrayList<>();
    private List<File> listFile = new ArrayList<>();
    private List<File> listFile1 = new ArrayList<>();
    private List<File> listFile2 = new ArrayList<>();
    private List<File> listFile3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_upload_picture;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        list.addAll((List<PaPxoyBean>) getIntent().getSerializableExtra("papoxyList"));
        MLog.e("传递的list" + list.size() + "值：" + list.get(0).getOrgCode());
        ActivityManager.addActivity(UploadPictureActivity.this);
    }

    @OnClick({R.id.imagez_one, R.id.imagef_one, R.id.imageg_one, R.id.confirm,
            R.id.imagez_two, R.id.imagef_two, R.id.back
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.imagez_one:
                show = 1;
                showDialog();
                break;
            case R.id.imagef_one:
//                if(listFile.size()<1){
//                    ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_up));
//                }else {
//                    show=2;
//                    showDialog();
//                }
                show = 2;
                showDialog();
                break;
            case R.id.imageg_one:
//                if(listFile.size()<2){
//                    ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_up));
//                }else {
//                    show=3;
//                    showDialog();
//                }
                show = 3;
                showDialog();
                break;
            case R.id.imagez_two:
                show = 1;
                showDialog();
                break;
            case R.id.imagef_two:
//                if(listFile.size()<1){
//                    ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_up));
//                }else {
//                    show=2;
//                    showDialog();
//                }
                show = 2;
                showDialog();
                break;
            case R.id.confirm:
                listFile.clear();
                if (listFile1.size() == 1 && listFile2.size() == 1 && listFile3.size() == 1) {
                    listFile.add(listFile1.get(0));
                    listFile.add(listFile2.get(0));
                    listFile.add(listFile3.get(0));
                    MLog.e("listFile长度：" + listFile.size());
                    getPaoroxy();
                } else {
                    ActivityUtil.showToast(UploadPictureActivity.this, getResources().getString(R.string.upall));
                    // DialogUtils.NormalDialogOneBtn(UploadPictureActivity.this);
                }

                break;
        }
    }

    private void showImages(Bitmap bitmap, File file) {
        switch (show) {
            case 1:
                imagez.setImageBitmap(bitmap);
                listFile1.add(0, file);
                break;
            case 2:
                imagef.setImageBitmap(bitmap);
                listFile2.add(0, file);
                break;
            case 3:
                imageg.setImageBitmap(bitmap);
                listFile3.add(0, file);
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
                    imageUri = FileProvider.getUriForFile(UploadPictureActivity.this, "com.nevs.car.fileprovider", fileUri);
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
                            imageUri = FileProvider.getUriForFile(UploadPictureActivity.this, "com.nevs.car.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
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
                    //bitmap转URI
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                    File file = new File(getRealFilePath(UploadPictureActivity.this, uri));
                    MLog.e("bitmap:" + bitmap.toString());
                    MLog.e("uri:" + uri.toString());
                    MLog.e("1:" + file.getName() + " " + file.toString());
                    if (bitmap != null) {
                        showImages(bitmap, file);
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


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void getPaoroxy() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getPaProxy(UploadPictureActivity.this,
                // M = 男  W = 女
                new String[]{"sex", "local", "address", "certification", "orgCode", "accessToken"},
                new Object[]{list.get(0).getSex(), list.get(0).getLocal(), list.get(0).getAddress(),
                        list.get(0).getCertification(), list.get(0).getOrgCode(), list.get(0).getAccessToken()},
                listFile,//list.get(0).getOrgCode()
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        new SharedPHelper(mContext).put(Constant.LOGINISPA, "Und");
                        DialogUtils.NormalDialogOneBtn(UploadPictureActivity.this);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(UploadPictureActivity.this, getResources().getString(R.string.toast_please_fail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;

                            default:
                                ActivityUtil.showToast(UploadPictureActivity.this, str);
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
