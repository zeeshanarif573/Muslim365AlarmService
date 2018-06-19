package com.example.muhammadzeeshan.muslim360withservice.Model;

import java.io.Serializable;

/**
 * Created by Muhammad Zeeshan on 3/31/2018.
 */

public class AlarmParameters implements Serializable {

    String AzanName, Time, NotiType, TunePath;

    public AlarmParameters() {
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAzanName() {
        return AzanName;
    }

    public void setAzanName(String azanName) {
        AzanName = azanName;
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
