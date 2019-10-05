package com.zero.video.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zero.video.R;
import com.zero.video.activity.VideoPlayActivity;
import com.zero.video.activity.VitamioPlayActivity;
import com.zero.video.adapter.VideoAdapter;
import com.zero.video.bean.MediaVideoBean;
import com.zero.video.database.MyQueryHandler;

import java.util.ArrayList;

/**
 * 本地视频界面
 * 视频需要导包：import android.provider.MediaStore.Video.Media;
 * 音频需要导包：import android.provider.MediaStore.Audio.Media;
 */
public class LocalFragment extends BaseFragment {

    private ListView lvVideo;
    private VideoAdapter videoAdapter;

    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_video, null);
        lvVideo = view.findViewById(R.id.lvVideo);
        return view;
    }

    @Override
    public void initData() {
        //内容解析解
        ContentResolver resolver = getActivity().getContentResolver();
        //获取视频的指定数据（注意：：Media._ID 该条目必须查询(不然会抛出错误)）
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.TITLE, Media.SIZE, Media.DURATION, Media.DATA},
//                null, null, null);
//        CursorUtils.printCursorVideo(cursor);
        videoAdapter = new VideoAdapter(getActivity(), null);
        lvVideo.setAdapter(videoAdapter);
        //数据库查询属于耗时操作，使用异步查询的方式
        MyQueryHandler queryHandler = new MyQueryHandler(resolver);
        queryHandler.startQuery(100, videoAdapter, Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.TITLE, Media.SIZE, Media.DURATION, Media.DATA}, null, null, null);
    }

    @Override
    public void initListener() {
        lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取游标，并把游标移动到点击的条目对应的位置上
                Cursor cursor = videoAdapter.getCursor();
//                cursor.moveToPosition(position);
//                //把游标指向的条目内容转化为实体对象
//                MediaVideoBean videoBean = MediaVideoBean.getMediaVideoBean(cursor);
                ArrayList<MediaVideoBean> videoBeans = getItemsFromCursor(cursor);
                //点击跳转播放界面
                //Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                //使用 Vitamio 视频框架的界面
                Intent intent = new Intent(getActivity(), VitamioPlayActivity.class);
                //intent.putExtra("item_video", videoBean);
                Bundle bundle = new Bundle();
                bundle.putInt("item_video_pos", position);
                bundle.putParcelableArrayList("item_video_all", videoBeans);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * @param cursor 游标
     * @return 获取所有的 视频 数据
     */
    private ArrayList<MediaVideoBean> getItemsFromCursor(Cursor cursor) {
        ArrayList<MediaVideoBean> items = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            MediaVideoBean bean = MediaVideoBean.getMediaVideoBean(cursor);
            items.add(bean);
        }
        return items;
    }

}
