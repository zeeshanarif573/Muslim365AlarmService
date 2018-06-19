package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class Timings {

    int date;
    String Id, azan, time;


    public Timings(int date, String azan, String time) {
        this.date = date;
        this.azan = azan;
        this.time = time;
    }


    public Timings() {
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAzan() {
        return azan;
    }

    public void setAzan(String azan) {
        this.azan = azan;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
