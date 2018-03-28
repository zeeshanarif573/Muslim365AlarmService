package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.Context;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;

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

    public static void peekAllDataFromTodayTimimgs(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<TodayTimings> todayTimingsList = databaseHelper.getAllTodayTimings();

        if (todayTimingsList.size() > 0) {
            for (TodayTimings todayTimings : todayTimingsList) {

                Log.e("AgentTodayTimings", "Id: " + todayTimings.getId());
                Log.e("AgentTodayTimings", "Date: " + todayTimings.getDate());
                Log.e("AgentTodayTimings", "Azan: " + todayTimings.getAzan());
                Log.e("AgentTodayTimings", "Timing: " + todayTimings.getTiming());
                Log.e("AgentTodayTimings", "Status: " + todayTimings.getStatus());

            }
        } else {
            Log.e("AgentTodayTimings", "Empty");
        }

    }

}
