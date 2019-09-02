package com.nevs.car.tools.util;

public class ClickUtil {
        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private static final int MIN_CLICK_DELAY_TIME0 = 500;
        private static long lastClickTime0=0;

        public static boolean isFastClick0() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime0) >= MIN_CLICK_DELAY_TIME0) {
                flag = true;
            }
            lastClickTime0 = curClickTime;
            return flag;
        }

    private static long lastClickTime=0;
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 500) {
            MLog.e("快速点击");
            return false;
        } else {
            MLog.e("正常点击");
            lastClickTime = time;
            return true;
        }
    }

//    private static final int MIN_CLICK_DELAY_TIME0 = 400;
//    private static long lastClickTime0;
//    public static boolean isFastClick0() {
//        boolean flag = false;
//        long curClickTime = System.currentTimeMillis();
//        if ((curClickTime - lastClickTime0) >= MIN_CLICK_DELAY_TIME0) {
//            flag = true;
//        }
//        lastClickTime0 = curClickTime;
//        return flag;
//    }
/**
 * btn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
if (Utils.isFastClick()) {
// 进行点击事件后的逻辑操作
}
}
});
 */

}
