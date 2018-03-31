package com.example.muhammadzeeshan.muslim360withservice;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.JsonHandler.BackgroundList;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;
import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;
import com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver.AlarmReciever;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class MyApp extends Application {

    ArrayList<Timings> timingsList;
    public TextView textView;
    List<TodayTimings> todayTimingsList;
    String FajrTime, DhuhrTime, AsrTime, MaghribTime, IshaTime;
    DatabaseHelper databaseHelper;
    final static int RQS_1 = 1;
    String strDate, strTime, date;
    String nearestTime;
    int index;
    BackgroundList backgroundList;


    @Override
    public void onCreate() {
        super.onCreate();

        /*init();
        Log.e("MainActivity", "OnCreate");

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("Date..", strDate);

        //Get Only Current Date....................
        Calendar onlyDate = Calendar.getInstance();
        date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        index = Integer.parseInt(date);

        //Split Date into years, month and date.............
        String[] splitYear = strDate.split("\\/");
        String Year = splitYear[0];
        String Month = splitYear[1];
        String Date = splitYear[2];

        InsertingIntoTiming();
        getDatafromMaster();
        InsertingIntoTodayTiming();


        //Get Current Time................................
        Calendar getTime = Calendar.getInstance();
        if ((getTime.get(Calendar.HOUR_OF_DAY) < 10) && (getTime.get(Calendar.MINUTE) < 10)) {
            strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);

        } else if (getTime.get(Calendar.HOUR_OF_DAY) < 10) {
            strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
        } else if (getTime.get(Calendar.MINUTE) < 10) {
            strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
        } else {
            strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
        }

        //Get Nearest Next Time.............................
        nearestTime = databaseHelper.getNearestTime(strTime);
        Log.e("Nearest_Time ", nearestTime);

        if (!nearestTime.isEmpty()) {

            //Split Time into Hours and Minutes.................
            String[] splitTime = nearestTime.split(" ");
            String splitPKT = splitTime[0];
            Log.e("Split PKT", splitPKT);

            String[] splitHour = splitPKT.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];
            Log.e("Split Time", hour + "_" + minute);

            //Set Alarm at Nearest date and time...............
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            setAlarm(cal);

        } else {
            index = Integer.parseInt(date) + 1;

            List<TodaysData> getTodayDataList = databaseHelper.getTodaysData(String.valueOf(index));

            if (getTodayDataList.size() > 0) {
                for (TodaysData todaysData : getTodayDataList) {

                    FajrTime = todaysData.getFajr();
                    DhuhrTime = todaysData.getDhuhr();
                    AsrTime = todaysData.getAsr();
                    MaghribTime = todaysData.getMaghrib();
                    IshaTime = todaysData.getIsha();

                    nearestTime = FajrTime;

                    Log.e("TodaysData", "Today's Date: " + todaysData.getDate());
                    Log.e("TodaysData", "Fajar Time: " + todaysData.getFajr());
                    Log.e("TodaysData", "Dhuhr Time: " + todaysData.getDhuhr());
                    Log.e("TodaysData", "Asr Time: " + todaysData.getAsr());
                    Log.e("TodaysData", "Maghrib Time: " + todaysData.getMaghrib());
                    Log.e("TodaysData", "Isha Time: " + todaysData.getIsha());
                }

            } else {
                Log.e("TodaysData", "Empty");
            }
        }*/
    }

    void init() {

        backgroundList = new BackgroundList(this);
        databaseHelper = new DatabaseHelper(this);
        timingsList = new ArrayList<>();
        todayTimingsList = new ArrayList<>();
    }

    private void InsertingIntoTiming() {

        Log.e("Inserting...", "Inserting Into Timing...");

        timingsList = backgroundList.getJson();

        Log.e("Retrieve...", "Data Retrieve Successfully...");
    }

    public void getDatafromMaster() {
        List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(index));

        if (getTodayDataList.size() > 0) {
            for (TodaysData todaysData : getTodayDataList) {

                FajrTime = todaysData.getFajr();
                DhuhrTime = todaysData.getDhuhr();
                AsrTime = todaysData.getAsr();
                MaghribTime = todaysData.getMaghrib();
                IshaTime = todaysData.getIsha();

                Log.e("TodaysData", "Today's Date: " + todaysData.getDate());
                Log.e("TodaysData", "Fajar Time: " + todaysData.getFajr());
                Log.e("TodaysData", "Dhuhr Time: " + todaysData.getDhuhr());
                Log.e("TodaysData", "Asr Time: " + todaysData.getAsr());
                Log.e("TodaysData", "Maghrib Time: " + todaysData.getMaghrib());
                Log.e("TodaysData", "Isha Time: " + todaysData.getIsha());

            }
        } else {
            Log.e("TodaysData", "Empty");
        }
    }

    private void InsertingIntoTodayTiming() {

        Log.e("Inserting...", "Inserting Into Today Timing...");

//        todayTimingsList.add(new TodayTimings(strDate, "Fajar", FajrTime, "0"));
//        todayTimingsList.add(new TodayTimings(strDate, "Dhuhr", DhuhrTime, "0"));
//        todayTimingsList.add(new TodayTimings(strDate, "Asr", AsrTime, "0"));
//        todayTimingsList.add(new TodayTimings(strDate, "Maghrib", MaghribTime, "0"));
//        todayTimingsList.add(new TodayTimings(strDate, "Isha", IshaTime, "0"));
//
//        databaseHelper.insertIntoTodayTiming(todayTimingsList);
//        DatabaseUtils.peekAllDataFromTodayTimimgs(this);

        Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
    }

    private void setAlarm(Calendar targetCal) {

        Intent intent = new Intent(getBaseContext(), AlarmReciever.class);
        intent.putExtra("nearestTime", nearestTime);
        intent.putExtra("reqCode", RQS_1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

        Log.e("Alarm Set at:", nearestTime);
    }

}