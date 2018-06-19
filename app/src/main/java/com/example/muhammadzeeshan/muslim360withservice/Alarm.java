package com.example.muhammadzeeshan.muslim360withservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.AlarmReciever;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


/**
 * Created by Muhammad Zeeshan on 3/20/2018.
 */

public class Alarm {

    public void setAlarm(Calendar targetCal, String nextNearestTime, Context context, int reqCode, AlarmParameters alarmParameters) {

        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("reqCode", reqCode);

        intent.putExtra("nearestTime", nextNearestTime);
        intent.putExtra("alarmParams", alarmParameters);

        SharedPreference sharedPreference = new SharedPreference(context);
        sharedPreference.setNotiId(reqCode);

        Log.e("RequestId", reqCode + "");
        Log.e("SharedPreferenceId", sharedPreference.getNotiId() + "");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }

        Log.e("Next Alarm Set at", alarmParameters.getTime());
        Log.e("Next Azan Name", alarmParameters.getAzanName());
    }

}