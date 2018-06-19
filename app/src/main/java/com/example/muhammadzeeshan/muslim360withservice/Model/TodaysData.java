package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/19/2018.
 */

public class TodaysData {

    String Date, fajr , Dhuhr , Asr,  Maghrib, Isha, Imsak;

    public TodaysData(String date, String fajr, String dhuhr, String asr, String maghrib, String isha, String imsak) {
        Date = date;
        this.fajr = fajr;
        Dhuhr = dhuhr;
        Asr = asr;
        Maghrib = maghrib;
        Isha = isha;
        Imsak = imsak;
    }

    public TodaysData() {
    }

    public String getImsak() {
        return Imsak;
    }

    public void setImsak(String imsak) {
        Imsak = imsak;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getDhuhr() {
        return Dhuhr;
    }

    public void setDhuhr(String dhuhr) {
        Dhuhr = dhuhr;
    }

    public String getAsr() {
        return Asr;
    }

    public void setAsr(String asr) {
        Asr = asr;
    }

    public String getMaghrib() {
        return Maghrib;
    }

    public void setMaghrib(String maghrib) {
        Maghrib = maghrib;
    }

    public String getIsha() {
        return Isha;
    }

    public void setIsha(String isha) {
        Isha = isha;
    }

}
