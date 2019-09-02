package com.nevs.car.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.interfaces.OnCheckedChangeListener;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.view.SwipeLayout;
import com.nevs.car.tools.view.SwitchButton;
import com.nevs.car.z_start.MyApp;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 2018/6/10.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<HashMap<String,Object>> nameList;
    private SwipeLayout preLayout;//记录上一个打开
    private OnCheckedChangeListener listener;

    public SwipeLayout getPreLayout() {
        return preLayout;
    }

    public MyAdapter(Context context, List<HashMap<String,Object>> nameList) {
        this.mContext = context;
        this.nameList = nameList;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onOpen(SwipeLayout layout);

        void onClose(SwipeLayout layout);

        void onSwiping(SwipeLayout layout);

        void onStartOpen(SwipeLayout layout);

        void onStartClose(SwipeLayout layout);

        void onpLacedTop(int position);

        void onNoRead(int position);

        void onDelete(int position);

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my, parent, false);
        return new ViewHolder(view);
    }
//    id_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            if (isChecked) {
//                ActivityUtil.showToast(SettingActivity.this, "推送服务已开启");
//            } else {
////                    JPushInterface.stopPush(getApplicationContext());
//                ActivityUtil.showToast(SettingActivity.this, "推送服务已关闭");
//            }
//        }
//    });
public void addOnCheckedChangeListener(OnCheckedChangeListener listener){
    this.listener = listener;
}
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.data.setText(nameList.get(position).get("runDuration")+"");
////"yyyy-MM-dd HH:mm"   "0 32 12 ? * 4,5,2,"
//        try {
//            String ss=HashmapTojson.getDateToString(Long.parseLong(nameList.get(position).get("scheduleValue").toString())*1000,"HH:mm");
//            MLog.e("ss"+ss);
//            //  holder.time.setText(ss.trim().substring(ss.length()-5));
//            holder.time.setText(ss);
//        }catch (Exception e){
//
//        }
        //"0 33 17 ? * 3,5,1,2,4,6,7,"    "0 33 17 ? * 4,"




     /**   {
            "items": [{
            "scheduleId": "717",
                    "vin": "LTPSBSIMULATOR199",
                    "scheduleType": 1,
                    "scheduleValue": "04/29/2019 9:3:00",
                    "runDuration": 10,
                    "createTime": 1544671615
        }, {
            "scheduleId": "1263",
                    "vin": "LTPSBSIMULATOR199",
                    "scheduleType": 1,
                    "scheduleValue": "04/28/2019 11:37:00",
                    "runDuration": 10,
                    "createTime": 1556336268
        }, {
            "scheduleId": "1264",
                    "vin": "LTPSBSIMULATOR199",
                    "scheduleType": 1,
                    "scheduleValue": "04/29/2019 6:5:00",
                    "runDuration": 10,
                    "createTime": 1556336271
        }],
            "resultMessage": 1000,
                "resultDescription": ""
        }
      **/

        try {

            String scheduleType=nameList.get(position).get("scheduleType")+"";
            MLog.e("scheduleType:"+scheduleType);


            if(scheduleType.equals("1.0")){
                String aa[]=(nameList.get(position).get("scheduleValue")+"").split(" ");
                holder.data.setText(aa[0]);
              //  holder.time.setText(aa[1]);
                String cc[]=aa[1].split(":");
                String cc0=cc[0];
                String cc1=cc[1];
                 if(cc0.length()==2&&Integer.parseInt(cc0)<10){
                    cc0=cc0.substring(1,2);
                }
                if(cc1.length()==2&&Integer.parseInt(cc1)<10){
                    cc1=cc1.substring(1,2);
                }

                String hour=Integer.parseInt(cc0) < 10 ? "0" + cc0 : "" + cc0;
                String minute=Integer.parseInt(cc1) < 10 ? "0" + cc1 : "" + cc1;
              //  holder.time.setText(hour+":"+minute+":"+"00");
//                String hour=Integer.parseInt(cc[0]) < 10 ? cc[0] : "" + cc[0];
//                String minute=Integer.parseInt(cc[1]) < 10 ?  cc[1] : "" + cc[1];
                holder.time.setText(hour+":"+minute+":00");
                MLog.e(position+"获取时间"+aa[0]+aa[1]);
            }else if(scheduleType.equals("3.0")){
                //"0 28 23 ? * 1,2,3,4,5,6,7,"
                String bb[]=(nameList.get(position).get("scheduleValue")+"").split(" ");
                String bbb2=bb[2];
                String bbb1=bb[1];

                if(bbb2.length()==2&&Integer.parseInt(bbb2)<10){
                    bbb2=bbb2.substring(1,2);
                }
                if(bbb1.length()==2&&Integer.parseInt(bbb1)<10){
                    bbb1=bbb1.substring(1,2);
                }

                String bb2=Integer.parseInt(bbb2) < 10 ? "0" + bbb2 : "" + bbb2;
                String bb1=Integer.parseInt(bbb1) < 10 ? "0" + bbb1 : "" + bbb1;
//                String bb2=Integer.parseInt(bb[2]) < 10 ? "" + bb[2] : "" + bb[2];
//                String bb1=Integer.parseInt(bb[1]) < 10 ? "" + bb[1] : "" + bb[1];

                holder.time.setText(bb2+":"+bb1+":00");
                String times=bb[5];
                String timeOnes[]=times.split(",");
                MLog.e(timeOnes.length+"长度");
                if(timeOnes.length==7){
                    holder.data.setText(mContext.getResources().getString(R.string.everyday));
                    MLog.e(timeOnes.length+"长度");
                }else {
                    String ss="";
                    for(int i=0;i<timeOnes.length;i++){
                        MLog.e("测试"+i+":"+timeOnes[i]);
                        switch (timeOnes[i]){
                            case "2":
                                ss+=mContext.getResources().getString(R.string.toast_monday)+" ";//星期一
                                break;
                            case "3":
                                ss+=mContext.getResources().getString(R.string.toast_tuesday)+" ";//星期二
                                break;
                            case "4":
                                ss+=mContext.getResources().getString(R.string.toast_wednesday)+" ";
                                break;
                            case "5":
                                ss+=mContext.getResources().getString(R.string.toast_thursday)+" ";
                                break;
                            case "6":
                                ss+=mContext.getResources().getString(R.string.toast_friday)+" ";
                                break;
                            case "7":
                                ss+=mContext.getResources().getString(R.string.toast_saturday)+" ";
                                break;
                            case "1":
                                ss+=mContext.getResources().getString(R.string.toast_sunday)+" ";//星期天
                                break;
                        }
                    }
                    holder.data.setText(ss);
                    if(ss.equals(MyApp.getInstance().getResources().getString(R.string.toast_monday)+" "+
                            MyApp.getInstance().getResources().getString(R.string.toast_tuesday)+" "+
                            MyApp.getInstance().getResources().getString(R.string.toast_wednesday)+" "+
                            MyApp.getInstance().getResources().getString(R.string.toast_thursday)+" "+
                            MyApp.getInstance().getResources().getString(R.string.toast_friday)+" ")){
                        holder.data.setText(MyApp.getInstance().getResources().getString(R.string.week));
                    }

                }

            }
        }catch (Exception e){
            MLog.e("空调预约列表异常");
        }




