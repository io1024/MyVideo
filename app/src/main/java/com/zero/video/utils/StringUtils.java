package com.zero.video.utils;

import android.annotation.SuppressLint;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 */
public class StringUtils {

    private static final int Hour = 60 * 60 * 1000;
    private static final int Min = 60 * 1000;
    private static final int Sec = 1000;


    @SuppressLint("DefaultLocale")
    public static String parseTime(int duration) {
        int hour = duration / Hour;
        int min = duration % Hour / Min;
        int sec = duration % Min / Sec;
        if (hour == 0) {
            return String.format("%2d:%2d", min, sec);
        } else {
            return String.format("%2d:%2d:%2d", hour, min, sec);
        }
    }

    /**
     * @return 获取手机当前的时间 格式：HH:mm:ss
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }
}
