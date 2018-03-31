package com.example.muhammadzeeshan.muslim360withservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.AlarmReciever;

import java.util.Calendar;


/**
 * Created by Muhammad Zeeshan on 3/20/2018.
 */

public class Alarm {

    public void setAlarm(Calendar targetCal, String nextNearestAlarm, Context context, int reqCode, AlarmParameters alarmParameters) {
        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("nearestTime", nextNearestAlarm);
        intent.putExtra("reqCode", reqCode);
        intent.putExtra("notiType", alarmParameters.getNotiType());
        intent.putExtra("tunePath", alarmParameters.getTunePath());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

        Log.e("Next Alarm Set at" , nextNearestAlarm);

    }

}
