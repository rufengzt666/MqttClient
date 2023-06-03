package com.example.mqttclient.protocol;

public class AirConfitionigMessage extends BaseMessage{
    public float value;
    public boolean state;
    public AirConfitionigMessage(boolean state, float value) {
        this.state = state;
        this.value = value;
        this.type = Type.AIR_CONDITIONING.index;
    }
}
