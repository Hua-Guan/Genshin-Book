package xyz.genshin.itismyduty.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

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
