package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/29/2018.
 */

public class NotiType {

    String Date, Azan, Type, TunePath;

    public NotiType(String date, String azan, String type, String tunePath) {
        Date = date;
        Azan = azan;
        Type = type;
        TunePath = tunePath;
    }

    public NotiType() {
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTunePath() {
        return TunePath;
    }

    public void setTunePath(String tunePath) {
        TunePath = tunePath;
    }
}
