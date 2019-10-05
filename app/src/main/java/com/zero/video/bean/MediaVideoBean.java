package com.zero.video.bean;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Video.Media;

/**
 * 视频实体
 */
public class MediaVideoBean implements Parcelable {

    private String name;
    private String path;
    private int duration;
    private long fileSize;

    public MediaVideoBean() {
    }

    /**
     * @param cursor 游标
     * @return 获取：通过 Cursor 查询 媒体文件的实体
     */
    public static MediaVideoBean getMediaVideoBean(Cursor cursor) {
        MediaVideoBean bean = new MediaVideoBean();
        bean.name = cursor.getString(cursor.getColumnIndex(Media.TITLE));//名字
        bean.path = cursor.getString(cursor.getColumnIndex(Media.DATA));//路径
        bean.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));//时长
        bean.fileSize = cursor.getLong(cursor.getColumnIndex(Media.SIZE));//大小
        return bean;
    }

    protected MediaVideoBean(Parcel in) {
        name = in.readString();
        path = in.readString();
        duration = in.readInt();
        fileSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeInt(duration);
        dest.writeLong(fileSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaVideoBean> CREATOR = new Creator<MediaVideoBean>() {
        @Override
        public MediaVideoBean createFromParcel(Parcel in) {
            return new MediaVideoBean(in);
        }

        @Override
        public MediaVideoBean[] newArray(int size) {
            return new MediaVideoBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
