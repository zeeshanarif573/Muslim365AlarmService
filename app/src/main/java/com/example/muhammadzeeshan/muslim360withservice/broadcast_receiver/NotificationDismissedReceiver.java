package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, AlarmNotificationSoundService.class));
    }
}
