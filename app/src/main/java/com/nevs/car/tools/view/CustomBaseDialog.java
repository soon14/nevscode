package com.nevs.car.tools.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.flyco.animation.Attention.Swing;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.nevs.car.R;
import com.nevs.car.tools.util.ActivityUtil;

/**
 * Created by mac on 2018/5/3.
 */

public class CustomBaseDialog extends BottomBaseDialog {
    private RatingBar ratingBar;
    private EditText editText;
    private TextView textView;
    private String ra=null;
    private String []dia=new String[2];

    public CustomBaseDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new Swing());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(getContext(), R.layout.dialog_custom_base, null);
        ratingBar= (RatingBar) inflate.findViewById(R.id.rabar);
        editText= (EditText) inflate.findViewById(R.id.edcontent);
        textView= (TextView) inflate.findViewById(R.id.submit);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

        return inflate;
    }



    @Override
    public void setUiBeforShow() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ra==null){
                    ActivityUtil.showToast(getContext(),"您还没有评分");
                }else if (editText.getText().toString().trim().length()<=0){
                    ActivityUtil.showToast(getContext(),"您还没有写评价");
                }else if(ra==null&&editText.getText().toString().trim().length()<=0){
                    ActivityUtil.showToast(getContext(),"您还没有评分");
                }else {

                }
                }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ra=Float.toString(rating);//5.0
            }
        });
    }
}