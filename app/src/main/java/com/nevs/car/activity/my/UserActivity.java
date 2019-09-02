package com.nevs.car.activity.my;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.UserInfoBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.BitmapUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhotoUtils;
import com.nevs.car.tools.util.ToastUtils;
import com.nevs.car.tools.util.ZhengZeUtils;
import com.nevs.car.tools.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

import static com.nevs.car.activity.UploadPictureActivity.hasSdcard;
import static com.tamic.novate.config.ConfigLoader.getContext;

public class UserActivity extends BaseActivity {

    @BindView(R.id.image_icon)
    CircleImageView imageIcon;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    @BindView(R.id.edit_phone)
    TextView editPhone;
    @BindView(R.id.edit_alias)
    EditText editAlias;
    @BindView(R.id.edit_family)
    EditText editFamily;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.choose_sex)
    TextView chooseSex;
    @BindView(R.id.edit_emai)
    EditText editEmai;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private String[] mStringItems = {"男","女"};
    private String[] mStringItemsEn = {"Man","Woman"};
    private SharedPHelper sharedPHelper;
    private String sex = "";
    private boolean lang=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_user;
    }


    @Override
    public void init(Bundle savedInstanceState) {
        //  MyUtils.setPadding(nView,mContext);
        sharedPHelper = new SharedPHelper(this);
        lang= MyUtils.getLanguage(mContext);
        inittelephone();

    }

    private void inittelephone() {
        editFamily.setEnabled(false);
        editName.setEnabled(false);
        // M = 男  W = 女
        if (sharedPHelper.get(Constant.LOGINNAME, "").toString().length() != 0) {
            editPhone.setText(dosubtext(String.valueOf(sharedPHelper.get(Constant.LOGINNAME, ""))));
        }
        editAlias.setText(new SharedPHelper(this).get(Constant.NAMES, "").toString());
        editFamily.setText(new SharedPHelper(this).get(Constant.LOGINFAMILYNAME, "").toString());
        editName.setText(new SharedPHelper(this).get(Constant.LOGINGIVENNAMME, "").toString());
        editEmai.setText(new SharedPHelper(this).get(Constant.LOGINEMAIL, "").toString());
        sex = new SharedPHelper(this).get(Constant.LOGINSEX, "").toString();
        if (sex.equals("M")) {
            chooseSex.setText(getResources().getString(R.string.nevs_boy));
        } else if (sex.equals("W")) {
            chooseSex.setText(getResources().getString(R.string.nevs_girl));
        } else {
            chooseSex.setText(getResources().getString(R.string.sex_no));
        }
        if (BitmapUtil.getBitmapFromLocal(Constant.ICONBITMAPNAME + sharedPHelper.get(Constant.LOGINNAME, "")) != null) {
            imageIcon.setImageBitmap(BitmapUtil.getBitmapFromLocal(Constant.ICONBITMAPNAME + sharedPHelper.get(Constant.LOGINNAME, "")));
        } else {

            if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("M")) {//男
                imageIcon.setBackgroundResource(R.mipmap.n_my_default);
            } else if (new SharedPHelper(getContext()).get(Constant.LOGINSEX, "").toString().equals("W")) {
                imageIcon.setBackgroundResource(R.mipmap.n_my_default);
            } else {
                imageIcon.setBackgroundResource(R.mipmap.n_my_default);
            }

        }
    }

    private String dosubtext(String str) {
        //字符串截取
        String bb = str.substring(3, 7);
        //字符串替换
        String cc = str.replace(bb, "****");
        return cc;
    }

    @OnClick({R.id.back, R.id.submit, R.id.image_icon,
            R.id.rel_sex, R.id.edit_emai, R.id.edit_alias, R.id.edit_family, R.id.edit_name})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                if (editAlias.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_alias));
                } else if (editFamily.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_family));
                } else if (editName.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_name));
                } else {
                    if (editEmai.getText().toString().trim().length() == 0) {
                        getHttp();
                    } else {
                        if (ZhengZeUtils.isEmail(editEmai.getText().toString().trim())) {
                            getHttp();
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_email));
                        }
                    }

                }
                break;
            case R.id.image_icon://设置头像
                showDia();
                break;
            case R.id.rel_sex:
                if(lang){
                    showSex();
                }else {
                    showSexEn();
                }

                break;
            case R.id.edit_emai:
                editEmai.setCursorVisible(true);
                break;
            case R.id.edit_alias:
                editAlias.setCursorVisible(true);
                break;
            case R.id.edit_family:
                editFamily.setCursorVisible(true);
                break;
            case R.id.edit_name:
                editName.setCursorVisible(true);
                break;
        }
    }

    private void showSex() {
        final NormalListDialog dialog = new NormalListDialog(UserActivity.this, mStringItems);
        dialog.title(getResources().getString(R.string.toast_choose))//
                .layoutAnimation(null)
                .titleBgColor(getResources().getColor(R.color.n_D1B48B))
                .show(R.style.myDialogAnim);
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseSex.setText(mStringItems[position]);
                if (mStringItems[position].equals("男")) {
                    sex = "M";
                } else {
                    sex = "W";
                }
                dialog.dismiss();
            }
        });
    }
    private void showSexEn() {
        final NormalListDialog dialog = new NormalListDialog(UserActivity.this, mStringItemsEn);
        dialog.title(getResources().getString(R.string.toast_choose))//
                .layoutAnimation(null)
                .titleBgColor(getResources().getColor(R.color.n_D1B48B))
                .show(R.style.myDialogAnim);
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseSex.setText(mStringItemsEn[position]);
                if (mStringItemsEn[position].equals("Man")) {
                    sex = "M";
                } else {
                    sex = "W";
                }
                dialog.dismiss();
            }
        });
    }
    private void showDia() {
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
     * 参数名称	类型	是否必填	说明
     * accessToken	字符串	是	访问令牌
     * ucode	字符串	是	用户CODE
     * mobile	字符串	是	用户手机
     * name	字符串		用户昵称
     * familyname	字符串		姓
     * givenname	字符串		名
     * email	字符串		邮箱
     * sex	字符串		"性别：
     * 未知  None
     * 男    M
     * 女    W"
     * picpath	字符串		头像图片路径
     */

//    private void getHttp() {
//        DialogUtils.loading(mContext,true);
//        HttpRxUtils.getUpdateUserInfo(UserActivity.this,
//                new String[]{"accessToken","userCenterAccessToken","ucode", "mobile", "name", "familyname", "givenname", "email", "sex", "picpath"},
//                new Object[]{new SharedPHelper(UserActivity.this).get(Constant.ACCESSTOKEN, ""),
//                        String.valueOf(sharedPHelper.get(Constant.LOGINNAME, "")),
//                        String.valueOf(sharedPHelper.get(Constant.LOGINNAME, "")),
//                        editAlias.getText().toString().trim(),
//                        editFamily.getText().toString().trim(),
//                        editName.getText().toString().trim(),
//                        editEmai.getText().toString().trim(),
//                        chooseSex.getText().toString().trim(),
//                        "/storage/emulate/0/DCIM/Camera/IMG_20180303_11.jpg"
//                },
//                new HttpRxListener() {
//                    @Override
//                    public void onSucc(Object s) {
//                        DialogUtils.hidding((Activity) mContext);
//                        ActivityUtil.showToast(UserActivity.this, getResources().getString(R.string.toast_submitsuccess));
//                        initshare();
//                        finish();
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        DialogUtils.hidding((Activity) mContext);
//                        switch (str) {
//                            case Constant.HTTP.HTTPFAIL:
//                                ActivityUtil.showToast(UserActivity.this, getResources().getString(R.string.toast_network));
//                                break;
//                            default:
//                                ActivityUtil.showToast(UserActivity.this, str);
//                        }
//                    }
//                }
//        );
//    }
    private void getHttp() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUpdateUserInfo(UserActivity.this,
                new String[]{"accessToken", "noticeType", "userInfo"},
                new Object[]{
                        new SharedPHelper(UserActivity.this).get(Constant.REGISTCENACCESSTOKEN, ""),
                        "EMail",
                        new UserInfoBean(editAlias.getText().toString().trim(),
                                editFamily.getText().toString().trim(),
                                editName.getText().toString().trim(),
                                sex,
                                editEmai.getText().toString().trim()
                        )
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(UserActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        initshare();
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(UserActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(UserActivity.this, str);
                        }
                    }
                }
        );
    }

    private void finishSelect() {
        Intent data = new Intent();
        setResult(1002, data);
        finish();
    }

    private void initshare() {
        new SharedPHelper(this).put(Constant.NAMES, editAlias.getText().toString().trim());
        new SharedPHelper(this).put(Constant.LOGINFAMILYNAME, editFamily.getText().toString().trim());
        new SharedPHelper(this).put(Constant.LOGINGIVENNAMME, editName.getText().toString().trim());
        new SharedPHelper(this).put(Constant.LOGINEMAIL, editEmai.getText().toString().trim());
        if (chooseSex.getText().toString().trim().equals(getResources().getString(R.string.nevs_boy))) {
            new SharedPHelper(this).put(Constant.LOGINSEX, "M");
        } else {
            new SharedPHelper(this).put(Constant.LOGINSEX, "W");
        }

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
                    imageUri = FileProvider.getUriForFile(UserActivity.this, "com.nevs.car.fileprovider", fileUri);
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
                            imageUri = FileProvider.getUriForFile(UserActivity.this, "com.nevs.car.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
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
                    if (bitmap != null) {
                        showImages(bitmap);
                    }
                    break;
                default:
            }
        }
    }

    private void showImages(Bitmap bitmap) {
        imageIcon.setImageBitmap(bitmap);
        BitmapUtil.saveBitmapToLocal(Constant.ICONBITMAPNAME + sharedPHelper.get(Constant.LOGINNAME, ""), bitmap);
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

    public void saveMyBitmap(Bitmap mBitmap, String bitName) {
        File f = new File("/sdcard/Note/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
