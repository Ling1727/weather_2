package com.example.weather.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.weather.R;
import com.example.weather.adapter.FragmentAdapter;
import com.example.weather.databinding.ActivityMainBinding;
import com.example.weather.event.EventMessage;
import com.example.weather.model.Daily;
import com.example.weather.model.Exponent;
import com.example.weather.model.Hourly;
import com.example.weather.model.MainActivityModel;
import com.example.weather.model.WeatherData;
import com.example.weather.tool.CityApi;
import com.example.weather.tool.GetPhoNetInfo;
import com.example.weather.tool.Locate;
import com.example.weather.ui.fragment.WeatherFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseAppCompatActivity {
    private ActivityMainBinding binding;
    private List<Fragment> fragmentList;
    private List<WeatherData> weatherDataList;
    private CityApi cityApi;
    private FragmentAdapter fragmentAdapter;
    private MainActivityModel mainActivityModel;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        mainActivityModel=new MainActivityModel(true,1,0,"");
        binding.setMain(mainActivityModel);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50*GetPhoNetInfo.density);
        layoutParams.topMargin=GetPhoNetInfo.stateHeight;
        binding.rlTool.setLayoutParams(layoutParams);
        EventBus.getDefault().register(this);
        initView();
        getData();
    }

    public void initView(){
        weatherDataList= LitePal.findAll(WeatherData.class);
        fragmentList=new ArrayList<>();
        for(WeatherData weatherData:weatherDataList){
            WeatherFragment weatherFragment=new WeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("weatherData",weatherData);
            weatherFragment.setArguments(bundle);
            fragmentList.add(weatherFragment);
        }
        fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),fragmentList);
        binding.setFmAdapetr(fragmentAdapter);
        binding.vpMain.setOffscreenPageLimit(5);
        binding.vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mainActivityModel.setCtiy(weatherDataList.get(i).getCity());
                binding.getMain().setCurrent(i);
                binding.setMain(mainActivityModel);
                initPoint();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initPoint();
        if(weatherDataList.size()!=0){
            mainActivityModel.setCtiy(weatherDataList.get(0).getCity());
            binding.setMain(mainActivityModel);
        }
    }

    public void initPoint(){
        LinearLayout.LayoutParams linearparams=new LinearLayout.LayoutParams(16* GetPhoNetInfo.density,4*GetPhoNetInfo.density);
        binding.llPoint.removeAllViews();
        for(int i=0;i<weatherDataList.size();i++){
            View view=new View(MainActivity.this);
            view.setBackgroundResource(R.drawable.point);
            view.setLayoutParams(linearparams);
            linearparams.leftMargin=i==0?0:5*GetPhoNetInfo.density;
            view.setEnabled(i==binding.getMain().getCurrent());
            binding.llPoint.addView(view);
        }
    }

    public void getData(){
        if(weatherDataList.size()==0){
            dialog= new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_wait);
            dialog.setCancelable(false);
            dialog.show();
            new Locate(MainActivity.this).runTask();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(EventMessage message){
        switch (message.getWhat()){
            case 0:
                cityApi=new CityApi();
                if(message.getMessage().equals("")){
                    cityApi.getWeatherDateForNet("广州");
                    Toast.makeText(MainActivity.this,"无法获取定位信息！,默认广州",Toast.LENGTH_SHORT).show();
                }else{
                    cityApi.getWeatherDateForNet(message.getMessage());
                }
                break;
            case 1:
                Toast.makeText(MainActivity.this,"数据获取失败，请检查网络！",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if(fragmentList.size()==0||message.getMessage().equals("updata")){
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    initView();
                    if(CityChoseActivity.eventBus!=null){
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                binding.vpMain.setCurrentItem(fragmentList.size()-1);
                                CityChoseActivity.eventBus.post(new EventMessage(1,""));
                            }
                        },1000);

                    }
                }
                break;
            case 4:
                for(int i=0;i<weatherDataList.size();i++){
                    if(weatherDataList.get(i).getCity().equals(message.getMessage())){
                        binding.vpMain.setCurrentItem(i);
                        break;
                    }
                }
                break;
            case 5:
                /*initView();
                for(int i=0;i<weatherDataList.size();i++){
                    Log.d("test2","message:"+message.getMessage());
                    if(weatherDataList.get(i).getCity().equals(message.getMessage())){
                        final int finalI = i;
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                binding.vpMain.setCurrentItem(finalI);
                            }
                        },1000);

                        break;
                    }
                }*/
                for(int i=0;i<weatherDataList.size();i++){
                    if(weatherDataList.get(i).getCity().equals(WeatherFragment.city)){
                        ((WeatherFragment)fragmentList.get(i)).initView(true);
                        break;
                    }
                }
                break;
            case 6:
                Toast.makeText(MainActivity.this,"刷新完成",Toast.LENGTH_SHORT).show();
                for(int i=0;i<weatherDataList.size();i++){
                    if(weatherDataList.get(i).getCity().equals(WeatherFragment.city)){
                        ((WeatherFragment)fragmentList.get(i)).mBinding.swipeRefreshLayout.setRefreshing(false);
                        Log.d("test2",((WeatherFragment)fragmentList.get(i)).mBinding.toString());
                    }
                }
                break;
            default:
                break;
        }
    }

    public void toCityChoseActivity(View view){
        startActivityForResult(new Intent(MainActivity.this,CityChoseActivity.class),111);
    }

    public void muen(View view){
        binding.vpMain.setCurrentItem(fragmentList.size()-1);
    }

    public void delete(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
                .setTitle("简单的对话框")
                .setMessage("确定删除:"+binding.getMain().getCtiy()+"?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(weatherDataList.size()==1){
                            Toast.makeText(MainActivity.this,"请至少添加一个城市",Toast.LENGTH_LONG).show();
                        }else{
                            String delete=binding.getMain().getCtiy();
                            LitePal.deleteAll(WeatherData.class,"city=?",delete);
                            LitePal.deleteAll(Daily.class,"city=?",delete);
                            LitePal.deleteAll(Exponent.class,"city=?",delete);
                            LitePal.deleteAll(Hourly.class,"city=?",delete);
                            binding.getMain().setCurrent(0);
                            initView();
                        }
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .create()
                .show();

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&requestCode==111){
            initView();
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
