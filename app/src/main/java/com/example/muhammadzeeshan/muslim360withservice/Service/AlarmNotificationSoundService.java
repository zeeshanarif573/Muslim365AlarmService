package com.example.muhammadzeeshan.muslim360withservice.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.MyApp;
import com.example.muhammadzeeshan.muslim360withservice.R;
import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.NotificationDismissedReceiver;

import java.io.File;

/**
 * Created by Muhammad Zeeshan on 3/26/2018.
 */

public class AlarmNotificationSoundService extends Service {

    ScreenOnOffReceiver broadCast;

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager alarmNotificationManager;

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String notiType = intent.getStringExtra("notiType");
        String tunePath = intent.getStringExtra("tunePath");

        File targetRingtone = new File(tunePath);

        if(notiType.equalsIgnoreCase("1")){
            mediaPlayer = MediaPlayer.create(this, Uri.parse(targetRingtone.getAbsolutePath()));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }


        Log.e("Service", "OnCreate");
        broadCast = new ScreenOnOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        this.registerReceiver(broadCast, filter);

        sendNotification("Wake Up! Wake Up! Alarm started!!");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("Service", "onDestroy");

        this.unregisterReceiver(broadCast);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void sendNotification(String msg) {

        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //get pending intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MyApp.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg).setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(this, NOTIFICATION_ID));
        alamNotificationBuilder.setContentIntent(contentIntent);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }

    public class ScreenOnOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Broadcast", "Receive");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e("Broadcast", "is off");
                stopService(new Intent(context, AlarmNotificationSoundService.class));
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.e("Broadcast", "is on");
                stopService(new Intent(context, AlarmNotificationSoundService.class));
            }
        }
    }
}
