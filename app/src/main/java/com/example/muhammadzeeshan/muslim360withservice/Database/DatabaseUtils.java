package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.Context;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.ManualCorrection;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class DatabaseUtils {

    public static void peekAllDataFromTimimgs(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<Timings> timingsList = databaseHelper.getAllTimings();

        if (timingsList.size() > 0) {
            for (Timings timings : timingsList) {

                Log.e("AgentTimings", "Id: " + timings.getId());
                Log.e("AgentTimings", "Date: " + timings.getDate());
                Log.e("AgentTimings", "Fajar: " + timings.getFajr());
                Log.e("AgentTimings", "Sunrise: " + timings.getSunrise());
                Log.e("AgentTimings", "Dhuhr: " + timings.getDhuhr());
                Log.e("AgentTimings", "Asr: " + timings.getAsr());
                Log.e("AgentTimings", "Sunset: " + timings.getSunset());
                Log.e("AgentTimings", "Maghrib: " + timings.getMaghrib());
                Log.e("AgentTimings", "Isha: " + timings.getIsha());
                Log.e("AgentTimings", "Imsak: " + timings.getImsak());
                Log.e("AgentTimings", "Midnight: " + timings.getMidnight());

            }
        } else {
            Log.e("AgentTimings", "Empty");
        }

    }

    public static void peekAllDataFromManualCorrection(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<ManualCorrection> manualCorrectionList = databaseHelper.getAllManualCorrectionData();

        if (manualCorrectionList.size() > 0) {
            for (ManualCorrection manualCorrection : manualCorrectionList) {

                Log.e("AgentManualCorrection", "Date: " + manualCorrection.getDate());
                Log.e("AgentManualCorrection", "Azan: " + manualCorrection.getAzan());
                Log.e("AgentManualCorrection", "Timing: " + manualCorrection.getTiming());
            }

        } else {
            Log.e("AgentTodayTimings", "Empty");
        }

    }

    public static void peekDataFromDTS(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String DTS_Value = databaseHelper.getDTSData();

        Log.e("AgentDTS", "DTS_Value: " + DTS_Value);
    }

    public static void peekAllDataFromTodayTimimgs(Context context) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<TodayTimings> todayTimingsList = databaseHelper.getAllTodayTimingData();

        if (todayTimingsList.size() > 0) {
            for (TodayTimings todayTimings : todayTimingsList) {

                Log.e("AgentTodayTimings", "Date: " + todayTimings.getDate());
                Log.e("AgentTodayTimings", "Azan: " + todayTimings.getAzan());
                Log.e("AgentTodayTimings", "Time: " + todayTimings.getActualTime());
                Log.e("AgentTodayTimings", "NotiType: " + todayTimings.getNotiType());
                Log.e("AgentTodayTimings", "TunePath: " + todayTimings.getTunePath());
            }

        } else {
            Log.e("AgentTodayTimings", "Empty");
        }
    }

    public static void peekAllDataFromMainData(Context context) {

        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(strDate);

        if (mainDataList.size() > 0) {
            for (MainData mainData : mainDataList) {

                Log.e("AgentMainData", "Azan: " + mainData.getAzan());
                Log.e("AgentMainData", "Time: " + mainData.getTime());
                Log.e("AgentMainData", "NotiType: " + mainData.getNotiType());
                Log.e("AgentMainData", "TunePath: " + mainData.getTunePath());
            }

        } else {
            Log.e("AgentMainData", "Empty");
        }
    }

}
