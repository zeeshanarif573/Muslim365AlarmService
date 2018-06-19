package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 5/2/2018.
 */

public class DailyVerse {

    String date, title, time, arabic, english;

    public DailyVerse(String date, String title, String time, String arabic, String english) {
        this.date = date;
        this.title = title;
        this.time = time;
        this.arabic = arabic;
        this.english = english;
    }

    public DailyVerse() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getArabic() {
        return arabic;
    }

    public void setArabic(String arabic) {
        this.arabic = arabic;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
