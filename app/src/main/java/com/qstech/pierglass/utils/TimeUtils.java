package com.qstech.pierglass.utils;

/**
 * Created by admin on 2016/9/27.
 */

public class TimeUtils {
    public static String toString(int time) {
        return ((time/3600 < 10)? ("0"+time/3600) : ("" + time/3600)) +
                ":"+ (((time%3600)/60 < 10)? ("0"+(time%3600)/60) : ("" + (time%3600)/60)) +
                ":"+ (((time%3600)%60 < 10)? ("0"+(time%3600)%60) : ((time%3600)%60));
    }

    public static int getHour(int time) {
        return time/3600;
    }

    public static int getMinutes(int time) {
        return (time%3600)/60;
    }

    public static int getSeconds(int time) {
        return (time%3600)%60;
    }
}
