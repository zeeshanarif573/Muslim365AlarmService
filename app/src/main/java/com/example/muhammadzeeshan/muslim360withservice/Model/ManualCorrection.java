package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/29/2018.
 */

public class ManualCorrection {

    String id, date, azan , time;

    public ManualCorrection(String date, String azan, String time) {
        this.date = date;
        this.azan = azan;
        this.time = time;
    }

    public ManualCorrection() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAzan() {
        return azan;
    }

    public void setAzan(String azan) {
        this.azan = azan;
    }

    public String getTiming() {
        return time;
    }

    public void setTiming(String timing) {
        this.time = timing;
    }
}
