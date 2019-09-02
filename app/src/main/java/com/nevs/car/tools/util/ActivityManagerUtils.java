package com.nevs.car.tools.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhanghao1 TODO Activity收集以及释放
 */
public class ActivityManagerUtils {

	public List<Activity> activityList = new ArrayList<Activity>();

	private static ActivityManagerUtils activityManagerUtils;

	private ActivityManagerUtils() {

	}

	public static ActivityManagerUtils getInstance() {
		if (null == activityManagerUtils) {
			activityManagerUtils = new ActivityManagerUtils();
		}
		return activityManagerUtils;
	}

	/**
	 *
	 * 获取栈顶的activity
	 * 
	 * @return
	 */
	public Activity getTopActivity() {
		return activityList.get(activityList.size() - 1);
	}

	/**
	 * 添加一个activity到栈底
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 当整个程序完全退出之后，移除所有的activity
	 */
	public void removeAllActivity() {
		if (activityList.size() > 0) {
			for (Activity activity : activityList) {
				if (null != activity) {
					if (!activity.isFinishing()) {
						activity.finish();
					}
					activity = null;
				}
			}
			activityList.clear();
		}
	}
	
}
