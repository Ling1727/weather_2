package com.example.weather.model;

import android.databinding.BindingAdapter;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

public class WeatherImageview {
    private int iv1;
    private int iv2;

    public WeatherImageview(){}

    public WeatherImageview(int iv1,int iv2){
        setIv1(iv1);
        setIv2(iv2);
    }

    @DrawableRes
    public int getIv1() {
        return iv1;
    }

    public void setIv1(@DrawableRes int iv1) {
        this.iv1 = iv1;
    }

    @DrawableRes
    public int getIv2() {
        return iv2;
    }

    public void setIv2(@DrawableRes int iv2) {
        this.iv2 = iv2;
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
