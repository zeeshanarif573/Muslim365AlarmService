package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/29/2018.
 */

public class MainData {

    String Id, Date, Azan, time, NotiType, tunePath;

    public MainData(String date, String azan, String time, String notiType, String tunePath) {
        Date = date;
        Azan = azan;
        this.time = time;
        NotiType = notiType;
        this.tunePath = tunePath;
    }

    public MainData() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAzan() {
        return Azan;
    }

    public void setAzan(String azan) {
        Azan = azan;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotiType() {
        return NotiType;
    }

    public void setNotiType(String notiType) {
        NotiType = notiType;
    }

    public String getTunePath() {
        return tunePath;
    }

    public void setTunePath(String tunePath) {
        this.tunePath = tunePath;
    }
}
