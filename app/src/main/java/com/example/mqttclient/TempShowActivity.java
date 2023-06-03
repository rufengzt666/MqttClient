package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class TempShowActivity extends AppCompatActivity {

    //定义画图框变量
    private LineCharFunction dynamicLineChartManager1;
    private List<Integer> list = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);

        //寻找控件ID
        final LineChart mChart1 = findViewById(R.id.dynamic_chart1);
        final Button button_data1 = findViewById(R.id.button_data1);

        //曲线名字
        names.add("温度");
        names.add("湿度");
        names.add("gas");

        //曲线颜色
        colour.add(Color.GREEN);
        colour.add(Color.CYAN);
        colour.add(Color.BLUE);
        dynamicLineChartManager1 = new LineCharFunction(mChart1, names, colour);
        dynamicLineChartManager1.setYAxis(100, 0, 10);//最大值，最小值，中间刻度值的数量

        //死循环添加数据
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
                            list.add((int) (Math.random() * 50) + 10);
                            list.add((int) (Math.random() * 80) + 10);
                            list.add((int) (Math.random() * 100));
                            dynamicLineChartManager1.addEntry(list);
                            list.clear();
                        }
                    });
                }
            }
        }).start();

        //按下按键实现的功能
        button_data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //数据传入端口
                dynamicLineChartManager1.addEntry((int) (Math.random() * 100));
            }
        });
    }

}