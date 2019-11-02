package com.example.weather.tool;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.weather.MyApplication;

public class GetPhoNetInfo {
    public static int screenWidth;
    public static int screenHeight;
    public static int density;
    public static int stateHeight;

    public static void init(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;         // 屏幕宽度（像素）
        screenHeight = dm.heightPixels;       // 屏幕高度（像素）
        density =(int)dm.density;
        if (context.getResources().getIdentifier("status_bar_height", "dimen", "android") > 0) {
            stateHeight = context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("status_bar_height", "dimen", "android"));
        }
    }

}
