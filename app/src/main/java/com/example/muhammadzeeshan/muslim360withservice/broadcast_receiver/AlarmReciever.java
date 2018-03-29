package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;
import com.example.muhammadzeeshan.muslim360withservice.Alarm;
import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/15/2018.
 */

public class AlarmReciever extends BroadcastReceiver {

    Alarm setAlarm = new Alarm();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
        context.startService(new Intent(context, AlarmNotificationSoundService.class));
        setupAlarm(context, intent.getStringExtra("nearestTime"), intent.getIntExtra("reqCode", 0), true);
    }


    private void setupAlarm(Context context, String nearestTime, int requestCode, boolean updateStatusOrNot) {
        String strDate, date;
        int index;
        Calendar cal;
        String FajrTime;
        String nextNearestAlarm;

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Log.e("Intent: ", "nearest tIme: " + nearestTime);
        Log.e("Intent: ", "request code: " + requestCode);
        requestCode++;
        Log.e("Next Request Code: ", "" + requestCode);

        if (updateStatusOrNot)
            databaseHelper.UpdateTodayTimingTable(nearestTime);

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        Log.e("strDate", strDate);

        //Get Only Current Date....................
        Calendar onlyDate = Calendar.getInstance();
        date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));

        //Split Date into years, month and date.............
        String[] splitYear = strDate.split("\\/");
        String Year = splitYear[0];
        String Month = splitYear[1];
        String Date = splitYear[2];

        //Get Nearest Next Time.............................
        nextNearestAlarm = databaseHelper.getNearestTime(nearestTime);

        if (!nextNearestAlarm.isEmpty()) {

            Log.e("Runing", "if");
            Log.e("Next Nearest Alarm", nextNearestAlarm);

            //Split Time into Hours and Minutes...................
            String[] splitTime = nextNearestAlarm.split(" ");
            String splitPKT = splitTime[0];
            Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);

            String[] splitHour = splitPKT.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];
            Log.e("Split time on colon", hour + "/" + minute);


            //Set Alarm at Nearest date and time...............
            cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);

        } else {
            Log.e("Runing", "else");
            index = Integer.parseInt(date) + 1;

            Calendar calDate = Calendar.getInstance();
            String nextdate = calDate.get(Calendar.YEAR) + "/" + calDate.get(Calendar.MONTH) + "/" +index;

            List<TodaysData> getTodayDataList = databaseHelper.getTodaysData(String.valueOf(index));

            Log.e("listSize", getTodayDataList.size() + "");

            if (getTodayDataList.size() > 0) {

                TodaysData todaysData = getTodayDataList.get(0);

                FajrTime = todaysData.getFajr();
                nextNearestAlarm = FajrTime;

                Log.e("FajarTime", nextNearestAlarm);

                databaseHelper.deleteTodayAzanTimings();
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

                List<TodayTimings> todayTimingsList = new ArrayList<>();
                todayTimingsList.add(new TodayTimings(nextdate ,"Fajar", todaysData.getFajr(), "0"));
                todayTimingsList.add(new TodayTimings(nextdate ,"Dhuhr", todaysData.getDhuhr(), "0"));
                todayTimingsList.add(new TodayTimings(nextdate ,"Asr", todaysData.getAsr(), "0"));
                todayTimingsList.add(new TodayTimings(nextdate ,"Maghrib", todaysData.getMaghrib(), "0"));
                todayTimingsList.add(new TodayTimings(nextdate ,"Isha", todaysData.getIsha(), "0"));

                databaseHelper.insertIntoTodayTiming(todayTimingsList);
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

                Log.e("Next Nearest Alarm", nextNearestAlarm);

                //Split Time into Hours and Minutes...................
                String[] splitTime = nextNearestAlarm.split(" ");
                String splitPKT = splitTime[0];
                Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);

                String[] splitHour = splitPKT.split("\\:");
                String hour = splitHour[0];
                String minute = splitHour[1];
                Log.e("Split time on colon", hour + "/" + minute);


                //Set Alarm at Nearest date and time...............
                cal = Calendar.getInstance();
                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                        Integer.parseInt(minute), 00);

                Log.e("else date", cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "");

                Log.e("Next Day Alarm", "Next Alarm Set at : " + index + " - " + Month + " - " + Year + ":" + nextNearestAlarm);
                setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);


            } else {
                Log.e("TodaysData", "Empty");
            }
        }
    }

}