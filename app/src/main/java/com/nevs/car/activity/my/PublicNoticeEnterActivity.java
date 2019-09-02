package com.nevs.car.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicNoticeEnterActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.n_view)
    RelativeLayout nView;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_public_notice_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initView();
    }

    private void initView() {
        if (getIntent().getStringExtra("title") != null) {
            title.setText(getIntent().getStringExtra("title"));
            content.setText(getIntent().getStringExtra("content"));
            time.setText(getIntent().getStringExtra("time"));
            tvTitle.setText(getIntent().getStringExtra("bigtitle"));
        }
        if (getIntent().getStringExtra("timez") != null) {
            // time.setText(HashmapTojson.getTimez(getIntent().getStringExtra("time"),"yyyy/MM/dd HH:mm:ss"));
            time.setText(HashmapTojson.getDateToString(Long.parseLong(getIntent().getStringExtra("time")) * 1000, "yyyy/MM/dd HH:mm:ss"));
            try {
//                JSONObject jsonObject=new JSONObject(getIntent().getStringExtra("content")+"");
//                title.setText(jsonObject.getString("title"));
//                String description=jsonObject.getString("description");
//                JSONObject jsonObject1=new JSONObject(description);
//                JSONObject jsonObject2=jsonObject1.getJSONObject("aps");
//                String description1=jsonObject2.getString("alert");
//                content.setText(description1);
//                JSONObject jsonObject=new JSONObject(getIntent().getStringExtra("content")+"");
//                title.setText(jsonObject.getString("title"));
//                content.setText(jsonObject.getString("description"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
}
