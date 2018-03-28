package com.example.muhammadzeeshan.muslim360withservice.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Muhammad Zeeshan on 3/26/2018.
 */

public class ScreenBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast", "Receive");

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.e("Broadcast", "is off");
        }
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.e("Broadcast", "is on");
        }
    }
}
