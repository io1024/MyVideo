package com.zero.video;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 启动界面
 */
public class AppStartActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    //    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};
    private String[] permissions = new String[]{Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    public void initView() {

    }

    @AfterPermissionGranted(1000)
    @Override
    public void initData() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //有权限
            //Log.e("EasyPermissions", "已授权");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            //申请权限
            EasyPermissions.requestPermissions(this, "需要获取手机卡读写权限和网络权限", 1000, permissions);
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    //权限回调方法：4个
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //EasyPermissions 权限回调  授权成功 执行该方法
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //EasyPermissions 权限回调  授权失败 执行该方法
        Toast.makeText(getApplicationContext(), "请确认授权后才可以使用~", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.e("EasyPermissions", "dialog提醒点击“确定”");
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.e("EasyPermissions", "dialog提醒点击“取消”");
    }
}
