package com.example.mqttclient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.Map;

public class FullscreenActivity extends AppCompatActivity {
    private TextView tv_tianqi;
    private TextView tv_wendu;
    private TextView tv_fengxiang;
    private TextView tv_fengli;

    private String tianqi = "";
    private String wendu = "";
    private String fengli = "";
    private String fengxiang = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        tv_tianqi =(TextView) findViewById(R.id.tv_tianqi);
        tv_wendu =(TextView) findViewById(R.id.tv_wendu);
        tv_fengxiang =(TextView) findViewById(R.id.tv_fengxiang);
        tv_fengli =(TextView) findViewById(R.id.tv_fengli);
        Intent intent = getIntent();
        HeConfig.init("HE2306032135261284","bff28147ead8492b86de40d4d8307d3c");
        HeConfig.switchToDevService();
        queryWeather(intent.getStringExtra("CityID"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeWeather();
                            changeWeather();
                        }
                    });
                }
            }
        }).start();

    }

    public void queryWeather(String CityID){


        QWeather.getWeatherNow(FullscreenActivity.this, CityID, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener(){
            public static final String TAG="he_feng_now";
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:"+new Gson());
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                tianqi = wendu = fengli = fengxiang = "";
                //Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                //System.out.println("获取天气成功： " + new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    tianqi=now.getText();
                    wendu=now.getTemp()+"℃";
                    fengli=now.getWindScale();
                    fengxiang=now.getWindDir();
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    System.out.println("失败代码: " + code);
                    //Log.i(TAG, "failed code: " + code);
                }
            }

        });
    };

    public void changeWeather() {
        tv_tianqi.setText("当前天气:"+tianqi);
        tv_wendu.setText("当前温度:"+wendu);
        tv_fengxiang.setText("风向："+fengxiang);
        tv_fengli.setText("风力："+fengli+"级");
    }
}