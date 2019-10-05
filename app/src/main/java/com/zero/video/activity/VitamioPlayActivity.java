package com.zero.video.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.video.BaseActivity;
import com.zero.video.R;
import com.zero.video.bean.MediaVideoBean;
import com.zero.video.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * 视频播放界面
 * 手机屏幕的 X 轴（宽度）、Y 轴（高度） 会随着Activity界面设置的横屏和竖屏改变。
 * 无论 activity 是竖屏 或 横屏，X 轴是都是指横向，Y 轴是指竖向（屏幕的左上角坐标是:(0,0) ）
 * .
 * 亮度的修改：修改本界面的亮度（直接修改系统亮度体验并不友好）
 * 方案：在播放界面上面覆盖一层 View,根据手势的滑动动态改变View的background
 * .
 * 引入 Vitamio 需要重新导包的类：VideoView、MediaPlayer
 * .
 * 待解决的问题：
 * 1. 存在某些视频无画面（有声音）
 * 2. 当屏幕关闭后，再次点亮屏幕，当前播放的视频会重新开始播放
 * 3. 随便开始一个视频播放，然后下一首/上一首操作 ，然后关闭屏幕再点亮，会返回第一个打开的视频且重新开始播放
 * 4. 横屏竖屏问题（现在是固定的横屏模式）
 */
public class VitamioPlayActivity extends BaseActivity {

