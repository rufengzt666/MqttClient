package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseCity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        Button GuangZhou = (Button) findViewById(R.id.GuangZhou);
        GuangZhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseCity.this, FullscreenActivity.class);
                intent.putExtra("CityID", "CN101280101");
                startActivity(intent);
            }
        });
        Button ZhongShan = (Button) findViewById(R.id.ZhongShan);
        ZhongShan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseCity.this, FullscreenActivity.class);
                intent.putExtra("CityID", "CN101070208");
                startActivity(intent);
            }
        });
    }
}