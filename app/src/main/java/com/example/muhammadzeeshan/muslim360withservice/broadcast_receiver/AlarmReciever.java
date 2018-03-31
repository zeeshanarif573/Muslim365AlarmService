package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Alarm;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/15/2018.
 */

public class AlarmReciever extends BroadcastReceiver {

    DatabaseHelper databaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        databaseHelper = new DatabaseHelper(context);

        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
        context.startService(new Intent(context, AlarmNotificationSoundService.class));
        setupAlarm(context, intent.getStringExtra("nearestTime"), intent.getIntExtra("reqCode", 0), true);
    }


    @SuppressLint("LongLogTag")
    private void setupAlarm(Context context, String nearestTime, int requestCode, boolean updateStatusOrNot) {
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
        nearestTime = databaseHelper.getNearestTime(strTime);

        if (!nearestTime.isEmpty()) {

            Log.e("If", "Running");

            //Split Time into Hours and Minutes.................
            String[] splitTime = nearestTime.split(" ");
            String splitPKT = splitTime[0];
            Log.e("Split PKT", splitPKT);

            String[] splitHour = splitPKT.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];
            Log.e("Split Time", hour + "_" + minute);

            //Split Date into years, month and date.............
            String[] splitYear = strDate.split("\\/");
            String Year = splitYear[0];
            String Month = splitYear[1];
            String Date = splitYear[2];

            //Set Alarm at Nearest date and time...............
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            Alarm alarm = new Alarm();
            alarm.setAlarm(cal, nearestTime, context, 0);

        } else {

            Log.e("Else", "Running");

            ArrayList<TodayTimings> todayTimingsArrayList = new ArrayList<>();

            //Get Only Current Date....................
            Calendar onlyDate = Calendar.getInstance();
            String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
            int index = Integer.parseInt(date) + 1;

            Calendar calDate = Calendar.getInstance();
            String nextdate = calDate.get(Calendar.YEAR) + "/" + calDate.get(Calendar.MONTH) + "/" + index;

            databaseHelper.deleteTodayAzanTimings();
            DatabaseUtils.peekAllDataFromTodayTimimgs(context);

            List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(nextdate);

            if (mainDataList.size() > 0) {
                for (MainData mainData : mainDataList) {

                    String AzanTime = mainData.getTime().substring(0, 5);

                    TodayTimings todayTimings = new TodayTimings(nextdate, mainData.getAzan(), AzanTime, mainData.getNotiType(), mainData.getTunePath());
                    todayTimingsArrayList.add(todayTimings);

                    String Fajar = mainDataList.get(0).getAzan();
                    nearestTime = Fajar;

                    databaseHelper.deleteTodayAzanTimings();
                    databaseHelper.insertIntoTodayTiming(todayTimingsArrayList);

                    //Split Time into Hours and Minutes...................
                    String[] splitTime = nearestTime.split(" ");
                    String splitPKT = splitTime[0];
                    Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);

                    String[] splitHour = splitPKT.split("\\:");
                    String hour = splitHour[0];
                    String minute = splitHour[1];
                    Log.e("Split time on colon", hour + "/" + minute);

                    //Split Date into years, month and date.............
                    String[] splitYear = strDate.split("\\/");
                    String Year = splitYear[0];
                    String Month = splitYear[1];

                    //Set Alarm at Nearest date and time...............
                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                            Integer.parseInt(minute), 00);

                    Log.e("Next Alarm Set Tomorrow at: ", +index + " - " + Month + " - " + Year + ":" + nearestTime);

                    Alarm alarm = new Alarm();
                    alarm.setAlarm(cal, nearestTime, context, 0);
                }

                Log.e("Timing Updated", "Get Data After Main Data Inserted");
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

            } else {
                Log.e("AgentMainData", "Empty");
            }

            //    List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(index));

//            if (getTodayDataList.size() > 0) {
//                for (int i = 0; i < getTodayDataList.size(); i++) {
//
//                //    ArrayList<NotiType> notiTypeArrayList = prepareNotiTypeList();
//                    TodaysData todaysData = getTodayDataList.get(i);
//
//                    String FajrTime = todaysData.getFajr();
//                    nearestTime = FajrTime;
//
//                    databaseHelper.deleteTodayAzanTimings();
//
//                    List<TodayTimings> todayTimingsList = new ArrayList<>();
//
//                    todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(0).getAzan(), todaysData.getFajr(), notiTypeArrayList.get(0).getType(), notiTypeArrayList.get(0).getTunePath()));
//                    todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(1).getAzan(), todaysData.getDhuhr(), notiTypeArrayList.get(1).getType(), notiTypeArrayList.get(1).getTunePath()));
//                    todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(2).getAzan(), todaysData.getAsr(), notiTypeArrayList.get(2).getType(), notiTypeArrayList.get(2).getTunePath()));
//                    todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(3).getAzan(), todaysData.getMaghrib(), notiTypeArrayList.get(3).getType(), notiTypeArrayList.get(3).getTunePath()));
//                    todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(4).getAzan(), todaysData.getIsha(), notiTypeArrayList.get(4).getType(), notiTypeArrayList.get(4).getTunePath()));
//
//
//                    databaseHelper.insertIntoTodayTiming(todayTimingsList);
//                    DatabaseUtils.peekAllDataFromTodayTimimgs(context);
//
//                    //Split Time into Hours and Minutes...................
//                    String[] splitTime = nearestTime.split(" ");
//                    String splitPKT = splitTime[0];
//                    Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);
//
//                    String[] splitHour = splitPKT.split("\\:");
//                    String hour = splitHour[0];
//                    String minute = splitHour[1];
//                    Log.e("Split time on colon", hour + "/" + minute);
//
//
//                    //Set Alarm at Nearest date and time...............
//                    Calendar cal = Calendar.getInstance();
//                    cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
//                            Integer.parseInt(minute), 00);
//
//                    Log.e("Next Day Alarm", "Next Alarm Set at : " + index + " - " + Month + " - " + Year + ":" + nearestTime);
//
//                    Alarm alarm = new Alarm();
//                    alarm.setAlarm(cal, nearestTime, context, 0);
//                }
//
//            } else {
//                Log.e("TodaysData", "Empty");
//
//            }
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

}