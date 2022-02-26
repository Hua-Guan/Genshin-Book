package xyz.genshin.itismyduty.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @params componentName 查询指定service的组件名；
     * e.g. com.hr.life.trnfa.service.services.MqttConnectService
     *
     * @return boolean 返回该服务是否在运行中；
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean serverIsRunning(Context context, String componentName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices.size() <= 0) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (componentName.equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 秒转化为天小时分秒字符串
     * @param seconds
     * @return String
     */
    public static String formatSeconds(int seconds) {
        String timeStr = "";
        if (seconds < 10) {
            timeStr = "0:0" + seconds;
        }else {
            timeStr = "0:"+seconds;
        }
        if (seconds > 59) {
            int second = seconds % 60;
            int min = seconds / 60;
            if (second < 10){
                timeStr = min + ":0" + second;
            }else {
                timeStr = min + ":" + second;
            }
            if (min > 59) {
                min = (seconds / 60) % 60;
                int hour = (seconds / 60) / 60;
                timeStr = hour + ":" + min + ":" + second;
                if (hour > 23) {
                    hour = ((seconds / 60) / 60) % 24;
                    int day = (((seconds / 60) / 60) / 24);
                    timeStr = day + ":" + hour + ":" + min + ":" + second;
                }
            }
        }
        return timeStr;
    }

}
