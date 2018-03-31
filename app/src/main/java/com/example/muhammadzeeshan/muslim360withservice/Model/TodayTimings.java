package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class TodayTimings {

    String date, Azan, Time, NotiType, TunePath;


    public TodayTimings(String date, String azan, String time, String notiType, String tunePath) {
        this.date = date;
        Azan = azan;
        Time = time;
        NotiType = notiType;
        TunePath = tunePath;
    }

    public TodayTimings() {
    }

    public String getAzan() {
        return Azan;
    }

    public void setAzan(String azan) {
        Azan = azan;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActualTime() {
        return Time;
    }

    public void setActualTime(String actualTime) {
        Time = actualTime;
    }

    public String getNotiType() {
        return NotiType;
    }

    public void setNotiType(String notiType) {
        NotiType = notiType;
    }

    public String getTunePath() {
        return TunePath;
    }

    public void setTunePath(String tunePath) {
        TunePath = tunePath;
    }
}
