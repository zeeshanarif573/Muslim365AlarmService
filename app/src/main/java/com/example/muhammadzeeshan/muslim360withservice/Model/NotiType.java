package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/29/2018.
 */

public class NotiType {

    String Id, Date, Azan, Type;

    public NotiType(String date, String azan, String type) {
        Date = date;
        Azan = azan;
        Type = type;
    }

    public NotiType() {
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
}
