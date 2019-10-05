package com.zero.video.utils;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class CursorUtils {

    public static void printCursorVideo(Cursor cursor) {
        if (cursor == null) {
            Log.e("Cursor", "Video" + "  无数据  ");
            return;
        }
        Log.e("Cursor", "Video" + " 一共查到 " + cursor.getCount() + " 条数据");
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            Log.e("Cursor", "Video" + MediaStore.Video.Media.TITLE + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
            Log.e("Cursor", "Video" + MediaStore.Video.Media.DURATION + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
            Log.e("Cursor", "Video" + MediaStore.Video.Media.DATA + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            Log.e("Cursor", "Video" + MediaStore.Video.Media.SIZE + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
            Log.e("Cursor", "=========================");

            //2019-10-04 12:12:15.742 12141-12141/com.zero.video E/Cursor: Videotitlewx_camera_1542273160794
            //2019-10-04 12:12:15.742 12141-12141/com.zero.video E/Cursor: Videoduration14706
            //2019-10-04 12:12:15.742 12141-12141/com.zero.video E/Cursor: Video_data/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1542273160794.mp4
            //2019-10-04 12:12:15.742 12141-12141/com.zero.video E/Cursor: Video_size4581059
        }
    }
}
