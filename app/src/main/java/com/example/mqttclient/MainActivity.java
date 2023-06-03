package com.example.mqttclient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.mqttclient.mqtt.MqttService;
import android.speech.tts.TextToSpeech;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MqttService.MqttEventCallBack {
    private TextToSpeech tts = null;
    private TextView connectState;
    private MqttService.MqttBinder mqttBinder;
    private String TAG = "MainActivity";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder)iBinder;
            mqttBinder.setMqttEventCallBack(MainActivity.this);
            if(mqttBinder.isConnected()) {
                connectState.setText("已连接");
                subscribeTopics();
            }
            else {
                connectState.setText("未连接");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectState = findViewById(R.id.connect_state);

        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.CHINESE);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                    else {
                        Log.e("TTS", "Initialization failed");
                    }
                }

            }
        });


        findViewById(R.id.settings_btn).setOnClickListener((view)->{
            Button btn = (Button)findViewById(R.id.settings_btn);
            String content = btn.getText().toString();
            if(content != null && !content.isEmpty() && tts != null) {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.pubsub_test_btn).setOnClickListener((view)->{
            Button btn = (Button)findViewById(R.id.pubsub_test_btn);
            String content = btn.getText().toString();
            if(content != null && !content.isEmpty() && tts != null) {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
            Intent intent = new Intent(MainActivity.this, PubSubTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.dev_demo_btn).setOnClickListener((view)->{
            Button btn = (Button)findViewById(R.id.dev_demo_btn);
            String content = btn.getText().toString();
            if(content != null && !content.isEmpty() && tts != null) {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
            Intent intent = new Intent(MainActivity.this, DevicesDemoAcitvity.class);
            startActivity(intent);
        });

        findViewById(R.id.get_weather).setOnClickListener((view) -> {
            Button btn = (Button)findViewById(R.id.get_weather);
            String content = btn.getText().toString();
            if(content != null && !content.isEmpty() && tts != null) {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
            Intent intent = new Intent(MainActivity.this, ChooseCity.class);
            startActivity(intent);
        });

        findViewById(R.id.setting_info).setOnClickListener((view) -> {
            Button btn = (Button)findViewById(R.id.setting_info);
            String content = btn.getText().toString();
            if(content != null && !content.isEmpty() && tts != null) {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
            Intent intent = new Intent(MainActivity.this, TempShowActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.tts_switch).setOnClickListener((view) -> {
            Button btn = (Button)findViewById(R.id.tts_switch);
            String content = btn.getText().toString();
            if(tts == null) {
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if(i == TextToSpeech.SUCCESS) {
                            int result = tts.setLanguage(Locale.CHINESE);
                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Language not supported");
                            }
                            else {
                                Log.e("TTS", "Initialization failed");
                            }
                        }

                    }
                });
                if(content != null && !content.isEmpty() && tts != null) {
                    tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
                }
                btn.setText("语音播报：关闭");
            }
            else {
                if(content != null && !content.isEmpty() && tts != null) {
                    tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
                }
                btn.setText("语音播报：开启");
                tts = null;
            }

        });
    }

    void subscribeTopics() {
        try {
            mqttBinder.subscribe("/test");
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unSubscribeTopices() {
        try {
            mqttBinder.unSubscribe("/test");
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectSuccess() {
        subscribeTopics();
        connectState.setText("已连接");
    }

    @Override
    public void onConnectError(String error) {
        Log.d(TAG, "onConnectError: " + error);
        connectState.setText("未连接");
    }

    @Override
    public void onDeliveryComplete() {
        Log.d(TAG, "publish ok");
    }

    @Override
    public void onMqttMessage(String topic, String message) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mqttBinder.isConnected()) {
            connectState.setText("已连接");
            subscribeTopics();
        }
        else {
            connectState.setText("未连接");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribeTopices();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
