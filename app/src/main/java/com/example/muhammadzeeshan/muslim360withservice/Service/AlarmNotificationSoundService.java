package com.example.muhammadzeeshan.muslim360withservice.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.R;
import com.example.muhammadzeeshan.muslim360withservice.SharedPreference;
import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.NotificationDismissedReceiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Muhammad Zeeshan on 3/26/2018.
 */

public class AlarmNotificationSoundService extends Service {

    ScreenOnOffReceiver broadCast;
    MediaPlayer mediaPlayer;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    NotificationChannel mChannel;
    SharedPreference sharedPreference;
    private AlarmParameters alarmParameters;
    private NotificationManager alarmNotificationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreference = new SharedPreference(AlarmNotificationSoundService.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, new Notification());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.hasExtra("alarmParams")) {
                alarmParameters = (AlarmParameters) intent.getSerializableExtra("alarmParams");

                File targetRingtone = new File(alarmParameters.getTunePath());

                if (alarmParameters.getNotiType().equalsIgnoreCase("1")) {

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(targetRingtone.getAbsolutePath()));
                    try {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.start();

                        Log.e("GetAbsolutePath", targetRingtone.getAbsolutePath() + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Log.e("if Condition", "Version");
                    createNotificationChannel("It's " + alarmParameters.getAzanName() + " time in your city.");
                } else {
                    Log.e("Else Condition", "Version");
                    sendNotification("It's " + alarmParameters.getAzanName() + " time in your city.");
                }

                Log.e("Service", "OnCreate");
                broadCast = new ScreenOnOffReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_SCREEN_ON);
                this.registerReceiver(broadCast, filter);

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Log.e("if Condition", "Version");
                    createNotificationChannel("It's " + alarmParameters.getAzanName() + " time in your city.");
                } else {
                    Log.e("Else Condition", "Version");
                    sendNotification("It's " + alarmParameters.getAzanName() + " time in your city.");
                }
            }

        }

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

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.muhammadzeeshan.muslim360withservice");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this)
                .setContentTitle(alarmParameters.getAzanName() + ": " + getAmPMFormatTime(alarmParameters.getTime()))
                .setSmallIcon(R.drawable.xhdpi)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(this, sharedPreference.getNotiId()));
        alamNotificationBuilder.setContentIntent(contentIntent);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(sharedPreference.getNotiId(), alamNotificationBuilder.build());
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, 0);
        return pendingIntent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String msg) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(CHANNEL_ID);

        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.muhammadzeeshan.muslim360withservice");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.xxxhdpi)
                .setContentTitle(alarmParameters.getAzanName() + ": " + getAmPMFormatTime(alarmParameters.getTime()))
                .setContentText(msg)
                .setDeleteIntent(createOnDismissedIntent(this, sharedPreference.getNotiId()));
        notification.setContentIntent(contentIntent);

        notificationManager.createNotificationChannel(mChannel);

        notificationManager.notify(sharedPreference.getNotiId(), notification.build());

    }

    private String getAmPMFormatTime(String time) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(time);
            System.out.println(_24HourDt);
            System.out.println(_12HourSDF.format(_24HourDt));
            String newTime = _12HourSDF.format(_24HourDt);
            return newTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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