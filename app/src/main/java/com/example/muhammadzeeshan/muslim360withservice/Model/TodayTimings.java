package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class TodayTimings {

    String Id, date, Azan, ActualTime;


    public TodayTimings(String date, String azan, String actualTime) {
        this.date = date;
        Azan = azan;
        ActualTime = actualTime;
    }

    public TodayTimings() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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
        return ActualTime;
    }

    public void setActualTime(String actualTime) {
        ActualTime = actualTime;
    }

}
