package com.example.weather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.weather.R;
import com.example.weather.model.Daily;
import org.litepal.LitePal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by hasee on 2019/3/26.
 */

public class DayFragment extends Fragment {
    private View view,line11;
    private TextView tv11,tv12,tv13,tv14,tv15;
    private ImageView iv11,iv12;
    private int day;
    private Daily daily;
    private Map<String, Integer> map=new HashMap<>();
    private String upDatetime,city;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_day,container,false);
        initView();
        initData();
        initControl();
        return view;
    }

    public void initView(){
        iv11=view.findViewById(R.id.iv11);
        iv12=view.findViewById(R.id.iv12);
        tv11=view.findViewById(R.id.tv11);
        tv12=view.findViewById(R.id.tv12);
        tv13=view.findViewById(R.id.tv13);
        tv14=view.findViewById(R.id.tv14);
        tv15=view.findViewById(R.id.tv15);
        line11=view.findViewById(R.id.line11);
    }

    private void initData(){
        setMap();
        day=getArguments().getInt("day");
        upDatetime=getArguments().getString("upDatetime");
        city=getArguments().getString("city");
        daily= LitePal.where("city=?",city).find(Daily.class).get(day);
    }

    public void initControl(){
        String windpower,winddirect;
        Calendar date = Calendar.getInstance();
        date.setTime(strToDate(upDatetime));
        Calendar end = Calendar.getInstance();
        end.setTime(strToDate("18:30"));
        if (date.before(end)) {
            windpower=daily.getDay_windpower();
            winddirect=daily.getDay_winddirect();
        } else {
            windpower=daily.getNight_windpower();
            winddirect=daily.getDay_winddirect();
        }
        iv11.setImageResource(map.get(daily.getDay_img()));
        iv12.setImageResource(map.get(daily.getNight_img()));
        if(day==0){
            tv11.setText("今天");
        }else{
            tv11.setText("周"+daily.getWeek().substring(2));
        }
        tv12.setText(daily.getData().substring(5));
        tv13.setText(winddirect);
        tv14.setText(windpower);
        tv15.setText(daily.getQuality());
    }

    public void setMap(){
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

    public static Date strToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
        }
        return date;
    }
}
