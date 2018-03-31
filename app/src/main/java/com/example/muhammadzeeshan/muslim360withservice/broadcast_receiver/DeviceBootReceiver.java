package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;
import com.example.muhammadzeeshan.muslim360withservice.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/21/2018.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    Alarm setAlarm = new Alarm();
    String nextNearestAlarm;
    DatabaseHelper databaseHelper;
    String FajrTime;
    int index;
    String currentDate, currentTime;

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(final Context context, Intent intent) {

        databaseHelper = new DatabaseHelper(context);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Toast.makeText(context, "On Boot condition", Toast.LENGTH_SHORT).show();

            //Get Whole Current Date....................
            Calendar getDate = Calendar.getInstance();
            currentDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

            Log.e("Boot Current Date", currentDate);

            //Get Current Time................................
            Calendar getTime = Calendar.getInstance();
            if ((getTime.get(Calendar.HOUR_OF_DAY) < 10) && (getTime.get(Calendar.MINUTE) < 10)) {
                currentTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
                Log.e("Boot Reciever Time..", currentTime);

            } else if (getTime.get(Calendar.HOUR_OF_DAY) < 10) {
                currentTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
                Log.e("Boot Reciever Time..", currentTime);
            } else if (getTime.get(Calendar.MINUTE) < 10) {
                currentTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
                Log.e("Boot Reciever Time..", currentTime);
            } else {
                currentTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
                Log.e("Boot Reciever Time..", currentTime);
            }


            if (databaseHelper.getDateCount(currentDate) > 0) {

                Log.e("Running Boot", "if");
                setupAlarm2(context, currentDate, currentTime, 100);

            } else {
                Log.e("Running Boot", "else");

                databaseHelper.deleteTodayAzanTimings();
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

                List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(getDate.get(Calendar.DAY_OF_MONTH)));
                Log.e("listSize", getTodayDataList.size() + "");

//                if (getTodayDataList.size() > 0) {
//                    TodaysData todaysData = getTodayDataList.get(0);
//
//                    List<TodayTimings> todayTimingsList = new ArrayList<>();
//                    todayTimingsList.add(new TodayTimings(currentDate, "Fajar", todaysData.getFajr()));
//                    todayTimingsList.add(new TodayTimings(currentDate, "Dhuhr", todaysData.getDhuhr()));
//                    todayTimingsList.add(new TodayTimings(currentDate, "Asr", todaysData.getAsr()));
//                    todayTimingsList.add(new TodayTimings(currentDate, "Maghrib", todaysData.getMaghrib()));
//                    todayTimingsList.add(new TodayTimings(currentDate, "Isha", todaysData.getIsha()));
//
//                    databaseHelper.insertIntoTodayTiming(todayTimingsList);
//                    DatabaseUtils.peekAllDataFromTodayTimimgs(context);
//
//                    setupAlarm2(context, currentDate, currentTime, 100);
//                }
            }

        }
    }

    private void setupAlarm2(Context context, String strDate, String currentTime, int requestCode) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Log.e("Intent: ", "nearest tIme: " + currentTime);
        Log.e("Intent: ", "request code: " + requestCode);
        requestCode++;
        Log.e("Next Request Code: ", "" + requestCode);

        String[] splitYear = strDate.split("\\/");
        String Year = splitYear[0];
        String Month = splitYear[1];
        String Date = splitYear[2];

        int date = Integer.parseInt(Date);

          //Get Nearest Next Time.............................
