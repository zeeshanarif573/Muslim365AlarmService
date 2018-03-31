package com.example.muhammadzeeshan.muslim360withservice.Model;

/**
 * Created by Muhammad Zeeshan on 3/29/2018.
 */

public class NotiType {

    String Azan, Type, TunePath;

    public NotiType( String azan, String type,String tunePath) {
        Azan = azan;
        Type = type;
        TunePath = tunePath;
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

    public String getTunePath() {
        return TunePath;
    }

    public void setTunePath(String tunePath) {
        TunePath = tunePath;
    }
}