//        String aa[]=(nameList.get(position).get("scheduleValue")+"").split(" ");
//        holder.data.setText(aa[0]);
//        holder.time.setText(aa[1]);
//        MLog.e("获取时间"+aa[0]+aa[1]);
        holder.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.OnCheckedChangeListener(buttonView,isChecked);
            }
        });
        holder.swipelayout.setOnSwipeChangeListener(new SwipeLayout.OnSwipeChangeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                preLayout = layout;

                if (onItemClickListener != null) {
                    onItemClickListener.onOpen(layout);
                }
            }

            @Override
            public void onClose(SwipeLayout layout) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClose(layout);
                }
            }

            @Override
            public void onSwiping(SwipeLayout layout) {
                if (onItemClickListener != null) {
                    onItemClickListener.onSwiping(layout);
                }
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                if (preLayout != null) {
                    preLayout.close();
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onStartOpen(layout);
                }
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                if (onItemClickListener != null) {
                    onItemClickListener.onStartClose(layout);
                }
            }
        });
        holder.layoutFront.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        holder.placedTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onpLacedTop(position);
                }
            }
        });
        holder.noRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onNoRead(position);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onDelete(position);
                }
            }
        });
        holder.layoutFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return nameList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.placed_top)
        TextView placedTop;
        @BindView(R.id.no_read)
        TextView noRead;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.layout_back)
        LinearLayout layoutBack;
        @BindView(R.id.item_time)
        TextView time;
        @BindView(R.id.item_date)
        TextView data;
        @BindView(R.id.id_switchd)
        SwitchButton switchButton;
        @BindView(R.id.layout_front)
        RelativeLayout layoutFront;
        @BindView(R.id.swipelayout)
        SwipeLayout swipelayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}