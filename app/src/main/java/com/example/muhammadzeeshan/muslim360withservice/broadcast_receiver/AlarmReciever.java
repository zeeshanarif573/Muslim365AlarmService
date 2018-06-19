package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Alarm;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.R;
import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Muhammad Zeeshan on 3/15/2018.
 */
public class AlarmReciever extends BroadcastReceiver {

    DatabaseHelper databaseHelper;
    Context context;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    NotificationChannel mChannel;
    NotificationManager alarmNotificationManager;
    MediaPlayer mediaPlayer;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        databaseHelper = new DatabaseHelper(context);

        if (intent.hasExtra("from")) {
            Log.e("AlarmReceiver", "Notification check");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("Please open the app to continue receiving prayer notifications");
            } else {
                sendNotification("Please open the app to continue receiving prayer notifications");
            }
        } else {
            AlarmParameters alarmParameters = (AlarmParameters) intent.getSerializableExtra("alarmParams");

            Log.e("TimeInAlarmReciever", alarmParameters.getTime() + "");

            Intent intent1 = new Intent(context, AlarmNotificationSoundService.class);
            intent1.putExtra("alarmParams", alarmParameters);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1);
            } else {
                context.startService(intent1);
            }

            setupAlarm(context, alarmParameters.getTime(), intent.getIntExtra("reqCode", 1));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("LongLogTag")
    private void setupAlarm(Context context, String nearestTime, int requestCode) {
        String strDate;

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Log.e("Intent: ", "nearest tIme: " + nearestTime);
        Log.e("Intent: ", "request code: " + requestCode);
        requestCode++;
        Log.e("Next Request Code: ", "" + requestCode);


        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("strDate", strDate);

        //Get Current Time................................
        String strTime = getCurrentTime();

        //Get Nearest Next Time.............................
        //   nearestTime = databaseHelper.getNearestTime(strTime);
        nearestTime = databaseHelper.getNearest(strTime);
        Log.e("Next Nearest Time: ", nearestTime);

        if (!nearestTime.isEmpty()) {

            Log.e("If", "Running");

            //Split Time into Hours and Minutes.................
            String[] splitTime = nearestTime.split(" ");
            String splitPKT = splitTime[0];

            String[] splitHour = nearestTime.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];

            //Split Date into years, month and date.............
            String[] splitYear = strDate.split("\\/");
            String Year = splitYear[0];
            String Month = splitYear[1];
            String Date = splitYear[2];

            //Set Alarm at Nearest date and time...............
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            AlarmParameters alarmParameters = databaseHelper.getAlarmParams(nearestTime);

            Log.e("alarmPArams", "Alarm: " + alarmParameters.getAzanName() + "\n" + " ,NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());
            Alarm alarm = new Alarm();
            alarm.setAlarm(cal, nearestTime, context, requestCode, alarmParameters);

        } else {

            Log.e("Else", "Running");

            databaseHelper.deleteTodayAzanTimings();
            getTodayDataFromMasterAndSaveInTodayData();

            int index = getOnlyNextDate();
            String nextdate = getNextDate();

            ArrayList<TodayTimings> todayTimingsArrayList = new ArrayList<>();

            List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(nextdate);
            if (mainDataList.size() > 0) {
                for (MainData mainData : mainDataList) {

                    String AzanTime = mainData.getTime().substring(0, 5);

                    TodayTimings todayTimings = new TodayTimings(nextdate, mainData.getAzan(), AzanTime, mainData.getNotiType(), mainData.getTunePath());
                    todayTimingsArrayList.add(todayTimings);

                    String Fajar = mainDataList.get(0).getTime();
                    nearestTime = Fajar;

                    databaseHelper.deleteTodayAzanTimings();
                    databaseHelper.insertIntoTodayTiming(todayTimingsArrayList);

                }

                Log.e("Data In Today Timing Status", " Updated");
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

                //Split Time into Hours and Minutes...................
                String[] splitTime = nearestTime.split(" ");
                String splitPKT = splitTime[0];

                String[] splitHour = nearestTime.split("\\:");
                String hour = splitHour[0];
                String minute = splitHour[1];

                //Split Date into years, month and date.............
                String[] splitYear = strDate.split("\\/");
                String Year = splitYear[0];
                String Month = splitYear[1];

                String SplitFajrTime = nearestTime.substring(0, 5);
                String FajrTime = SplitFajrTime;

                Log.e("Fajar_Time: ", FajrTime);

                //Set Alarm at Nearest date and time...............
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                        Integer.parseInt(minute), 00);

                AlarmParameters alarmParameters = databaseHelper.getAlarmParams(FajrTime);
                Log.e("alarmPArams", "Alarm: " + alarmParameters.getAzanName() + "\n" + " ,NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());
                Alarm alarm = new Alarm();
                alarm.setAlarm(cal, nearestTime, context, requestCode, alarmParameters);

                Log.e("Timing Updated", "Get Data After Main Data Inserted");

            } else {
                Log.e("AgentMainData", "Empty");
                settingNotificationAlarm();
            }

        }
    }

    private void settingNotificationAlarm() {
        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("from", "notification");
        Log.e("RequestId", 9090 + "");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 9090, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = 01;
        Log.e("Notification set ", "at: " + year + "," + month + "," + day);
        calendar.set(year, month, day, 0, 0, 5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }

    public String getCurrentTime() {

        //Get Current Time................................
        Calendar getTime = Calendar.getInstance();

        if ((getTime.get(Calendar.HOUR_OF_DAY) < 10) && (getTime.get(Calendar.MINUTE) < 10)) {
            String strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else if (getTime.get(Calendar.HOUR_OF_DAY) < 10) {
            String strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else if (getTime.get(Calendar.MINUTE) < 10) {
            String strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else {
            String strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;
        }
    }

    private String getNextDate() {
        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date) + 1;

        Calendar calDate = Calendar.getInstance();
        String nextdate = calDate.get(Calendar.YEAR) + "/" + calDate.get(Calendar.MONTH) + "/" + index;

        return nextdate;
    }

    private int getOnlyNextDate() {
        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date) + 1;

        return index;
    }

    private void getTodayDataFromMasterAndSaveInTodayData() {

        String nextdate = getNextDate();
        int onlyDate = getOnlyNextDate();

        List<TodayTimings> getTodayTimings = databaseHelper.getMergeTodayTiming(String.valueOf(onlyDate));

        for (TodayTimings todayTimings : getTodayTimings) {
            String[] timeSplit = todayTimings.getActualTime().split(" ");
            todayTimings.setActualTime(timeSplit[0]);
            todayTimings.setDate(nextdate);
            if (todayTimings.getNotiType() == null) {
                todayTimings.setNotiType("1");
            }
            if (todayTimings.getTunePath() == null) {
                todayTimings.setTunePath("");
            }
        }

        if (getTodayTimings.size() > 0) {
            databaseHelper.insertIntoTodayTiming(getTodayTimings);
            DatabaseUtils.peekAllDataFromTodayTimimgs(context);
            Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
        } else {
            Log.e("TodaysData", "Empty");
        }
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, 0);
        return pendingIntent;
    }

    private void sendNotification(String msg) {

        mediaPlayer = MediaPlayer.create(context, R.raw.notification);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

        alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.muhammadzeeshan.muslim360withservice");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.xhdpi)
                .setContentTitle("Reminder")
                .setWhen(5000)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(context, 1))
                .setDefaults(Notification.DEFAULT_SOUND);
        alamNotificationBuilder.setContentIntent(contentIntent);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String msg) {

        mediaPlayer = MediaPlayer.create(context, R.raw.notification);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(CHANNEL_ID);

        CharSequence name = context.getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.muhammadzeeshan.muslim360withservice");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.xxxhdpi)
                .setContentTitle("Reminder")
                .setContentText(msg)
                .setDeleteIntent(createOnDismissedIntent(context, 1))
                .setDefaults(Notification.DEFAULT_SOUND);
        notification.setContentIntent(contentIntent);

        notificationManager.createNotificationChannel(mChannel);

        notificationManager.notify(1, notification.build());

    }

}