    private TextView tvNameTop, tvTimeTop, tvBatteryTop, tvCurrentTime, tvTotalTime, tvGoBack, tvPre, tvPlayPause, tvNext, tvFullScreen;
    private LinearLayout layoutVideoTop, layoutVideoBottom;
    private SeekBar voiceSeekBarTop, pbSeekBarBottom;
    private VideoView videoView;
    private ImageView ivVideoTop;
    private View coverView;
    private final int Msg_Update_System_Time = 100;
    private final int Msg_Update_Playing_Time = 200;
    private final int Msg_Hide_Control_Panel = 300;
    private ArrayList<MediaVideoBean> itemVideoAll; //通过 Cursor 获取到的所有数据
    private MyBroadcastReceiver broadcastReceiver;
    private GestureDetector gestureDetector;
    private AudioManager audioManager;
    private boolean isFullScreen = false;
    private float currentAlpha;
    private float downY;
    private int currentVolume;
    private int maxVolume;
    private int itemVideoPos;//Cursor的当前位置
    private int screenWidth, screenHeight;//屏幕的宽高
    private int videoOriginalWidth, videoOriginalHeight;//视频原始的宽高
    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        //移除消息队列中的所有消息，避免handler引起的内存泄漏
        handler.removeCallbacksAndMessages(null);
        //注销广播接受者
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void initView() {
        //初始化 Vitamio 工程
        //LibsChecker.checkVitamioLibs(this);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_vitamio_play);
        videoView = findViewById(R.id.videoView);
        //系统自带播放控制面板
        //videoView.setMediaController(new MediaController(this ));
        //自定义播放控制
        layoutVideoTop = findViewById(R.id.layoutVideoTop);
        tvNameTop = findViewById(R.id.tvNameTop);//视频名称
        tvBatteryTop = findViewById(R.id.tvBatteryTop);//手机电量 %
        tvTimeTop = findViewById(R.id.tvTimeTop);//手机时间
        ivVideoTop = findViewById(R.id.ivVideoTop);//声音
        voiceSeekBarTop = findViewById(R.id.voiceSeekBarTop);//声音
        layoutVideoBottom = findViewById(R.id.layoutVideoBottom);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);//当前进度
        pbSeekBarBottom = findViewById(R.id.pbSeekBarBottom);//进度条
        tvTotalTime = findViewById(R.id.tvTotalTime);//总时长
        tvGoBack = findViewById(R.id.tvGoBack);//返回
        tvPre = findViewById(R.id.tvPre);//上一个
        tvPlayPause = findViewById(R.id.tvPlayPause);//播放、暂停
        tvNext = findViewById(R.id.tvNext);//下一个
        tvFullScreen = findViewById(R.id.tvFullScreen);//全屏
        coverView = findViewById(R.id.coverView);//屏幕亮度控制
    }

    @Override
    public void initData() {
        //获取屏幕的宽\高
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        Log.e("屏幕", "宽度-X轴=" + screenWidth + "高度-Y轴=" + screenHeight);

        //注册电池电量变化的广播接受者
        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, filter);

        //判断跳转来源
        Uri uri = getIntent().getData();
        if (uri != null) {
            //从应用外部调用
            videoView.setVideoURI(uri);
            String uriPath = uri.getPath();
            tvNameTop.setText(uriPath);
//        String httpUrl = getIntent().getStringExtra("httpUrl");
//        if (!TextUtils.isEmpty(httpUrl)) {
//            videoView.setVideoURI(Uri.parse(httpUrl));
////            videoView.setVideoPath(httpUrl);
//            videoView.requestFocus();
        } else {
            //mediaVideoBean = getIntent().getParcelableExtra("item_video");
            itemVideoAll = getIntent().getExtras().getParcelableArrayList("item_video_all");
            itemVideoPos = getIntent().getExtras().getInt("item_video_pos");
            if (itemVideoAll == null || itemVideoAll.size() == 0 || itemVideoAll.size() <= itemVideoPos) {
                finish();
                return;
            }
            MediaVideoBean mediaVideoBean = itemVideoAll.get(itemVideoPos);
            if (mediaVideoBean != null) {
                initPlayer(mediaVideoBean);
            }
        }

        //获取音量（AudioManager）
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取最大音量（媒体音量）
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取当前音量
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        voiceSeekBarTop.setMax(maxVolume);
        voiceSeekBarTop.setProgress(currentVolume);
        //获取定义的亮度
        currentAlpha = coverView.getAlpha();
        //主动测量自定的控制面板,并获取测量结果,进行设置
        layoutVideoTop.measure(0, 0);
        int topHeight = layoutVideoTop.getMeasuredHeight();
        layoutVideoTop.setTranslationY(-topHeight);//向上偏移
        layoutVideoBottom.measure(0, 0);
        int bottomHeight = layoutVideoBottom.getMeasuredHeight();
        layoutVideoBottom.setTranslationY(bottomHeight);//向下偏移
    }

    private void initPlayer(MediaVideoBean videoBean) {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        //videoView.setVideoURI();
        videoView.setVideoPath(videoBean.getPath());
        tvNameTop.setText(videoBean.getName());
        //设置总时长
        tvTotalTime.setText(StringUtils.parseTime(videoBean.getDuration()));
        pbSeekBarBottom.setMax(videoBean.getDuration());
    }

    @Override
    public void initListener() {
        ivVideoTop.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);
        tvPre.setOnClickListener(this);
        tvPlayPause.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        tvFullScreen.setOnClickListener(this);
        //VideoView准备完成的监听
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.setZOrderMediaOverlay(true);
                //设置总时长
                tvTotalTime.setText(StringUtils.parseTime((int) mp.getDuration()));
                pbSeekBarBottom.setMax((int) mp.getDuration());
                videoView.start();
                updateSystemTime();
                updatePlayingTime();
                videoOriginalHeight = mp.getVideoHeight();
                videoOriginalWidth = mp.getVideoWidth();

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        //设置缓存进度（第二进度）
                        pbSeekBarBottom.setProgress((int) (percent * mp.getDuration() / 100));
                    }
                });

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START://缓存开始
                                //layoutBuffer.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END://缓存结束
                                //layoutBuffer.setVisibility(View.GONE);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
        //播放结束的监听
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeMessages(Msg_Update_Playing_Time);
                //当前时间进度完成（保持和总时长一致）
                tvCurrentTime.setText(StringUtils.parseTime((int) mp.getDuration()));
                pbSeekBarBottom.setProgress((int) mp.getDuration());
            }
        });
        //音量监听
        voiceSeekBarTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //fromUser:如果是用户拖动进度则是true，如果是代码中改变的则为 false
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    currentVolume = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始操作 SeekBar 执行该回调（移除隐藏操作）
                handler.removeMessages(Msg_Hide_Control_Panel);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止操作 SeekBar 执行该回调
                handler.sendEmptyMessageDelayed(Msg_Hide_Control_Panel, 3000);
            }
        });
        //播放进度监听
        pbSeekBarBottom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始操作 SeekBar 执行该方法（移除隐藏操作）
                handler.removeMessages(Msg_Hide_Control_Panel);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止操作 SeekBar 执行该回调
                handler.sendEmptyMessageDelayed(Msg_Hide_Control_Panel, 3000);
            }
        });
        //设置控制面板监听
        gestureDetector = new GestureDetector(this, new MyGestureListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGoBack:
                this.finish();
                break;
            case R.id.tvPre:
                processPreNext(true);
                break;
            case R.id.tvNext:
                processPreNext(false);
                break;
            case R.id.tvPlayPause:
                if (videoView.isPlaying()) {
                    handler.removeMessages(Msg_Update_Playing_Time);
                    videoView.pause();
                } else {
                    videoView.start();
                    updatePlayingTime();
                }
                break;
            case R.id.tvFullScreen:
                fullScreen();
                break;
            case R.id.ivVideoTop:
                //获取当前的音量
                int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (streamVolume != 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    voiceSeekBarTop.setProgress(0);
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    voiceSeekBarTop.setProgress(currentVolume);
                }
                break;
        }
    }

    //通过手势改变音量和亮度
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float x = event.getX();
                if (x < screenWidth / 2) {
                    //在左边做手势变化（处理音量变化）
                    if (y - downY > 0) {
                        //向下滑动（降低音量）
                        if (y - downY > 10) {
                            currentVolume--;
                        }
                    } else {
                        //向上滑动（增加音量）
                        if (y - downY < 10) {
                            currentVolume++;
                        }
                    }
                    if (currentVolume < 0) {
                        currentVolume = 0;
                    }
                    if (currentVolume > maxVolume) {
                        currentVolume = maxVolume;
                    }
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                } else {
                    //在右边做手势变化（处理亮度变化）
                    if (y - downY > 0) {
                        //下滑（降低亮度）
                        if (y - downY > 10) {
                            currentAlpha = currentAlpha + 0.1f;
                        }
                    } else {
                        //上滑（增加亮度）
                        if (y - downY < 10) {
                            currentAlpha = currentAlpha - 0.1f;
                        }
                    }
                    if (currentAlpha < 0.1f) {
                        currentAlpha = 0.1f;
                    }
                    if (currentAlpha > 0.8f) {
                        currentAlpha = 0.8f;
                    }
                    coverView.setAlpha(currentAlpha);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onTouchEvent(event);
    }


    //上一首/下一首切换
    private void processPreNext(boolean isPre) {
        if (itemVideoAll == null || itemVideoAll.size() == 0) {
            //容错：兼容网络播放或从外部应用进来的播放
            return;
        }
        if (isPre) { //上一首
            if (itemVideoPos > 0) {
                MediaVideoBean videoBean = itemVideoAll.get(--itemVideoPos);
                if (videoBean != null) {
                    initPlayer(videoBean);
                }
            } else {
                Toast.makeText(this, "前面没有啦~", Toast.LENGTH_SHORT).show();
            }
        } else {//下一首
            if (itemVideoPos < itemVideoAll.size() - 1) {
                MediaVideoBean videoBean = itemVideoAll.get(++itemVideoPos);
                if (videoBean != null) {
                    initPlayer(videoBean);
                }
            } else {
                Toast.makeText(this, "后面没有了~", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //设置全屏
    private void fullScreen() {
        if (isFullScreen) {
            //按照高度进行比例缩放
            videoView.getLayoutParams().height = screenHeight;
            videoView.getLayoutParams().width = screenWidth / videoOriginalHeight * videoOriginalWidth;
//            //可能会导致视频比最初的显示略小
//            videoView.getLayoutParams().height = videoOriginalHeight;
//            videoView.getLayoutParams().width = videoOriginalWidth;
        } else {
            videoView.getLayoutParams().height = screenHeight;
            videoView.getLayoutParams().width = screenWidth;
        }
        videoView.requestLayout();
        isFullScreen = !isFullScreen;

    }

    //隐藏控制自定义的面板
    private void hideControlPanel() {
        layoutVideoTop.animate().translationY(-layoutVideoTop.getHeight()).setDuration(300);
        layoutVideoBottom.animate().translationY(layoutVideoBottom.getHeight()).setDuration(300);
    }

    //显示控制自定义的面板
    private void showControlPanel() {
        layoutVideoTop.animate().translationY(0).setDuration(300);
        layoutVideoBottom.animate().translationY(0).setDuration(300);
        handler.sendEmptyMessageDelayed(Msg_Hide_Control_Panel, 3000);
    }

    //更新播放时间进度
    private void updatePlayingTime() {
        //获取当前播放时间未知
        int playPos = (int) videoView.getCurrentPosition();
        tvCurrentTime.setText(StringUtils.parseTime(playPos));
        handler.sendEmptyMessageDelayed(Msg_Update_Playing_Time, 500);
        pbSeekBarBottom.setProgress(playPos);
    }


    //更新手机系统时间
    private void updateSystemTime() {
        tvTimeTop.setText(StringUtils.getTime());
        handler.sendEmptyMessageDelayed(Msg_Update_System_Time, 1000);
    }

    //更新手机电量
    private void updateBatteryUI(int level) {
        tvBatteryTop.setText(level + "%");
    }

    //广播监听手机电量变化
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //通过 Intent 获取系统电量的 key 就是："level"（注意别写错了）
            int level = intent.getIntExtra("level", 0);
            updateBatteryUI(level);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            showControlPanel();
            return super.onSingleTapUp(e);
        }
    }

    //弱引用方式
    private static class MyHandler extends Handler {

        private WeakReference<VitamioPlayActivity> weakReference;

        MyHandler(VitamioPlayActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VitamioPlayActivity activity = weakReference.get();
            if (activity != null && msg != null) {
                if (msg.what == activity.Msg_Update_System_Time) {
                    activity.updateSystemTime();
                } else if (msg.what == activity.Msg_Update_Playing_Time) {
                    activity.updatePlayingTime();
                } else if (msg.what == activity.Msg_Hide_Control_Panel) {
                    activity.hideControlPanel();
                }
            }
        }
    }

}
