package com.zero.video;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();
}
