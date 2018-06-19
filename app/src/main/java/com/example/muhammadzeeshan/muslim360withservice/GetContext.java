package com.example.muhammadzeeshan.muslim360withservice;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.AlarmReciever;

/**
 * Created by Muhammad Zeeshan on 4/20/2018.
 */

public class GetContext extends Application {

    Context context;

    public GetContext(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void cancelAlarm() {
        SharedPreference sharedPreference = new SharedPreference(context);
        Log.e("SharedPreferenceId", sharedPreference.getNotiId() + "");
        Intent intent2 = new Intent(context, AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent displayIntent = PendingIntent.getBroadcast(context, sharedPreference.getNotiId(), intent2, PendingIntent.FLAG_NO_CREATE);

        if(displayIntent != null){
            alarmManager.cancel(displayIntent);

        }
    }
}
