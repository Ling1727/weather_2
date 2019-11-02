package com.example.weather.tool;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.weather.event.EventMessage;
import com.example.weather.ui.activity.CityChoseActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hasee on 2018/7/8.
 */

public class Locate {
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private Context context;

    public Locate(Context context){
        this.context=context;
    }

    public void runTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLocationClient = new LocationClient(context);
                myLocationListener = new MyLocationListener();
                mLocationClient.registerLocationListener(myLocationListener);
                initLocation();
                mLocationClient.start();
            }
        }).start();
    }

    void initLocation()
    {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    class MyLocationListener implements BDLocationListener {
       String city=null;
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
          /*  String addr = bdLocation.getAddrStr();    //获取详细地址信息
            String country = bdLocation.getCountry();    //获取国家
            Log.d("city", ":" + cityName);
            String province = bdLocation.getProvince();    //获取省份
            Log.d("city", ":" + cityName);
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //*/
            city=bdLocation.getCity();
            if(!(city==null)) {
                city=city.substring(city.length()-1).equals("市")?city.substring(0,city.length()-1):city;
                Log.d("test4",city.substring(city.length()-2)+"..."+city.substring(0,city.length()-2));
                if(CityChoseActivity.eventBus!=null){
                    CityChoseActivity.eventBus.post(new EventMessage(0,city));
                }else{
                    EventBus.getDefault().post(new EventMessage(0,city));
                }
            }else{
                if(CityChoseActivity.eventBus!=null){
                    CityChoseActivity.eventBus.post(new EventMessage(0,""));
                }else{
                    EventBus.getDefault().post(new EventMessage(0,""));
                }
            }
            }
        }

}




