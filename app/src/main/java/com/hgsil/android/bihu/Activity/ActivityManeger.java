package com.hgsil.android.bihu.Activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class ActivityManeger {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    public static void finishAllExceptSelf(Activity mActivity){
        for (Activity activity:activities){
            if (!mActivity.equals(activity)){
                if (!activity.isFinishing()){
                    activity.finish();
                }
            }
        }
    }
}
