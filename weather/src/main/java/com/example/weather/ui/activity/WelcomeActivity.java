package com.example.weather.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.example.weather.R;
import com.example.weather.adapter.WelcomeVPAdapter;
import com.example.weather.databinding.ActivityWelcomeBinding;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding binding;
    private  Boolean color;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        color=true;
        binding.setColor(color);
        List<View> viewList=new ArrayList<>();
        viewList.add(getLayoutInflater().from(WelcomeActivity.this).inflate(R.layout.viewpager_1, null));
        viewList.add(getLayoutInflater().from(WelcomeActivity.this).inflate(R.layout.viewpager_2, null));
        binding.setWelcomeVPAdapter(new WelcomeVPAdapter(viewList));
        binding.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                color=position==0;
                binding.setColor(color);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void finishButton(View view){
        getSharedPreferences("setInfo",MODE_PRIVATE).edit().putBoolean("isWelcome",false).apply();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        this.finish();
    }
}
