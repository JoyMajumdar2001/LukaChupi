package com.joy.lukachupi;

import com.google.firebase.Timestamp;

public class MsgModel {
    private String data;
    private Timestamp time;


    private MsgModel(){}

    private  MsgModel(String data, Timestamp time){
        this.data = data;
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
