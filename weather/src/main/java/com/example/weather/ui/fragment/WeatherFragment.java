package com.example.weather.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.databinding.FragmentWeatherBinding;
import com.example.weather.event.EventMessage;
import com.example.weather.model.Hourly;
import com.example.weather.model.WeatherData;
import com.example.weather.model.WeatherImageview;
import com.example.weather.tool.CityApi;
import com.example.weather.tool.GetPhoNetInfo;
import com.example.weather.view.HourlyLineView;
import com.example.weather.view.HourlyView;
import com.example.weather.view.LineView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by hasee on 2019/3/11.
 */

public class WeatherFragment extends Fragment {
    private WeatherData weatherData;
    private static Map<String, Integer> map=new HashMap<>();
    private static int[] rl=new int[]{R.id.rl11, R.id.rl12, R.id.rl13, R.id.rl14, R.id.rl15, R.id.rl16, R.id.rl17};
    private List<Hourly> hourlyList=new ArrayList<>();
    public FragmentWeatherBinding mBinding;
    private List<FragmentTransaction> transactionList=new ArrayList<>();
    HourlyLineView hourlyLineView;
    public static EventBus eventBus;
    public static String city="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);
        int height= GetPhoNetInfo.screenHeight;
        int stateHeight=GetPhoNetInfo.stateHeight;
        mBinding.space1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(height-stateHeight-50*GetPhoNetInfo.density)/2));
        mBinding.llDown.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(height-stateHeight-50*GetPhoNetInfo.density)/2));
        /*// 设置刷新时进度动画变换的颜色，接收参数为可变长度数组。也可以使用setColorSchemeColors()方法。
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_orange_light, android.R
                .color.holo_green_light);
        // 设置刷新时圆形图标的大小。只可传递2个参数，SwipeRefreshLayout.LARGE或SwipeRefreshLayout.DEFAULT。
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        // 设置刷新时圆形图标的背景色。也可以使用setProgressBackgroundColorSchemeColor()方法。
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.background_light);
        // 设置刷新时的圆形图标。scale：是否缩放；start：圆形图标在刷新前的起始位置；end：圆形图标的偏移量。
        swipeRefreshLayout.setProgressViewOffset(true, 100, 200);*/
        // 设置会触发下拉刷新的手势滑动距离
        mBinding.swipeRefreshLayout.setDistanceToTriggerSync(150);
        //设置在listview上下拉刷新的监听

        initView(false);
        eventBus=EventBus.builder().build();
        eventBus.register(this);
        /*mBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"点击了",Toast.LENGTH_SHORT).show();
                weatherData.setTime(weatherData.getTime());
                mBinding.setWeatherData(weatherData);
            }
        });*/
        return mBinding.getRoot();
    }

    public void initView(boolean isNeed) {
        if(isNeed){
            weatherData=LitePal.where("city=?",weatherData.getCity()).find(WeatherData.class).get(0);
        }else{
            weatherData=(WeatherData) getArguments().getSerializable("weatherData");
        }
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里可以做一下下拉刷新的操作
                //例如去请求后台接口啥的。。。
                /*MainActivity mainActivity=(MainActivity) getActivity();
                mainActivity.upData();*/
                city=weatherData.getCity();
                new CityApi().getWeatherDateForNet(city);
                Log.d("test2","city:"+city);
            }
        });
        Log.d("test2",mBinding.toString());
        Calendar date = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTime(strToDate("18:30"));
        mBinding.setIsMorning(date.before(end));
        mBinding.setWeatherData(weatherData);
        mBinding.setMap(map);
        mBinding.setWeatherImageview(new WeatherImageview(mBinding.getIsMorning()?map.get(weatherData.getDaily().get(0).getDay_img()):map.get(weatherData.getDaily().get(0).getNight_img())
                ,mBinding.getIsMorning()?map.get(weatherData.getDaily().get(1).getDay_img()):map.get(weatherData.getDaily().get(1).getNight_img())));

        for(int i=0;i<7;i++){
            DayFragment dayFragment=new DayFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("day",i);
            bundle.putString("upDatetime",weatherData.getUpdatetime());
            bundle.putString("city",weatherData.getCity());
            dayFragment.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(rl[i],dayFragment,"tag"+i).commit();
            /*if(transactionList.size()<=i){
                transactionList.add(transaction);
            }
            if(!transactionList.get(i).isEmpty()){
                transactionList.get(i).replace(rl[i],dayFragment).commit();

            }else{
                transactionList.get(i).add(rl[i],dayFragment).commit();
            }*/
        }
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mBinding.rlLine.measure(w,h);
        mBinding.rlLine.removeAllViews();
        mBinding.rlLine.addView(new LineView(getActivity(),weatherData.getCity(),mBinding.rlLine.getMeasuredWidth(),mBinding.rlLine.getMeasuredHeight(),0));
        mBinding.rlLine2.measure(w,h);
        mBinding.rlLine2.removeAllViews();
        mBinding.rlLine2.addView(new LineView(getActivity(),weatherData.getCity(),mBinding.rlLine.getMeasuredWidth(),mBinding.rlLine.getMeasuredHeight(),1));
        mBinding.rlHourly.measure(w,h);
        if(hourlyLineView!=null){
            mBinding.rlHourly.removeView(hourlyLineView);
        }
        hourlyLineView=new HourlyLineView(getActivity(),GetPhoNetInfo.screenWidth,mBinding.rlHourly.getMeasuredHeight(),weatherData.getCity());
        mBinding.rlHourly.addView(hourlyLineView);
        hourlyList= LitePal.where("city=?",weatherData.getCity()).find(Hourly.class);
        mBinding.llHourly.removeAllViews();
        for(int y=0;y<hourlyList.size();y++){
            mBinding.llHourly.addView(new HourlyView(getActivity(),hourlyList.get(y).getWeather(),map.get(hourlyList.get(y).getImg()),GetPhoNetInfo.screenWidth,mBinding.rlHourly.getMeasuredHeight()));
        }
        if(isNeed){
            eventBus.post(new EventMessage(1,""));
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onGetMessageInFragment(EventMessage message){
        switch (message.getWhat()){
            case 0:
                EventBus.getDefault().post(new EventMessage(5,city));
                //initView(true);
                break;
            case 1:
                EventBus.getDefault().post(new EventMessage(6,city));
                Toast.makeText(getActivity(),"刷新完成",Toast.LENGTH_SHORT).show();
                mBinding.swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    public static Date strToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    static {
        map.put("0", R.mipmap.w0);map.put("1", R.mipmap.w1);map.put("2", R.mipmap.w2);map.put("3", R.mipmap.w3);
        map.put("4", R.mipmap.w4);map.put("5", R.mipmap.w5);map.put("6", R.mipmap.w6);map.put("7", R.mipmap.w7);
        map.put("8", R.mipmap.w8);map.put("9", R.mipmap.w9);map.put("10", R.mipmap.w10);map.put("11", R.mipmap.w11);
        map.put("12", R.mipmap.w12);map.put("13", R.mipmap.w13);map.put("14", R.mipmap.w14);map.put("15", R.mipmap.w15);
        map.put("16", R.mipmap.w16);map.put("17", R.mipmap.w17);map.put("18", R.mipmap.w18);map.put("19", R.mipmap.w19);
        map.put("20", R.mipmap.w20);map.put("21", R.mipmap.w21);map.put("22", R.mipmap.w22);map.put("23", R.mipmap.w23);
        map.put("24", R.mipmap.w24);map.put("25", R.mipmap.w25);map.put("26", R.mipmap.w26);map.put("27", R.mipmap.w27);
        map.put("28", R.mipmap.w28);map.put("29", R.mipmap.w29);map.put("30", R.mipmap.w30);map.put("31", R.mipmap.w31);
        map.put("32", R.mipmap.w32);map.put("39", R.mipmap.w39);map.put("49", R.mipmap.w49);map.put("53", R.mipmap.w53);
        map.put("54", R.mipmap.w54);map.put("55", R.mipmap.w55);map.put("56", R.mipmap.w56);map.put("57", R.mipmap.w57);
        map.put("58", R.mipmap.w58);map.put("99", R.mipmap.w99);map.put("301", R.mipmap.w301);map.put("302", R.mipmap.w302);
    }

}
