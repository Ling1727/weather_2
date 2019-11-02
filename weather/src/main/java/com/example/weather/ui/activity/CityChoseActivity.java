package com.example.weather.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.weather.R;
import com.example.weather.event.EventMessage;
import com.example.weather.model.WeatherData;
import com.example.weather.tool.CityApi;
import com.example.weather.tool.Locate;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2019/3/12.
 */

public class CityChoseActivity extends AppCompatActivity {
    List<HotCity> hotCities = new ArrayList<>();
    CityPicker cityPicker;
    Locate locate;
    private String citycode,city;
    public static EventBus eventBus;
    private List<WeatherData> weatherDataList;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_city);
        eventBus=EventBus.builder().build();
        eventBus.register(this);
        weatherDataList= LitePal.findAll(WeatherData.class);
        initdata();
    }

    public void initdata(){
        //ProgressDialog.show(CityChoseActivity.this,"简单环形进度条","任务进行中",false,false);
        hotCities.add(new HotCity("北京", "北京", "101010100")); //code为城市代码
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        cityPicker= CityPicker.from(CityChoseActivity.this) ;
        //CityPicker.from(CityChoseActivity.this) //activity或者fragment
        cityPicker.enableAnimation(true)	//启用动画效果，默认无
                //.setAnimationStyle(anim)	//自定义动画
                .setLocatedCity(null)  //APP自身已定位的城市，传null会自动定位（默认）//new LocatedCity("杭州", "浙江", "101210101")
                .setHotCities(hotCities)	//指定热门城市
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        citycode=data.getCode();
                        city=data.getName();
                        boolean ishave=false;
                        for(WeatherData weatherData:weatherDataList){
                            if(weatherData.getCity().equals(city)){
                                ishave=true;
                                break;
                            }
                        }
                        if(!ishave){
                            dialog= new Dialog(CityChoseActivity.this);
                            dialog.setContentView(R.layout.dialog_wait);
                            dialog.setCancelable(false);
                            dialog.show();
                            new CityApi().getWeatherDateForNet(city);
                        }else{
                            Toast.makeText(CityChoseActivity.this,"该城市已存在",Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new EventMessage(4,city));
                            CityChoseActivity.this.finish();
                        }
                    }
                    @Override
                    public void onCancel(){
                        Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                        //关闭Activity
                        CityChoseActivity.this.finish();
                    }

                    @Override
                    public void onLocate() {
                        locate=new Locate(CityChoseActivity.this);
                        locate.runTask();
                    }
                })
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(EventMessage message){
        switch (message.getWhat()){
            case 0:
                if(message.getMessage().equals("")){
                    Toast.makeText(CityChoseActivity.this,"定位失败！",Toast.LENGTH_SHORT).show();
                }else{
                    cityPicker.locateComplete(new LocatedCity(message.getMessage(),"广东","101280101"), LocateState.SUCCESS);
                }
                break;
            case 1:
                Toast.makeText(CityChoseActivity.this,"数据加载成功！",Toast.LENGTH_SHORT).show();
                if(dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                CityChoseActivity.this.finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        eventBus=null;
    }
}