//        nextNearestAlarm = databaseHelper.getNearestTime(currentTime);
//
//        if (!nextNearestAlarm.isEmpty()) {
//
//            Log.e("Next Nearest Alarm", nextNearestAlarm);
//
//            //Split Time into Hours and Minutes...................
//            String[] splitTime = nextNearestAlarm.split(" ");
//            String splitPKT = splitTime[0];
//            Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);
//
//            String[] splitHour = splitPKT.split("\\:");
//            String hour = splitHour[0];
//            String minute = splitHour[1];
//            Log.e("Split time on colon", hour + "/" + minute);
//
//
//            //Set Alarm at Nearest date and time...............
//            Calendar cal = Calendar.getInstance();
//            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
//                    Integer.parseInt(hour), Integer.parseInt(minute), 00);
//            setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);
//
//        } else {
//            Log.e("Runing", "else");
//            index = date + 1;
//
//            Calendar getDate = Calendar.getInstance();
//            String nextdate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" +index;
//
//
//            List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(index));
//
//            Log.e("listSize", getTodayDataList.size() + "");
//
//            if (getTodayDataList.size() > 0) {
//
//                TodaysData todaysData = getTodayDataList.get(0);
//
//                FajrTime = todaysData.getFajr();
//                nextNearestAlarm = FajrTime;
//
//                Log.e("FajarTime", nextNearestAlarm);
//
//                databaseHelper.deleteTodayAzanTimings();
//                DatabaseUtils.peekAllDataFromTodayTimimgs(context);
//
//                List<TodayTimings> todayTimingsList = new ArrayList<>();
//                todayTimingsList.add(new TodayTimings(nextdate, "Fajar", todaysData.getFajr(), "0"));
//                todayTimingsList.add(new TodayTimings(nextdate, "Dhuhr", todaysData.getDhuhr(), "0"));
//                todayTimingsList.add(new TodayTimings(nextdate, "Asr", todaysData.getAsr(), "0"));
//                todayTimingsList.add(new TodayTimings(nextdate, "Maghrib", todaysData.getMaghrib(), "0"));
//                todayTimingsList.add(new TodayTimings(nextdate, "Isha", todaysData.getIsha(), "0"));
//
//                databaseHelper.insertIntoTodayTiming(todayTimingsList);
//                DatabaseUtils.peekAllDataFromTodayTimimgs(context);
//
//                Log.e("Next Nearest Alarm", nextNearestAlarm);
//
//                //Split Time into Hours and Minutes...................
//                String[] splitTime = nextNearestAlarm.split(" ");
//                String splitPKT = splitTime[0];
//                Log.e("Next Nearest Alarm", "Split on ' ' " + splitPKT);
//
//                String[] splitHour = splitPKT.split("\\:");
//                String hour = splitHour[0];
//                String minute = splitHour[1];
//                Log.e("Split time on colon", hour + "/" + minute);
//
//
//                //Set Alarm at Nearest date and time...............
//                Calendar cal = Calendar.getInstance();
//                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
//                        Integer.parseInt(minute), 00);
//
//                Log.e("else date", cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "");
//
//                Log.e("Next Day Alarm", "Next Alarm Set at : " + index + " - " + Month + " - " + Year + ":" + nextNearestAlarm);
//                setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);
//            } else {
//                Log.e("TodaysData", "Empty");
//            }
//        }

    }

    /*private void setupAlarm(Context context, String nearestTime, int requestCode, boolean updateStatusOrNot) {
        String strDate, date;
        int index;
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

        //Get Only Current Date....................
        Calendar onlyDate = Calendar.getInstance();
        date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));

        index = Integer.parseInt(date);

        //Split Date into years, month and date.............
        String[] splitYear = strDate.split("\\/");
        String Year = splitYear[0];
        String Month = splitYear[1];
        String Date = splitYear[2];

        //Get Nearest Next Time.............................
        nextNearestAlarm = databaseHelper.getNearestTime(nearestTime);

        if (!nextNearestAlarm.isEmpty()) {

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
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);

        } else {

            Log.e("Runing", "else");
            index = Integer.parseInt(date) + 1;

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
                todayTimingsList.add(new TodayTimings(strDate, "Fajar", todaysData.getFajr(), "0"));
                todayTimingsList.add(new TodayTimings(strDate, "Dhuhr", todaysData.getDhuhr(), "0"));
                todayTimingsList.add(new TodayTimings(strDate, "Asr", todaysData.getAsr(), "0"));
                todayTimingsList.add(new TodayTimings(strDate, "Maghrib", todaysData.getMaghrib(), "0"));
                todayTimingsList.add(new TodayTimings(strDate, "Isha", todaysData.getIsha(), "0"));

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
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                        Integer.parseInt(minute), 00);

                Log.e("else date", cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "");

                Log.e("Next Day Alarm", "Next Alarm Set at : " + index + " - " + Month + " - " + Year + ":" + nextNearestAlarm);
                setAlarm.setAlarm(cal, nextNearestAlarm, context, requestCode);


            } else {
                Log.e("TodaysData", "Empty");
            }
        }
    }*/

}