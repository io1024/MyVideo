package com.zero.video.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zero.video.R;
import com.zero.video.activity.VideoPlayActivity;
import com.zero.video.activity.VitamioPlayActivity;

/**
 * 模拟：网络视频界面
 */
public class NetworkFragment extends BaseFragment {

    private TextView tvNet1, tvNet2, tvNet3;

    private String[] urls = {"http://vd4.bdstatic.com/mda-jj0fmszjt026j7vv/sc/mda-jj0fmszjt026j7vv.mp4",
            "http://vd2.bdstatic.com/mda-jhkr4h4pc4nym25j/sc/mda-jhkr4h4pc4nym25j.mp4",
            "http://vd2.bdstatic.com/mda-jggeg4bhkvic2dui/sc/mda-jggeg4bhkvic2dui.mp4"};

    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        tvNet1 = view.findViewById(R.id.tvNet1);
        tvNet2 = view.findViewById(R.id.tvNet2);
        tvNet3 = view.findViewById(R.id.tvNet3);

        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        tvNet1.setOnClickListener(this);
        tvNet2.setOnClickListener(this);
        tvNet3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
////        Intent intent = new Intent(getActivity(), VitamioPlayActivity.class);
//        if (v.getId() == R.id.tvNet1) {
//            intent.putExtra("httpUrl", 0);
//        } else if (v.getId() == R.id.tvNet2) {
//            intent.putExtra("httpUrl", 1);
//        } else if (v.getId() == R.id.tvNet3) {
//            intent.putExtra("httpUrl", 2);
//        }
        if (v.getId() == R.id.tvNet1) {
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setDataAndType(Uri.parse(urls[0]), "video/mpeg4");
            intent1.addCategory("android.intent.category.DEFAULT");//可以省略不写
            startActivity(intent1);
        } else if (v.getId() == R.id.tvNet2) {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.parse(urls[1]), "video/mpeg4");
            intent2.addCategory("android.intent.category.DEFAULT");//可以省略不写
            startActivity(intent2);
        } else if (v.getId() == R.id.tvNet3) {
            Intent intent = new Intent(getActivity(), VitamioPlayActivity.class);
            intent.setDataAndType(Uri.parse(urls[2]), "video/mp4");
            startActivity(intent);
        }

    }
}
