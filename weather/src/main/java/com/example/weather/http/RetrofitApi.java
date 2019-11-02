package com.example.weather.http;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitApi {
    //post请求
    @FormUrlEncoded
    @POST("query")
    Call<JSONObject> getData(@Field("appkey") String appkey,@Field("city") String city);
}
