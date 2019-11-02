package com.example.weather.tool;


import android.util.Log;

import com.example.weather.event.EventMessage;
import com.example.weather.model.Daily;
import com.example.weather.model.Exponent;
import com.example.weather.model.Hourly;
import com.example.weather.model.WeatherData;
import com.example.weather.ui.activity.CityChoseActivity;
import com.example.weather.ui.fragment.WeatherFragment;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by hasee on 2019/3/11.
 */

public class CityApi {
    private Boolean isOver;
    private WeatherData weatherData;
    private List<WeatherData> weatherDataList;
    private List<Exponent> exponentList;
    private List<Daily> dailyList;
    private List<Hourly> hourlyList;

    public void getWeatherDateForNet(String city){
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.jisuapi.com/weather/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(RetrofitApi.class).getData("2c370c8ab5b36926",city).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull retrofit2.Call<JSONObject> call, @NotNull retrofit2.Response<JSONObject> response) {
                if(response.body()!=null){
                    try {
                        Log.d("test",response.body().toString());
                        JSONObject json = new JSONObject(response.body().toString());
                        Get(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull retrofit2.Call<JSONObject> call, @NotNull Throwable t) {
                EventBus.getDefault().post(new EventMessage(1,"error"));
            }
        });*/

        Log.d("test","city:"+city);
        final String website="https://api.jisuapi.com/weather/query?appkey=2c370c8ab5b36926&city="+city;
        //                   "http://api.jisuapi.com/weather/query?appkey=2c370c8ab5b36926&city="+city
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder().
                connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        Request request = new Request.Builder().url(website).method("GET",null).build();
        //3.创建一个call对象,参数就是Request请求对象
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                EventBus.getDefault().post(new EventMessage(1,"error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.body()!=null){
                    try {
                        Get(JSONObject.fromObject(response.body().string()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void Get(final JSONObject json) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                weatherData=new WeatherData();
                exponentList= LitePal.findAll(Exponent.class);
                dailyList= LitePal.findAll(Daily.class);
                hourlyList= LitePal.findAll(Hourly.class);
                try {
                    if (json.getInt("status") != 0) {
                        System.out.println(json.getString("msg"));
                    } else {
                        JSONObject resultarr = json.optJSONObject("result");
                        weatherData.setCity(resultarr.getString("city"));
                        Log.d("test4",weatherData.getCity()+"");
                        weatherData.setCityid(resultarr.getInt("cityid"));
                        weatherData.setCitycode(resultarr.getString("citycode"));
                        weatherData.setDate(resultarr.getString("date"));
                        weatherData.setWeek(resultarr.getString("week"));
                        weatherData.setWeather(resultarr.getString("weather"));
                        weatherData.setTemp(resultarr.getString("temp"));
                        weatherData.setTemphigh(resultarr.getString("temphigh"));
                        weatherData.setTemplow(resultarr.getString("templow"));
                        weatherData.setImg(resultarr.getString("img"));
                        weatherData.setHumidity(resultarr.getString("humidity"));
                        weatherData.setPressure(resultarr.getString("pressure"));
                        weatherData.setWindspeed(resultarr.getString("windspeed"));
                        weatherData.setWinddirect(resultarr.getString("winddirect"));
                        weatherData.setWindpower(resultarr.getString("windpower"));
                        String updatatime0=(String) resultarr.get("updatetime");
                        String updatatime=updatatime0.substring(11,16);
                        weatherData.setUpdatetime(updatatime);
                        Date date=new Date();
                        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
                        weatherData.setTime(sdf.format(date));
                        Log.d("test4",weatherData.getTime()+"");
                        Log.d("test",weatherData.getUpdatetime()+" "+weatherData.getTime().toString());
                        if (resultarr.opt("index") != null) {
                            JSONArray index = resultarr.optJSONArray("index");
                            for (int i = 0; i < index.size(); i++) {
                                JSONObject obj = (JSONObject) index.opt(i);
                                Exponent exponent1 =new Exponent();
                                exponent1.setCity(resultarr.getString("city"));
                                exponent1.setIname(obj.getString("iname"));
                                exponent1.setIvalue(obj.getString("ivalue"));
                                exponent1.setDetail(obj.getString("detail"));
                                boolean ishave=false;
                                for(int e=0;e<exponentList.size();e++){
                                    if(exponent1.getCity().equals(exponentList.get(e).getCity())){
                                        ishave=true;
                                        break;
                                    }
                                }
                                if(ishave){
                                    exponent1.updateAll("city=? and iname=?",exponent1.getCity(),exponent1.getIname());
                                }else{
                                    exponent1.save();
                                }
                            }
                        }
                        if (resultarr.opt("aqi") != null) {
                            JSONObject aqi = resultarr.optJSONObject("aqi");
                            weatherData.setSo2(aqi.getString("so2"));
                            weatherData.setSo224(aqi.getString("so224"));
                            weatherData.setNo2(aqi.getString("no2"));
                            weatherData.setNo224(aqi.getString("no224"));
                            weatherData.setCo(aqi.getString("co"));
                            weatherData.setCo24(aqi.getString("co24"));
                            weatherData.setO3(aqi.getString("o3"));
                            weatherData.setO38(aqi.getString("o38"));
                            weatherData.setO324(aqi.getString("o324"));
                            weatherData.setPm10(aqi.getString("pm10"));
                            weatherData.setPm1024(aqi.getString("pm1024"));
                            weatherData.setPm2_5(aqi.getString("pm2_5"));
                            weatherData.setPm2_524(aqi.getString("pm2_524"));
                            weatherData.setIso2(aqi.getString("iso2"));
                            weatherData.setIno2(aqi.getString("ino2"));
                            weatherData.setIco(aqi.getString("ico"));
                            weatherData.setIo3(aqi.getString("io3"));
                            weatherData.setIo38(aqi.getString("io38"));
                            weatherData.setIpm10(aqi.getString("ipm10"));
                            weatherData.setIpm2_5(aqi.getString("ipm2_5"));
                            weatherData.setAqi(aqi.getString("aqi"));
                            weatherData.setPressure(aqi.getString("primarypollutant"));
                            weatherData.setQuality(aqi.getString("quality"));
                            weatherData.setTimepoint(aqi.getString("timepoint"));
                            if (aqi.opt("aqiinfo") != null) {
                                JSONObject aqiinfo = aqi.optJSONObject("aqiinfo");
                                weatherData.setLevel(aqiinfo.getString("level"));
                                weatherData.setColor(aqiinfo.getString("color"));
                                weatherData.setAffect(aqiinfo.getString("affect"));
                                weatherData.setMeasure(aqiinfo.getString("measure"));
                            }
                        }
                        if (resultarr.opt("daily") != null) {
                            JSONArray daily = resultarr.optJSONArray("daily");
                            for (int i = 0; i < daily.size(); i++) {
                                JSONObject obj = (JSONObject) daily.opt(i);
                                Daily daily1=new Daily();
                                daily1.setCity(resultarr.getString("city"));
                                daily1.setType(i+"");
                                daily1.setData(obj.getString("date"));
                                daily1.setWeek(obj.getString("week"));
                                daily1.setSunrise(obj.getString("sunrise"));
                                daily1.setSunset(obj.getString("sunset"));
                                daily1.setQuality(weatherData.getQuality());
                                if (obj.opt("night") != null) {
                                    JSONObject night = (JSONObject) obj.opt("night");
                                    daily1.setNight_weather(night.getString("weather"));
                                    daily1.setNight_templow(night.getString("templow"));
                                    daily1.setNight_img(night.getString("img"));
                                    daily1.setNight_winddirect(night.getString("winddirect"));
                                    daily1.setNight_windpower(night.getString("windpower"));
                                }
                                if (obj.opt("day") != null) {
                                    JSONObject day = obj.optJSONObject("day");
                                    daily1.setDay_weather(day.getString("weather"));
                                    daily1.setDay_temphigh(day.getString("temphigh"));
                                    daily1.setDay_img(day.getString("img"));
                                    daily1.setDay_winddirect(day.getString("winddirect"));
                                    daily1.setDay_windpower(day.getString("windpower"));
                                }
                                boolean ishave=false;
                                for(int d=0;d<dailyList.size();d++){
                                    if(daily1.getCity().equals(dailyList.get(d).getCity())){
                                        ishave=true;
                                        break;
                                    }
                                }
                                if(ishave){
                                    daily1.updateAll("city=? and type=?",daily1.getCity(), daily1.getType());
                                }else{
                                    daily1.save();
                                }
                            }
                        }
                        if (resultarr.opt("hourly") != null) {
                            JSONArray hourly = resultarr.optJSONArray("hourly");
                            for (int i = 0; i < hourly.size(); i++) {
                                JSONObject obj = (JSONObject) hourly.opt(i);
                                Hourly hourly1=new Hourly();
                                hourly1.setType(i+"");
                                hourly1.setCity(resultarr.getString("city"));
                                hourly1.setTime(obj.getString("time"));
                                hourly1.setWeather(obj.getString("weather"));
                                hourly1.setTemp(obj.getString("temp"));
                                hourly1.setImg(obj.getString("img"));

                                boolean ishave=false;
                                for(int h=0;h<hourlyList.size();h++){
                                    if(hourly1.getCity().equals(hourlyList.get(h).getCity())){
                                        ishave=true;
                                        break;
                                    }
                                }
                                if(ishave){
                                    hourly1.updateAll("city=? and type=?",hourly1.getCity(), hourly1.getType());
                                }else{
                                    hourly1.save();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                weatherDataList= LitePal.findAll(WeatherData.class);
                boolean ishave=false;
                for(int i=0;i<weatherDataList.size();i++){
                    if(weatherData.getCitycode().equals(weatherDataList.get(i).getCitycode())){
                        ishave=true;
                        break;
                    }
                }
                if(ishave){
                    weatherData.updateAll("citycode=?",weatherData.getCitycode());
                }else{
                    weatherData.save();
                }

                if(CityChoseActivity.eventBus!=null){
                    EventBus.getDefault().post(new EventMessage(2,"updata"));
                }else{
                    EventBus.getDefault().post(new EventMessage(2,""));
                    if(WeatherFragment.eventBus!=null){
                        WeatherFragment.eventBus.post(new EventMessage(0,""));

                    }
                }


            }
        }).start();
    }


    public Boolean getOver() {
        return isOver;
    }

    public void setOver(Boolean over) {
        isOver = over;
    }
}
