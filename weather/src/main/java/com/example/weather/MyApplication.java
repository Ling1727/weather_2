package com.example.weather;

import android.app.Application;
import android.content.Context;

import com.example.weather.tool.GetPhoNetInfo;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    private  Context context;
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        setContext(this);
        GetPhoNetInfo.init(context);
    }

    public static MyApplication getInstance(){
        if(instance==null){
            return new MyApplication();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
