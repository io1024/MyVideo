package com.zero.video.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.zero.video.R;
import com.zero.video.bean.MediaVideoBean;
import com.zero.video.utils.StringUtils;

/**
 * 视频 条目适配器
 * newView：并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
 * bindView：从代码中可以看出在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用
 * adapter.changeCursor(cursor)，它的功能类似于adapter.notifyDataSetChanged()方法
 */
public class VideoAdapter extends CursorAdapter {

    private Context context;

    public VideoAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.adapter_item_video, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tvName);
            holder.tvTime = view.findViewById(R.id.tvTime);
            holder.tvSize = view.findViewById(R.id.tvSize);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        MediaVideoBean videoBean = MediaVideoBean.getMediaVideoBean(cursor);
        holder.tvName.setText(videoBean.getName());
        holder.tvSize.setText(Formatter.formatFileSize(context, videoBean.getFileSize()));
        holder.tvTime.setText(StringUtils.parseTime(videoBean.getDuration()));
        return view;
    }


    private class ViewHolder {
        TextView tvName, tvTime, tvSize;
    }
}
