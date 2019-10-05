package com.zero.video;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zero.video.adapter.FragmentAdapter;
import com.zero.video.fragment.NetworkFragment;
import com.zero.video.fragment.LocalFragment;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    TextView tvVideoTab, tvMusicTab;
    ViewPager vp;
    View line;
    private int screenWidth;
    private ArrayList<Fragment> fragments;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        tvVideoTab = findViewById(R.id.tvVideoTab);
        tvMusicTab = findViewById(R.id.tvMusicTab);
        line = findViewById(R.id.line);
        vp = findViewById(R.id.vp);
    }

    @Override
    public void initData() {
        //处理 line 的宽度
        //获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        Log.e("屏幕", "宽度-X轴=" + screenWidth + "高度-Y轴=" + height);

        line.getLayoutParams().width = screenWidth / 2;
        line.requestLayout();//设置重新绘制line

        LocalFragment localFragment = new LocalFragment();
        NetworkFragment networkFragment = new NetworkFragment();
        fragments = new ArrayList<>();
        fragments.add(localFragment);
        fragments.add(networkFragment);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
        updateTabUI(0);//初始化
    }

    @Override
    public void initListener() {
        tvVideoTab.setOnClickListener(this);
        tvMusicTab.setOnClickListener(this);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //获取 line 开始滑动时的初始位置
                int startPos = screenWidth * position / fragments.size();
                //计算移动的距离(手指移动的像素：positionOffset * screenWidth)
                float distance = positionOffset * screenWidth / fragments.size();
                //设置 line 随着滑动而滑动
                line.setTranslationX(startPos + distance);
            }

            @Override
            public void onPageSelected(int position) {
                updateTabUI(position);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVideoTab:
                updateTabUI(0);
                vp.setCurrentItem(0);
                break;
            case R.id.tvMusicTab:
                updateTabUI(1);
                vp.setCurrentItem(1);
                break;
        }
    }

    private void updateTabUI(int pos) {
        int c1 = getResources().getColor(R.color.color_ffdb7079);
        int c2 = getResources().getColor(R.color.color_ffffffff);
        if (pos == 0) {
            //tvVideoTab.setTextSize(20);
            tvVideoTab.setTextColor(c1);
            tvVideoTab.setTypeface(Typeface.DEFAULT_BOLD);
            //通过属性动画修改大小（X、Y轴方向变大）
            tvVideoTab.animate().scaleX(1.2f).scaleY(1.2f);
            //tvMusicTab.setTextSize(18);
            tvMusicTab.setTextColor(c2);
            tvMusicTab.setTypeface(Typeface.DEFAULT);
            tvMusicTab.animate().scaleX(1.0f).scaleY(1.0f);
        } else {
            //tvVideoTab.setTextSize(18);
            tvVideoTab.setTextColor(c2);
            tvVideoTab.setTypeface(Typeface.DEFAULT);
            tvVideoTab.animate().scaleX(1.0f).scaleY(1.0f);
            //tvMusicTab.setTextSize(20);
            tvMusicTab.setTextColor(c1);
            tvMusicTab.setTypeface(Typeface.DEFAULT_BOLD);
            tvMusicTab.animate().scaleX(1.2f).scaleY(1.2f);
        }
    }
}
