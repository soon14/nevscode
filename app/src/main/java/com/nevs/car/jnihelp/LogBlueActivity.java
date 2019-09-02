package com.nevs.car.jnihelp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.z_start.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogBlueActivity extends BaseActivity implements LogBlueLin{
    private BlueBrocastRecever blueBrocastRecever;

    @BindView(R.id.nonono)
    TextView nonono;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_log_blue;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        blueBrocastRecever=BlueBrocastRecever.getInstance();
        BlueBrocastRecever.setLog(this);
        BleService.setLog(this);
        MainActivity.setLog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.clearText, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clearText:
                nonono.setText("");
                break;
            case R.id.back:
                finish();
                break;
        }
    }



    @Override
    public void getLog(int i, String str) {
        try {
            switch (i){
                case 91:
                    nonono.append(str+"\n");
                    break;
                case 92:
                    nonono.append(str+"\n");
                    break;
                case 93:
                    nonono.append(str+"\n");
                    break;
                case 94:
                    nonono.append(str+"\n");
                    break;
                case 95:
                    nonono.append(str+"\n");
                    break;
                case 101:
                    nonono.append(str+"\n");
                    break;
                case 102:
                    nonono.append(str+"\n");
                    break;
                case 103:
                    nonono.append(str+"\n");
                    break;
                case 111:
                    nonono.append(str+"\n");
                    break;
                case 112:
                    nonono.append(str+"\n");
                    break;
            }
        }catch (Exception e){
            MLog.e("yicactivity "+e);
        }

    }
}
