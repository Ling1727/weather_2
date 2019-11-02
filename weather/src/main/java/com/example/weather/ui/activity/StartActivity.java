package com.example.weather.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.weather.R;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import rx.functions.Action1;


public class StartActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getPermissions();

    }


    private void getPermissions() {
        new RxPermissions(StartActivity.this)
                .requestEach(/*Manifest.permission.ACCESS_FINE_LOCATION
                        ,*/Manifest.permission.ACCESS_COARSE_LOCATION
                        /*,Manifest.permission.ACCESS_WIFI_STATE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_SMS
                        ,Manifest.permission.READ_PHONE_STATE*/)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if(permission.granted){
                            isWelcome();
                        }else{
                            getPermissions();
                        }
                    }
                });
    }

    private void isWelcome() {
        if(getSharedPreferences("setInfo",MODE_PRIVATE).getBoolean("isWelcome",true)){
            startActivity(new Intent(StartActivity.this, WelcomeActivity.class));
        }else{
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }
        finish();
    }
}
