package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mqttclient.mqtt.MqttService;
import com.example.mqttclient.protocol.AirConfitionigMessage;
import com.example.mqttclient.protocol.BoolMessage;
import com.example.mqttclient.protocol.FloatMessage;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

public class DevicesDemoAcitvity extends AppCompatActivity implements MqttService.MqttEventCallBack,
        CompoundButton.OnCheckedChangeListener {

    private TextView connectState, temperatureValue, humidityValue, pmValue, gasValue, doorStatus;
    private EditText airConditioningValue;
    private MqttService.MqttBinder mqttBinder;
    private String TAG = "MainActivity";
    private Switch parlourLightSwitch, curtain_switch, fan_socket_switch, air_conditioning_switch;
    private Map<String, Integer> subscribeTopics = new HashMap<>();

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder)iBinder;
            mqttBinder.setMqttEventCallBack(DevicesDemoAcitvity.this);
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

    void subscribeTopics() {
        try {
            subscribeTopics.put("/test/temp", 1);
            subscribeTopics.put("/test/hum", 2);
            subscribeTopics.put("/test/pm", 3);
            subscribeTopics.put("/test/gas", 4);
            subscribeTopics.put("/test/door", 5);
            for(Map.Entry<String, Integer>entry:subscribeTopics.entrySet()) {
                mqttBinder.subscribe(entry.getKey());
            }
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unSubscribeTopics() {
        try{
            for(Map.Entry<String, Integer>entry : subscribeTopics.entrySet()) {
                mqttBinder.unSubscribe(entry.getKey());
            }
            subscribeTopics.clear();
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_demo);

        connectState = findViewById(R.id.dev_connect_state);

        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);

        temperatureValue = findViewById(R.id.temperature_value);

        humidityValue = findViewById(R.id.humidity_value);
        pmValue = findViewById(R.id.pm_value);
        gasValue = findViewById(R.id.gas_value);
        doorStatus = findViewById(R.id.door_status);

        airConditioningValue = findViewById(R.id.air_conditioning_value);
        parlourLightSwitch = findViewById(R.id.parlour_light_switch);
        parlourLightSwitch.setOnCheckedChangeListener(this);
        curtain_switch = findViewById(R.id.curtain_switch);
        curtain_switch.setOnCheckedChangeListener(this);
        fan_socket_switch = findViewById(R.id.fan_socket_switch);
        fan_socket_switch.setOnCheckedChangeListener(this);
        air_conditioning_switch = findViewById(R.id.air_conditioning_switch);
        air_conditioning_switch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.parlour_light_switch:
                try {
                    if(compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/light1",
                                new Gson().toJson(new BoolMessage(true)));
                    }else {
                        mqttBinder.publishMessage("/test/light1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.curtain_switch:
                try {
                    if(compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/curtain1",
                                new Gson().toJson(new BoolMessage(true)));
                    }else {
                        mqttBinder.publishMessage("/test/curtain1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fan_socket_switch:
                try {
                    if(compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/fan1",
                                new Gson().toJson(new BoolMessage(true)));
                    }else {
                        mqttBinder.publishMessage("/test/fan1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.air_conditioning_switch:
                try {
                    if(compoundButton.isChecked()) {
                        String json = new Gson().toJson(new AirConfitionigMessage(true,
                                Float.parseFloat(airConditioningValue.getText().toString())));
                        Log.d("json", json);
                        mqttBinder.publishMessage("/test/airConditioning", json);
                    }else {
                        String json = new Gson().toJson(new AirConfitionigMessage(false,
                                Float.parseFloat(airConditioningValue.getText().toString())));
                        Log.d("json", json);
                        mqttBinder.publishMessage("/test/airConditioning", json);
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
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
        subscribeTopics.clear();
    }

    @Override
    public void onDeliveryComplete() {
        Log.d(TAG, "publish ok");
    }

    @Override
    public void onMqttMessage(String topic, String message) {
        Log.d("onMqttMessage", "topic:" + topic + "message length:" + message.length() +
                ", message:" + message);
        Gson gson = new Gson();
        switch (subscribeTopics.get(topic)) {
            case 1:
                temperatureValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        FloatMessage.class).value));
                break;
            case 2:
                humidityValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        FloatMessage.class).value));
                break;
            case 3:
                pmValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        FloatMessage.class).value));
                break;
            case 4:
                gasValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        FloatMessage.class).value));
                if(gson.fromJson(message.trim(), FloatMessage.class).value > 25) {
                    String id = "chanal_1";
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel =
                                new NotificationChannel(id,"123", NotificationManager.IMPORTANCE_HIGH);
                        manager.createNotificationChannel(notificationChannel);
                        Notification notification = new Notification.Builder(this, id)
                                .setContentTitle("WORING!!!")
                                .setContentText("警告，可燃气体浓度过高，请注意!!!")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .build();
                        manager.notify(1, notification);
                    }
                }
                break;
            case 5:
                String status = gson.fromJson(message.trim(), BoolMessage.class).value ? "开" : "关";
                doorStatus.setText(status);
                break;
        }
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
        unSubscribeTopics();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}