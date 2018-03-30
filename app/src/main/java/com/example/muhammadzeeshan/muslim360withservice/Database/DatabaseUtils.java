package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.Context;
import android.util.Log;

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

                Log.e("AgentManualCorrection", "Id: " + manualCorrection.getId());
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

    public static void peekAllDataFromNotiType(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<NotiType> notiTypeList = databaseHelper.getAllNotiTypeData();

        if (notiTypeList.size() > 0) {
            for (NotiType notiType : notiTypeList) {

                Log.e("AgentNoti_Type", "Id: " + notiType.getId());
                Log.e("AgentNoti_Type", "Date: " + notiType.getDate());
                Log.e("AgentNoti_Type", "Azan: " + notiType.getAzan());
                Log.e("AgentNoti_Type", "Type: " + notiType.getType());

            }
        } else {
            Log.e("AgentNoti_Type", "Empty");
        }

    }

    public static void peekDataFromTunePath(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String TunePath = databaseHelper.getTunePathData();

        Log.e("AgentTunePath", "Tune_Path: " + TunePath);
    }

    public static void peekAllDataFromTodayTimimgs(Context context) {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<TodayTimings> todayTimingsList = databaseHelper.getAlarmTriggerTime(strDate);

        if (todayTimingsList.size() > 0) {
            for (TodayTimings todayTimings : todayTimingsList) {

                Log.e("AgentTodayTimings", "Id: " + todayTimings.getId());
                Log.e("AgentTodayTimings", "Date: " + todayTimings.getDate());
                Log.e("AgentTodayTimings", "Azan: " + todayTimings.getAzan());
                Log.e("AgentTodayTimings", "ActualTime: " + todayTimings.getActualTime());
                Log.e("AgentTodayTimings", "NotiType: " + todayTimings.getNotiType());
                Log.e("AgentTodayTimings", "TunePath: " + todayTimings.getTunePath());
            }

        } else {
            Log.e("AgentTodayTimings", "Empty");
        }

    }

}
